package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.common.enums.NotificationTemplate;
import fpt.edu.vn.gms.common.enums.NotificationType;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.response.StockReceiptDetailResponse;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PurchaseRequestMapper;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.repository.PurchaseRequestItemRepository;
import fpt.edu.vn.gms.repository.PurchaseRequestRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    PriceQuotationRepository priceQuotationRepository;
    PurchaseRequestRepository purchaseRequestRepository;
    PurchaseRequestItemRepository purchaseRequestItemRepository;
    InventoryService inventoryService;
    CodeSequenceService codeSequenceService;
    StockReceiptService stockReceiptService;
    PurchaseRequestMapper purchaseRequestMapper;
    NotificationService notificationService;
    EmployeeRepository employeeRepository;

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
                    "/stock-receipt/" + receipt.getId());
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
                    "/purchase-requests/" + pr.getId());
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
}
