package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.NotificationTemplate;
import fpt.edu.vn.gms.common.enums.NotificationType;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.common.enums.WarehouseReviewStatus;
import fpt.edu.vn.gms.dto.request.PartUpdateReqDto;
import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PartMapper;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.PartService;
import fpt.edu.vn.gms.service.WarehouseQuotationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WarehouseQuotationServiceImpl implements WarehouseQuotationService {

    NotificationService notificationService;
    PartService partService;
    PartRepository partRepository;
    PriceQuotationRepository quotationRepository;
    PriceQuotationItemRepository priceQuotationItemRepo;
    PriceQuotationItemMapper priceQuotationItemMapper;
    PriceQuotationMapper priceQuotationMapper;


    @Override
    public Page<PriceQuotationResponseDto> getPendingQuotations(int page, int size) {

        log.info("Fetching pending quotations — page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());

        Page<PriceQuotation> quotations = quotationRepository
                .findByStatus(PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM, pageable);

        log.info("Found {} quotations waiting for warehouse approval", quotations.getTotalElements());

        return quotations.map(quotation -> {
            var dto = priceQuotationMapper.toResponseDto(quotation);

            if (dto.getItems() != null) {

                dto.setItems(
                        dto.getItems().stream()
                                .filter(item ->
                                        item.getItemType() == PriceQuotationItemType.PART &&
                                                (item.getInventoryStatus() == PriceQuotationItemStatus.UNKNOWN
                                                        || item.getInventoryStatus() == PriceQuotationItemStatus.OUT_OF_STOCK)
                                )
                                .toList()
                );
            }

            return dto;
        });
    }

    @Transactional
    public PriceQuotationItemResponseDto confirmItemDuringWarehouseReview(Long itemId, String warehouseNote) {

        log.info("Confirming item during warehouse review — itemId={} note={}", itemId, warehouseNote);

        PriceQuotationItem item = priceQuotationItemRepo.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item {} not found when confirming", itemId);
                    return new ResourceNotFoundException("Không tìm thấy item báo giá");
                });

        Part part = item.getPart();
        if (part == null) {
            log.error("Item {} cannot be confirmed because it has no Part assigned", itemId);
            throw new IllegalStateException("Item chưa gắn Part — không thể xác nhận");
        }

        // ===== 1. Ghi chú kho nếu có =====
        if (warehouseNote != null) {
            item.setWarehouseNote(warehouseNote);
        }

        // ===== 2. Set trạng thái kho xác nhận =====
        item.setWarehouseReviewStatus(WarehouseReviewStatus.CONFIRMED);

        // ===== 3. Kiểm tra tồn kho =====
        double availableQty =
                Optional.ofNullable(part.getQuantityInStock()).orElse(0.0)
                        - Optional.ofNullable(part.getReservedQuantity()).orElse(0.0);

        if (availableQty >= item.getQuantity()) {
            item.setInventoryStatus(PriceQuotationItemStatus.AVAILABLE);
        } else {
            item.setInventoryStatus(PriceQuotationItemStatus.OUT_OF_STOCK);
        }

        // ===== 4. Cập nhật lại item =====
        priceQuotationItemRepo.save(item);

        log.info("Item {} confirmed — status={}, inventory={}",
                itemId, item.getWarehouseReviewStatus(), item.getInventoryStatus());

        // ===== 5. Cập nhật tổng báo giá =====
        recalculateQuotationTotal(item.getPriceQuotation());

        // ===== 6. Kiểm tra tất cả item (để gửi notification nếu cần) =====
        checkAllItemsAndSendNotification(item.getPriceQuotation());

        // ===== 7. Trả về DTO =====
        return priceQuotationItemMapper.toResponseDto(item);
    }


    @Transactional
    public PriceQuotationItemResponseDto rejectItemDuringWarehouseReview(Long itemId, String warehouseNote) {

        log.info("Rejecting item during warehouse review — itemId={} note={}", itemId, warehouseNote);

        PriceQuotationItem item = priceQuotationItemRepo.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item {} not found when rejecting", itemId);
                    return new ResourceNotFoundException("Không tìm thấy item báo giá");
                });

        // Cập nhật warehouse note
        item.setWarehouseNote(warehouseNote);

        // Chỉ reject, không đụng các trường khác
        item.setWarehouseReviewStatus(WarehouseReviewStatus.REJECTED);

        priceQuotationItemRepo.save(item);

        log.info("Item {} rejected successfully — note={}", itemId, warehouseNote);

        // GỌI HÀM CHECK ĐỂ GỬI NOTIFICATION
        checkAllItemsAndSendNotification(item.getPriceQuotation());

        return priceQuotationItemMapper.toResponseDto(item);
    }


    private void checkAllItemsAndSendNotification(PriceQuotation quotation) {

        log.info("Checking all PART items for quotation {}", quotation.getPriceQuotationId());

        List<PriceQuotationItem> partItems = quotation.getItems().stream()
                .filter(i -> i.getItemType() == PriceQuotationItemType.PART)
                .toList();

        boolean allChecked = partItems.stream()
                .allMatch(i ->
                        i.getWarehouseReviewStatus() == WarehouseReviewStatus.CONFIRMED ||
                                i.getWarehouseReviewStatus() == WarehouseReviewStatus.REJECTED
                );

        log.info("All PART items reviewed = {}", allChecked);

        if (!allChecked) {
            log.info("Quotation {} still has PENDING items", quotation.getPriceQuotationId());
            return;
        }

        Employee advisor = quotation.getServiceTicket().getCreatedBy();

        // CASE REJECT
        if (partItems.stream().anyMatch(i ->
                i.getWarehouseReviewStatus() == WarehouseReviewStatus.REJECTED)) {

            log.warn("Quotation {} REJECTED by warehouse", quotation.getPriceQuotationId());

            quotation.setStatus(PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM);
            quotationRepository.save(quotation);

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

        // CASE CONFIRMED ALL
        log.info("Quotation {} APPROVED by warehouse", quotation.getPriceQuotationId());

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

    @Transactional
    public void updatePartDuringWarehouseReview(Long itemId, PartUpdateReqDto dto) {

        log.info("Updating part during warehouse review — itemId={} dto={}", itemId, dto);

        PriceQuotationItem item = priceQuotationItemRepo.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item {} not found when updating part", itemId);
                    return new ResourceNotFoundException("Không tìm thấy item báo giá");
                });

        Part part = item.getPart();
        if (part == null) {
            log.error("Item {} does not have a Part attached", itemId);
            throw new IllegalStateException("Item chưa gắn Part — không thể cập nhật");
        }

        log.info("Updating Part {} (before merge)", part.getPartId());

        // --- Tính giá bán ---
        BigDecimal purchase = dto.getPurchasePrice();
        BigDecimal selling = dto.getSellingPrice();

        // Update part info
        PartReqDto savedPart = partService.updatePart(part.getPartId(), dto);

        log.info("Part {} updated successfully", savedPart.getPartId());

        // Merge back into item
        log.info("Merging updated Part {} into Item {}", savedPart.getPartId(), itemId);

        item.setItemName(savedPart.getName());
        item.setUnitPrice(savedPart.getSellingPrice());
        item.setTotalPrice(savedPart.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())));


        if (dto.getNote() != null) {
            item.setWarehouseNote(dto.getNote());
        }

        item.setWarehouseReviewStatus(WarehouseReviewStatus.CONFIRMED);

        double availableQty = Optional.ofNullable(savedPart.getQuantity()).orElse(0.0)
                - Optional.ofNullable(savedPart.getReservedQuantity()).orElse(0.0);

        item.setInventoryStatus(availableQty >= item.getQuantity()
                ? PriceQuotationItemStatus.AVAILABLE
                : PriceQuotationItemStatus.OUT_OF_STOCK
        );

        priceQuotationItemRepo.save(item);

        log.info("Item {} merge complete — status={}, inventory={}",
                itemId, item.getWarehouseReviewStatus(), item.getInventoryStatus());

        // TÍNH LẠI TỔNG BÁO GIÁ
        recalculateQuotationTotal(item.getPriceQuotation());

        // GỬI NOTIFICATION
        checkAllItemsAndSendNotification(item.getPriceQuotation());
    }

    @Transactional
    public PriceQuotationItemResponseDto createPartDuringWarehouseReview(Long itemId, PartUpdateReqDto dto) {

        log.info("Creating NEW part for UNKNOWN item — itemId={} dto={}", itemId, dto);

        PriceQuotationItem item = priceQuotationItemRepo.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item {} not found", itemId);
                    return new ResourceNotFoundException("Không tìm thấy item báo giá");
                });

        if (item.getPart() != null) {
            log.warn("Item {} already has Part {}, cannot create new Part", itemId, item.getPart().getPartId());
            throw new IllegalStateException("Item đã có Part — không thể tạo Part mới");
        }

        PartReqDto savedPart = partService.createPart(dto);

        log.info("Created new Part id={} for item {}", savedPart.getPartId(), itemId);

        Part part = partRepository.findById(savedPart.getPartId())
                .orElseThrow(() -> new ResourceNotFoundException("Linh kiện chưa được tạo!"));

        // ===== MERGE NEW PART INTO ITEM =====
        item.setPart(part);
        item.setItemName(savedPart.getName());
        item.setUnit(part.getUnit().getName());
        item.setUnitPrice(savedPart.getSellingPrice());
        item.setTotalPrice(savedPart.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        // cập nhật ghi chú kho
        if (dto.getNote() != null) {
            item.setWarehouseNote(dto.getNote());
        }

        // vì item UNKNOWN nên khi tạo part → set status = CONFIRMED
        item.setWarehouseReviewStatus(WarehouseReviewStatus.CONFIRMED);

        // tồn kho = 0 → chắc chắn OUT_OF_STOCK
        item.setInventoryStatus(PriceQuotationItemStatus.OUT_OF_STOCK);

        priceQuotationItemRepo.save(item);

        log.info("Item {} updated with new Part {} — status={}, inventory={}",
                itemId, savedPart.getPartId(),
                item.getWarehouseReviewStatus(),
                item.getInventoryStatus());

        // TÍNH LẠI TỔNG BÁO GIÁ
        recalculateQuotationTotal(item.getPriceQuotation());

        // GỬI NOTIFICATION
        checkAllItemsAndSendNotification(item.getPriceQuotation());

        return priceQuotationItemMapper.toResponseDto(item);
    }

    private void recalculateQuotationTotal(PriceQuotation quotation) {

        BigDecimal newTotal = quotation.getItems().stream()
                .map(i -> Optional.ofNullable(i.getTotalPrice()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        quotation.setEstimateAmount(newTotal);
        quotation.setUpdatedAt(LocalDateTime.now());

        quotationRepository.save(quotation);

        log.info("Recalculated quotation {} new total: {}",
                quotation.getPriceQuotationId(), newTotal);
    }

}



