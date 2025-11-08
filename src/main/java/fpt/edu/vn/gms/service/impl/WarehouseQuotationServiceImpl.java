package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.NotificationType;
import fpt.edu.vn.gms.common.PriceQuotationItemType;
import fpt.edu.vn.gms.common.PriceQuotationStatus;
import fpt.edu.vn.gms.common.WarehouseReviewStatus;
import fpt.edu.vn.gms.dto.request.WarehouseReviewItemDto;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.repository.PriceQuotationItemRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.WarehouseQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseQuotationServiceImpl implements WarehouseQuotationService {

    private final PriceQuotationRepository quotationRepository;
    private final PriceQuotationMapper priceQuotationMapper;
    private final PriceQuotationItemMapper priceQuotationItemMapper;
    private final NotificationSocketService notificationSocketService;
    private final NotificationService notificationService;

    @Override
    public List<PriceQuotationResponseDto> getPendingQuotations() {

        // Lấy các báo giá đang chờ kho xác nhận
        List<PriceQuotation> quotations = quotationRepository
                .findByStatus(PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM);

        // Chuyển sang DTO, nhưng chỉ giữ lại item có type = PART
        return quotations.stream()
                .map(quotation -> {
                    var dto = priceQuotationMapper.toResponseDto(quotation);
                    if (dto.getItems() != null) {
                        dto.setItems(dto.getItems().stream()
                                .filter(item -> item.getItemType() == PriceQuotationItemType.PART)
                                .toList());
                    }
                    return dto;
                })
                .toList();
    }


    @Override
    public PriceQuotationItemResponseDto updateWarehouseReview(Long quotationId, WarehouseReviewItemDto dto) {

        PriceQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá ID: " + quotationId));

        if (quotation.getStatus() != PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM) {
            throw new RuntimeException("Báo giá không ở trạng thái chờ kho xác nhận");
        }

        // Tìm item cần duyệt
        PriceQuotationItem item = quotation.getItems().stream()
                .filter(i -> i.getPriceQuotationItemId().equals(dto.getItemId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy item ID: " + dto.getItemId()));

        if (item.getItemType() != PriceQuotationItemType.PART) {
            throw new RuntimeException("Chỉ có thể duyệt các item loại linh kiện (PART)");
        }

        // Cập nhật giá nếu có thay đổi
        if (dto.getUnitPrice() != null) {
            item.setUnitPrice(dto.getUnitPrice());
            item.setTotalPrice(dto.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // Ghi chú và trạng thái duyệt
        item.setWarehouseNote(dto.getWarehouseNote());
        item.setWarehouseReviewStatus(dto.isConfirmed()
                ? WarehouseReviewStatus.CONFIRMED
                : WarehouseReviewStatus.REJECTED);

        quotation.setUpdatedAt(LocalDateTime.now());
        quotationRepository.save(quotation);

        // --- Kiểm tra tất cả item và gửi WebSocket ---
        checkAllItemsAndSendNotification(quotation);

        return priceQuotationItemMapper.toResponseDto(item);
    }

    /**
     * Kiểm tra tất cả PART item, gửi WebSocket + notification
     */
    private void checkAllItemsAndSendNotification(PriceQuotation quotation) {

        List<PriceQuotationItem> partItems = quotation.getItems().stream()
                .filter(i -> i.getItemType() == PriceQuotationItemType.PART)
                .toList();

        boolean allChecked = partItems.stream()
                .allMatch(i -> i.getWarehouseReviewStatus() == WarehouseReviewStatus.CONFIRMED
                        || i.getWarehouseReviewStatus() == WarehouseReviewStatus.REJECTED);

        if (!allChecked) return; // vẫn còn pending → không gửi gì

        boolean hasRejected = partItems.stream()
                .anyMatch(i -> i.getWarehouseReviewStatus() == WarehouseReviewStatus.REJECTED);

        if (hasRejected) {
            // Có ít nhất một item reject → báo giá bị kho từ chối
            quotation.setStatus(PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM);
            quotationRepository.save(quotation);

            String advisorPhone = quotation.getServiceTicket().getCreatedBy().getPhone();
            NotificationResponseDto wsNotification = NotificationResponseDto.builder()
                    .title("Kho đã từ chối một số linh kiện")
                    .message(String.format("Kho đã từ chối phiếu dịch vụ #%s", quotation.getServiceTicket().getServiceTicketCode()))
                    .type(NotificationType.QUOTATION_CONFIRMED)
                    .code(quotation.getServiceTicket().getServiceTicketCode())
                    .build();

            notificationSocketService.sendToAdvisor(advisorPhone, wsNotification);

            notificationService.createNotification(
                    advisorPhone,
                    wsNotification.getTitle(),
                    wsNotification.getMessage(),
                    wsNotification.getType(),
                    wsNotification.getCode()
            );

        } else {
            // Tất cả item confirmed → báo giá được kho xác nhận
            quotation.setStatus(PriceQuotationStatus.WAREHOUSE_CONFIRMED);
            quotationRepository.save(quotation);

            String advisorPhone = quotation.getServiceTicket().getCreatedBy().getPhone();

            NotificationResponseDto wsNotification = NotificationResponseDto.builder()
                    .title("Kho đã xác nhận tất cả linh kiện")
                    .message(String.format("Kho đã từ chối phiếu dịch vụ #%s", quotation.getServiceTicket().getServiceTicketCode()))
                    .type(NotificationType.QUOTATION_CONFIRMED)
                    .code(quotation.getServiceTicket().getServiceTicketCode())
                    .build();

            notificationSocketService.sendToAdvisor(advisorPhone, wsNotification);

            notificationService.createNotification(
                    advisorPhone,
                    wsNotification.getTitle(),
                    wsNotification.getMessage(),
                    wsNotification.getType(),
                    wsNotification.getCode()
            );
        }
    }

}


