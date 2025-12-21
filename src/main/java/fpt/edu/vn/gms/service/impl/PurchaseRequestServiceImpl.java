package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.common.enums.NotificationTemplate;
import fpt.edu.vn.gms.common.enums.NotificationType;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.common.enums.StockLevelStatus;
import fpt.edu.vn.gms.dto.request.CreatePurchaseRequestFromQuotationDto;
import fpt.edu.vn.gms.dto.request.PartItemDto;
import fpt.edu.vn.gms.dto.request.PurchaseRequestCreateDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.StockReceiptDetailResponse;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseSuggestionItemDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.entity.PurchaseRequestQuotation;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.mapper.PurchaseRequestMapper;
import fpt.edu.vn.gms.repository.PriceQuotationItemRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.repository.PurchaseRequestItemRepository;
import fpt.edu.vn.gms.repository.PurchaseRequestRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.repository.PartRepository;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.InventoryService;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.StockReceiptService;
import fpt.edu.vn.gms.specification.PurchaseRequestSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    PriceQuotationRepository priceQuotationRepository;
    PriceQuotationItemRepository priceQuotationItemRepository;
    PurchaseRequestRepository purchaseRequestRepository;
    PurchaseRequestItemRepository purchaseRequestItemRepository;
    InventoryService inventoryService;
    CodeSequenceService codeSequenceService;
    StockReceiptService stockReceiptService;
    PurchaseRequestMapper purchaseRequestMapper;
    PriceQuotationItemMapper priceQuotationItemMapper;
    NotificationService notificationService;
    EmployeeRepository employeeRepository;
    PartRepository partRepository;

    @Transactional
    @Override
    public PurchaseRequest createPurchaseRequestFromQuotation(Long quotationId) {
        PriceQuotation quotation = priceQuotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá ID: " + quotationId));

        List<PurchaseRequestItem> items = new ArrayList<>();
        BigDecimal totalEstimated = BigDecimal.ZERO;

        for (PriceQuotationItem qItem : quotation.getItems()) {
            if (qItem.getItemType() != PriceQuotationItemType.PART)
                continue;
            Part part = qItem.getPart();
            if (part == null)
                continue;

            double required = qItem.getQuantity() + part.getReorderLevel();
            double available = inventoryService.getAvailableQuantity(part.getPartId());
            double reserved = inventoryService.getReservedQuantity(part.getPartId());

            double requiredPurchaseQuantity = (required) - (available - reserved);

            if (requiredPurchaseQuantity <= 0) {
                continue;
            }

            BigDecimal unitPrice = qItem.getUnitPrice() != null ? qItem.getUnitPrice() : part.getPurchasePrice();
            if (unitPrice == null)
                unitPrice = BigDecimal.ZERO;

            BigDecimal estimatedPrice = unitPrice.multiply(BigDecimal.valueOf(requiredPurchaseQuantity));

            PurchaseRequestItem prItem = PurchaseRequestItem.builder()
                    .purchaseRequest(null)
                    .quotationItem(qItem)
                    .part(part)
                    .partName(part.getName())
                    .quantity(requiredPurchaseQuantity)
                    .unit(part.getUnit() != null ? part.getUnit().getName() : null)
                    .estimatedPurchasePrice(estimatedPrice)
                    .reviewStatus(ManagerReviewStatus.PENDING)
                    .quantityReceived(0.0)
                    .build();

            items.add(prItem);
            totalEstimated = totalEstimated.add(estimatedPrice);
        }

        if (items.isEmpty()) {
            throw new RuntimeException("Không cần mua thêm linh kiện — kho còn đủ số lượng.");
        }

        PurchaseRequest pr = PurchaseRequest.builder()
                .code(codeSequenceService.generateCode("PR"))
                .relatedQuotation(quotation)
                .totalEstimatedAmount(totalEstimated)
                .reviewStatus(ManagerReviewStatus.PENDING)
                .reason("Báo giá " + quotation.getCode())
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        purchaseRequestRepository.save(pr);

        for (PurchaseRequestItem item : items) {
            item.setPurchaseRequest(pr);
            purchaseRequestItemRepository.save(item);
            pr.getItems().add(item);
        }

        return pr;
    }

    @Transactional
    @Override
    public PurchaseRequest createRequest(PurchaseRequestCreateDto dto) {
        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Danh sách linh kiện mua hàng không được để trống");
        }

        List<PurchaseRequestItem> items = new ArrayList<>();
        BigDecimal totalEstimated = BigDecimal.ZERO;

        for (PartItemDto itemDto : dto.getItems()) {
            if (itemDto.getPartId() == null) {
                continue;
            }

            Part part = partRepository.findById(itemDto.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy linh kiện ID: " + itemDto.getPartId()));

            double qty = Optional.ofNullable(itemDto.getQuantity()).orElse(0.0);
            if (qty <= 0) {
                continue;
            }

            BigDecimal unitPrice = Optional.ofNullable(part.getPurchasePrice()).orElse(BigDecimal.ZERO);
            BigDecimal estimatedPrice = unitPrice.multiply(BigDecimal.valueOf(qty));

            PurchaseRequestItem prItem = PurchaseRequestItem.builder()
                    .purchaseRequest(null)
                    .quotationItem(null)
                    .part(part)
                    .partName(part.getName())
                    .quantity(qty)
                    .unit(part.getUnit() != null ? part.getUnit().getName() : null)
                    .estimatedPurchasePrice(unitPrice)
                    .reviewStatus(ManagerReviewStatus.PENDING)
                    .quantityReceived(0.0)
                    .build();

            items.add(prItem);
            totalEstimated = totalEstimated.add(estimatedPrice);
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Không có dòng mua hàng hợp lệ để tạo phiếu yêu cầu mua hàng");
        }

        PurchaseRequest pr = PurchaseRequest.builder()
                .code(codeSequenceService.generateCode("PR"))
                .relatedQuotation(null)
                .totalEstimatedAmount(totalEstimated)
                .reviewStatus(ManagerReviewStatus.PENDING)
                .reason(dto.getReason() != null ? dto.getReason() : "Yêu cầu mua hàng từ kho")
                .items(new ArrayList<>())
                .createdBy(dto.getCreatedById())
                .createdAt(LocalDateTime.now())
                .build();

        purchaseRequestRepository.save(pr);

        for (PurchaseRequestItem item : items) {
            item.setPurchaseRequest(pr);
            purchaseRequestItemRepository.save(item);
            pr.getItems().add(item);
        }

        return pr;
    }

    @Transactional
    @Override
    public PurchaseRequest approvePurchaseRequest(Long requestId) {
        PurchaseRequest pr = purchaseRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu mua hàng"));

        if (pr.getItems() == null || pr.getItems().isEmpty()) {
            throw new RuntimeException("Phiếu yêu cầu mua hàng không có item");
        }

        pr.setReviewStatus(ManagerReviewStatus.APPROVED);
        purchaseRequestRepository.save(pr);

        StockReceiptDetailResponse receipt = stockReceiptService.createReceiptFromPurchaseRequest(pr.getId());
        log.info("Created stock receipt {} from purchase request {}", receipt.getId(), pr.getId());

        // Gửi notification cho người tạo phiếu (nếu có) và toàn bộ nhân viên role
        NotificationTemplate template = NotificationTemplate.PURCHASE_REQUEST_CONFIRMED;
        String formattedTitle = String.format(template.getTitle(), pr.getCode());

        Set<Long> receiverIds = new LinkedHashSet<>();
        if (pr.getCreatedBy() != null) {
            receiverIds.add(pr.getCreatedBy());
        }
        employeeRepository.findByRole(Role.WAREHOUSE)
                .forEach(emp -> receiverIds.add(emp.getEmployeeId()));

        for (Long receiverId : receiverIds) {
            notificationService.createNotification(
                    receiverId,
                    formattedTitle,
                    template.format(pr.getCode()),
                    NotificationType.PURCHASE_REQUEST,
                    pr.getId().toString(),
                    "/warehouse/purchase-requests/" + receipt.getId());
        }

        return pr;
    }

    @Transactional
    @Override
    public PurchaseRequest rejectPurchaseRequest(Long requestId, String reason) {
        PurchaseRequest pr = purchaseRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu mua hàng"));

        pr.setReviewStatus(ManagerReviewStatus.REJECTED);
        if (reason != null) {
            pr.setReason(reason);
        }
        purchaseRequestRepository.save(pr);

        NotificationTemplate template = NotificationTemplate.PURCHASE_REQUEST_REJECTED;
        String formattedTitle = String.format(template.getTitle(), pr.getCode());

        Set<Long> receiverIds = new LinkedHashSet<>();
        if (pr.getCreatedBy() != null) {
            receiverIds.add(pr.getCreatedBy());
        }
        if (pr.getRelatedQuotation() != null && pr.getRelatedQuotation().getCreatedBy() != null) {
            receiverIds.add(pr.getRelatedQuotation().getCreatedBy());
        }
        employeeRepository.findByRole(Role.WAREHOUSE)
                .forEach(emp -> receiverIds.add(emp.getEmployeeId()));

        for (Long receiverId : receiverIds) {
            notificationService.createNotification(
                    receiverId,
                    formattedTitle,
                    template.format(pr.getCode()),
                    NotificationType.PURCHASE_REQUEST,
                    pr.getId().toString(),
                    "/warehouse/purchase-requests/" + pr.getId());
        }

        return pr;
    }

    @Override
    public Page<PurchaseRequestResponseDto> getPurchaseRequests(String keyword, String status, String fromDate,
            String toDate, Pageable pageable) {
        Page<PurchaseRequest> page = purchaseRequestRepository.findAll(
                PurchaseRequestSpecification.build(keyword, status, fromDate, toDate), pageable);
        return page.map(purchaseRequestMapper::toListDto);
    }

    @Override
    public PurchaseRequestDetailDto getPurchaseRequestDetail(Long id) {
        PurchaseRequest pr = purchaseRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu mua hàng"));
        return purchaseRequestMapper.toDetailDto(pr);
    }

    @Override
    public List<PurchaseSuggestionItemDto> getSuggestedPurchaseItems() {
        List<Part> parts = partRepository.findByStatusIn(
                List.of(StockLevelStatus.OUT_OF_STOCK, StockLevelStatus.LOW_STOCK));

        List<PurchaseSuggestionItemDto> result = new ArrayList<>();

        for (Part part : parts) {
            double inStock = Optional.ofNullable(part.getQuantityInStock()).orElse(0.0);
            double reserved = Optional.ofNullable(part.getReservedQuantity()).orElse(0.0);
            double reorder = Optional.ofNullable(part.getReorderLevel()).orElse(0.0);

            double available = inStock - reserved;
            double needed = reorder - available;

            // OUT_OF_STOCK luôn hiển thị, LOW_STOCK chỉ hiển thị khi needed > 0
            if (part.getStatus() != StockLevelStatus.OUT_OF_STOCK && needed <= 0) {
                continue;
            }

            // Nếu OUT_OF_STOCK và needed <= 0 (do reorderLevel = 0), set needed = 1 hoặc giá trị mặc định
            if (part.getStatus() == StockLevelStatus.OUT_OF_STOCK && needed <= 0) {
                needed = Math.max(1.0, Math.abs(available) + 1);
            }

            PurchaseSuggestionItemDto dto = PurchaseSuggestionItemDto.builder()
                    .partId(part.getPartId())
                    .sku(part.getSku())
                    .partName(part.getName())
                    .unit(part.getUnit() != null ? part.getUnit().getName() : null)
                    .quantityInStock(inStock)
                    .reservedQuantity(reserved)
                    .reorderLevel(reorder)
                    .available(available)
                    .suggestedQuantity(needed)
                    .build();

            result.add(dto);
        }

        return result;
    }

    @Transactional
    @Override
    public PurchaseRequest createFromQuotation(CreatePurchaseRequestFromQuotationDto dto, Employee currentEmployee) {
        // 1. Validate quotations
        if (dto.getQuotationIds() == null || dto.getQuotationIds().isEmpty()) {
            throw new IllegalArgumentException("Danh sách báo giá không được để trống");
        }

        List<PriceQuotation> quotations = priceQuotationRepository.findAllById(dto.getQuotationIds());
        if (quotations.size() != dto.getQuotationIds().size()) {
            throw new ResourceNotFoundException("Một số báo giá không tồn tại");
        }

        // Validate tất cả quotations đều ở trạng thái CUSTOMER_CONFIRMED
        boolean allConfirmed = quotations.stream()
                .allMatch(q -> q.getStatus() == PriceQuotationStatus.CUSTOMER_CONFIRMED);
        if (!allConfirmed) {
            throw new IllegalArgumentException("Tất cả báo giá phải ở trạng thái khách hàng đã xác nhận");
        }

        // 2. Validate items thuộc các quotations này
        if (dto.getQuotationItemIds() == null || dto.getQuotationItemIds().isEmpty()) {
            throw new IllegalArgumentException("Danh sách items không được để trống");
        }

        List<PriceQuotationItem> selectedItems = priceQuotationItemRepository.findAllById(dto.getQuotationItemIds());

        if (selectedItems.size() != dto.getQuotationItemIds().size()) {
            throw new IllegalArgumentException("Một số items không tồn tại");
        }

        // Kiểm tra tất cả items phải thuộc một trong các quotations đã chọn
        Set<Long> quotationIdsSet = quotations.stream()
                .map(PriceQuotation::getPriceQuotationId)
                .collect(Collectors.toSet());

        boolean allItemsBelongToQuotations = selectedItems.stream()
                .allMatch(item -> quotationIdsSet.contains(item.getPriceQuotation().getPriceQuotationId()));

        if (!allItemsBelongToQuotations) {
            throw new IllegalArgumentException("Một số items không thuộc các báo giá đã chọn");
        }

        // 3. Tạo PurchaseRequest
        String reasonText = dto.getReason() != null ? dto.getReason()
                : "Từ " + quotations.size() + " báo giá: " + quotations.stream()
                        .map(PriceQuotation::getCode)
                        .collect(Collectors.joining(", "));

        PurchaseRequest pr = PurchaseRequest.builder()
                .reason(reasonText)
                .code(codeSequenceService.generateCode("PR"))
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>())
                .quotations(new ArrayList<>())
                .createdBy(currentEmployee.getEmployeeId())
                .createdAt(LocalDateTime.now())
                .build();

        // 4. Tạo PurchaseRequestQuotation relationships
        List<PurchaseRequestQuotation> prQuotations = quotations.stream()
                .map(quotation -> PurchaseRequestQuotation.builder()
                        .purchaseRequest(pr)
                        .priceQuotation(quotation)
                        .build())
                .collect(Collectors.toList());
        pr.setQuotations(prQuotations);

        // 5. Tạo PurchaseRequestItems từ selectedItems
        BigDecimal totalEstimated = BigDecimal.ZERO;
        List<PurchaseRequestItem> prItems = new ArrayList<>();

        for (PriceQuotationItem item : selectedItems) {
            if (item.getItemType() != PriceQuotationItemType.PART) {
                continue; // Chỉ lấy items là PART
            }

            Part part = item.getPart();
            if (part == null) {
                continue;
            }

            BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice()
                    : (part.getPurchasePrice() != null ? part.getPurchasePrice() : BigDecimal.ZERO);

            BigDecimal estimatedPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            totalEstimated = totalEstimated.add(estimatedPrice);

            PurchaseRequestItem prItem = PurchaseRequestItem.builder()
                    .purchaseRequest(pr)
                    .quotationItem(item) // Mapping với PriceQuotationItem
                    .part(part)
                    .partName(item.getItemName() != null ? item.getItemName() : part.getName())
                    .quantity(item.getQuantity())
                    .unit(item.getUnit() != null ? item.getUnit()
                            : (part.getUnit() != null ? part.getUnit().getName() : null))
                    .estimatedPurchasePrice(unitPrice)
                    .reviewStatus(ManagerReviewStatus.PENDING)
                    .quantityReceived(0.0)
                    .note(dto.getNote())
                    .build();

            prItems.add(prItem);
        }

        if (prItems.isEmpty()) {
            throw new IllegalArgumentException("Không có items hợp lệ để tạo yêu cầu mua hàng");
        }

        pr.setItems(prItems);
        pr.setTotalEstimatedAmount(totalEstimated);

        // 6. Save (cascade sẽ tự động save items và quotations)
        return purchaseRequestRepository.save(pr);
    }

    @Override
    public List<PriceQuotationItemResponseDto> getQuotationItemsByPurchaseRequest(Long purchaseRequestId) {
        PurchaseRequest pr = purchaseRequestRepository.findById(purchaseRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy yêu cầu mua hàng với ID: " + purchaseRequestId));

        // Sử dụng relationship hiện có qua PurchaseRequestItem
        List<PriceQuotationItem> quotationItems = purchaseRequestItemRepository
                .findQuotationItemsByPurchaseRequestId(purchaseRequestId);

        return quotationItems.stream()
                .map(priceQuotationItemMapper::toResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }
}
