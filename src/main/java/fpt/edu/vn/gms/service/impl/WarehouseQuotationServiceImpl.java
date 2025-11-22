package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.NotificationTemplate;
import fpt.edu.vn.gms.common.enums.NotificationType;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.common.enums.WarehouseReviewStatus;
import fpt.edu.vn.gms.dto.request.WarehouseReviewItemDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.repository.PartRepository;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseQuotationServiceImpl implements WarehouseQuotationService {

    private final PartRepository partRepository;
    private final NotificationService notificationService;
    private final PriceQuotationRepository quotationRepository;
    private final PriceQuotationItemMapper priceQuotationItemMapper;
    private final PriceQuotationMapper priceQuotationMapper;

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
                        .filter(item -> item.getItemType() == PriceQuotationItemType.PART && item.getInventoryStatus() == PriceQuotationItemStatus.UNKNOWN || item.getInventoryStatus() == PriceQuotationItemStatus.OUT_OF_STOCK)
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

        Part part = partRepository.findById(dto.getPartId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy linh kiện ID: " + dto.getPartId()));

        item.setPart(part);
        item.setUnitPrice(part.getSellingPrice());

        // Tính tổng tiền: unitPrice * quantity
        double qty = Optional.ofNullable(item.getQuantity()).orElse(0.0);
        BigDecimal totalPrice = part.getSellingPrice().multiply(BigDecimal.valueOf(qty));
        item.setTotalPrice(totalPrice);

        item.setWarehouseNote(dto.getWarehouseNote());
        item.setWarehouseReviewStatus(dto.isConfirmed()
                ? WarehouseReviewStatus.CONFIRMED
                : WarehouseReviewStatus.REJECTED);

        quotationRepository.saveAndFlush(quotation);

        // --- Kiểm tra tất cả item và gửi WebSocket ---
        checkAllItemsAndSendNotification(quotation);

        return priceQuotationItemMapper.toResponseDto(item);
    }

    private void checkAllItemsAndSendNotification(PriceQuotation quotation) {

        List<PriceQuotationItem> partItems = quotation.getItems().stream()
                .filter(i -> i.getItemType() == PriceQuotationItemType.PART)
                .toList();

        boolean allChecked = partItems.stream()
                .allMatch(i -> i.getWarehouseReviewStatus() == WarehouseReviewStatus.CONFIRMED
                        || i.getWarehouseReviewStatus() == WarehouseReviewStatus.REJECTED);

        System.out.println("allChecked = " + allChecked); // Debug

        if (!allChecked) return; // vẫn còn pending → không gửi gì

        Employee advisor = quotation.getServiceTicket().getCreatedBy();

        if (partItems.stream().anyMatch(i -> i.getWarehouseReviewStatus() == WarehouseReviewStatus.REJECTED)) {
            // Có ít nhất một item reject → báo giá bị kho từ chối
            quotation.setStatus(PriceQuotationStatus.WAREHOUSE_CONFIRMED);
            quotationRepository.save(quotation);

            // Dùng NotificationTemplate
            if (advisor != null) {
                NotificationTemplate template = NotificationTemplate.PRICE_QUOTATION_REJECTED;

                notificationService.createNotification(
                        advisor.getEmployeeId(),
                        template.getTitle(),
                        template.format(quotation.getPriceQuotationId()),
                        NotificationType.QUOTATION_REJECTED,
                        quotation.getPriceQuotationId().toString(),
                        "/service-tickets/" + quotation.getServiceTicket().getServiceTicketId()
                );
            }


            return;
        }
            // Tất cả item confirmed → báo giá được kho xác nhận
            quotation.setStatus(PriceQuotationStatus.WAREHOUSE_CONFIRMED);
            quotationRepository.save(quotation);

            if (advisor != null) {
                NotificationTemplate template = NotificationTemplate.PRICE_QUOTATION_APPROVED;

                notificationService.createNotification(
                        advisor.getEmployeeId(),
                        template.getTitle(),
                        template.format(quotation.getPriceQuotationId()),
                        NotificationType.QUOTATION_CONFIRMED,
                        quotation.getPriceQuotationId().toString(),
                        "/service-tickets/" + quotation.getServiceTicket().getServiceTicketId()
                );
            }
    }
}


