package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.*;
import fpt.edu.vn.gms.dto.request.WarehouseReviewItemDto;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.repository.PartRepository;
import fpt.edu.vn.gms.repository.PriceQuotationItemRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.WarehouseQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final PartRepository partRepository;

    @Override
    public Page<PriceQuotationResponseDto> getPendingQuotations(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());

        // Lấy báo giá đang chờ kho xác nhận (có phân trang)
        Page<PriceQuotation> quotations = quotationRepository
                .findByStatus(PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM, pageable);

        // Chuyển sang DTO và lọc item PART
        return quotations.map(quotation -> {
            var dto = priceQuotationMapper.toResponseDto(quotation);
            if (dto.getItems() != null) {
                dto.setItems(dto.getItems().stream()
                        .filter(item -> item.getItemType() == PriceQuotationItemType.PART && item.getInventoryStatus() == PriceQuotationItemStatus.UNKNOWN)
                        .toList());
            }
            return dto;
        });
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

        // Lấy part tương ứng với partId
        Part part = partRepository.findById(dto.getPartId()).orElseThrow();

        // Ghi chú và trạng thái duyệt
        item.setPart(part);
        item.setItemName(dto.getPartName());
        item.setUnitPrice(dto.getSellingPrice());
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
                    .type(NotificationType.QUOTATION_REJECTED)
                    .build();

            notificationSocketService.sendToAdvisor(advisorPhone, wsNotification);

            notificationService.createNotification(
                    advisorPhone,
                    wsNotification.getTitle(),
                    wsNotification.getMessage(),
                    wsNotification.getType()
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
                    .build();

            notificationSocketService.sendToAdvisor(advisorPhone, wsNotification);

            notificationService.createNotification(
                    advisorPhone,
                    wsNotification.getTitle(),
                    wsNotification.getMessage(),
                    wsNotification.getType()
            );
        }
    }

}


