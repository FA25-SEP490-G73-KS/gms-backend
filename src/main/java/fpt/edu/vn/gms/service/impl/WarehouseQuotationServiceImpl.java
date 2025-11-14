package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.*;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseQuotationServiceImpl implements WarehouseQuotationService {

    private final PriceQuotationRepository quotationRepository;
    private final PriceQuotationMapper priceQuotationMapper;
    private final PriceQuotationItemMapper priceQuotationItemMapper;
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

    private void checkAllItemsAndSendNotification(PriceQuotation quotation) {

        List<PriceQuotationItem> partItems = quotation.getItems().stream()
                .filter(i -> i.getItemType() == PriceQuotationItemType.PART)
                .toList();

        boolean allChecked = partItems.stream()
                .allMatch(i -> i.getWarehouseReviewStatus() == WarehouseReviewStatus.CONFIRMED
                        || i.getWarehouseReviewStatus() == WarehouseReviewStatus.REJECTED);

        if (!allChecked) return; // vẫn còn pending → không gửi gì

        Employee advisor = quotation.getServiceTicket().getCreatedBy();
        if (advisor == null) return;

        if (partItems.stream().anyMatch(i -> i.getWarehouseReviewStatus() == WarehouseReviewStatus.REJECTED)) {
            // Có ít nhất một item reject → báo giá bị kho từ chối
            quotation.setStatus(PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM);
            quotationRepository.save(quotation);

            // Dùng NotificationTemplate
            NotificationTemplate template = NotificationTemplate.PRICE_QUOTATION_REJECTED;

            notificationService.createNotification(
                    advisor.getEmployeeId(),
                    template.getTitle(),
                    template.format(quotation.getPriceQuotationId()),
                    NotificationType.QUOTATION_REJECTED,
                    quotation.getPriceQuotationId().toString(),
                    "/service-tickets/" + quotation.getServiceTicket().getServiceTicketId()
            );

        } else {
            // Tất cả item confirmed → báo giá được kho xác nhận
            quotation.setStatus(PriceQuotationStatus.WAREHOUSE_CONFIRMED);
            quotationRepository.save(quotation);

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


