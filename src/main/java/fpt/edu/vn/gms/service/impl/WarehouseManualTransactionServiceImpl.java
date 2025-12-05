package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ExportItemStatus;
import fpt.edu.vn.gms.common.enums.ExportStatus;
import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.common.enums.DeductionType;
import fpt.edu.vn.gms.common.enums.StockLevelStatus;
import fpt.edu.vn.gms.dto.request.ManualTransactionItemRequest;
import fpt.edu.vn.gms.dto.request.ManualTransactionRequest;
import fpt.edu.vn.gms.dto.response.ManualTransactionItemResponse;
import fpt.edu.vn.gms.dto.response.ManualTransactionResponse;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.WarehouseManualTransactionMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.WarehouseManualTransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseManualTransactionServiceImpl implements WarehouseManualTransactionService {

    PartRepository partRepository;
    StockExportRepository stockExportRepository;
    SupplierRepository supplierRepository;
    PurchaseRequestRepository purchaseRequestRepository;
    DeductionRepository deductionRepository;
    EmployeeRepository employeeRepository;
    CodeSequenceService codeSequenceService;
    WarehouseManualTransactionMapper mapper;

    private static final BigDecimal TOTAL_PRICE_TOLERANCE = new BigDecimal("1");

    @Transactional
    @Override
    public ManualTransactionResponse createManualTransaction(ManualTransactionRequest request) {
        validateRequest(request);

        boolean isExport = "EXPORT".equalsIgnoreCase(request.getType());
        boolean isDraft = Boolean.FALSE;

        return isExport ? handleExport(request, isDraft) : handleReceipt(request, isDraft);
    }

    private void validateStockForExport(Part part, double qty) {
        double inStock = part.getQuantityInStock() == null ? 0.0 : part.getQuantityInStock();
        double reserved = part.getReservedQuantity() == null ? 0.0 : part.getReservedQuantity();
        double available = inStock - reserved;
        if (available < qty) {
            throw new IllegalArgumentException("Không đủ tồn kho cho linh kiện ID=" + part.getPartId());
        }
    }

    private void applyExportToStock(Part part, double qty) {
        double inStock = part.getQuantityInStock() == null ? 0.0 : part.getQuantityInStock();
        double reserved = part.getReservedQuantity() == null ? 0.0 : part.getReservedQuantity();
        part.setQuantityInStock(inStock - qty);
        part.setReservedQuantity(Math.max(0.0, reserved - qty));
        updateStockLevelStatus(part);
        partRepository.save(part);
    }

    private void applyReceiptToStock(Part part, double qty) {
        double inStock = part.getQuantityInStock() == null ? 0.0 : part.getQuantityInStock();
        part.setQuantityInStock(inStock + qty);
        updateStockLevelStatus(part);
        partRepository.save(part);
    }

    /**
     * Cập nhật trạng thái tồn kho linh kiện dựa trên tồn khả dụng.
     * available = quantityInStock - reservedQuantity
     * - available <= 0                 => OUT_OF_STOCK
     * - 0 < available <= reorderLevel  => LOW_STOCK
     * - available > reorderLevel       => IN_STOCK
     */
    private void updateStockLevelStatus(Part part) {
        if (part == null) return;

        double inStock = part.getQuantityInStock() == null ? 0.0 : part.getQuantityInStock();
        double reserved = part.getReservedQuantity() == null ? 0.0 : part.getReservedQuantity();
        double threshold = part.getReorderLevel() == null ? 0.0 : part.getReorderLevel();

        double available = inStock - reserved;

        StockLevelStatus newStatus;
        if (available <= 0) {
            newStatus = StockLevelStatus.OUT_OF_STOCK;
        } else if (available <= threshold) {
            newStatus = StockLevelStatus.LOW_STOCK;
        } else {
            newStatus = StockLevelStatus.IN_STOCK;
        }

        if (part.getStatus() != newStatus) {
            part.setStatus(newStatus);
        }
    }

    private ManualTransactionResponse handleExport(ManualTransactionRequest request, boolean isDraft) {
        StockExport export = StockExport.builder()
                .code(codeSequenceService.generateCode("EX"))
                .reason(request.getReason())
                .createdBy(request.getCreatedBy())
                .status(isDraft ? ExportStatus.DRAFT : ExportStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        List<StockExportItem> items = new ArrayList<>();

        for (ManualTransactionItemRequest ir : request.getItems()) {
            Part part = partRepository.findById(ir.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy linh kiện ID=" + ir.getPartId()));

            double qty = ir.getQuantity() == null ? 0.0 : ir.getQuantity();
            if (!isDraft) {
                validateStockForExport(part, qty);
                applyExportToStock(part, qty);
            }

            StockExportItem item = StockExportItem.builder()
                    .stockExport(export)
                    .part(part)
                    .quantity(qty)
                    .quantityExported(isDraft ? 0.0 : qty)
                    .status(isDraft ? ExportItemStatus.EXPORTING : ExportItemStatus.FINISHED)
                    .note(ir.getNote())
                    .build();
            items.add(item);
        }

        export.setExportItems(items);
        StockExport saved = stockExportRepository.save(export);

        if (request.getReason() != null
                && request.getReason().toLowerCase().contains("do nhân viên")
                && request.getReceiverId() != null
                && !items.isEmpty()) {

            Employee employee = employeeRepository.findById(request.getReceiverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên nhận hàng"));

            StockExportItem firstItem = items.get(0);
            Part firstPart = firstItem.getPart();
            BigDecimal unitCost = firstPart.getPurchasePrice() != null ? firstPart.getPurchasePrice() : BigDecimal.ZERO;
            BigDecimal deductionAmount = unitCost.multiply(BigDecimal.valueOf(firstItem.getQuantity()));

            if (deductionAmount.compareTo(BigDecimal.ZERO) > 0) {
                Deduction deduction = Deduction.builder()
                        .employee(employee)
                        .type(DeductionType.DAMAGE)
                        .amount(deductionAmount)
                        .reason(saved.getReason())
                        .date(java.time.LocalDate.now())
                        .createdBy(saved.getCreatedBy())
                        .build();
                deductionRepository.save(deduction);
            }
        }

        List<ManualTransactionItemResponse> itemDtos = saved.getExportItems().stream()
                .map(mapper::toResponseFromExportItem)
                .toList();

        return ManualTransactionResponse.builder()
                .id(saved.getId())
                .code(saved.getCode())
                .type("EXPORT")
                .isDraft(isDraft)
                .status(saved.getStatus().name())
                .createdBy(saved.getCreatedBy())
                .createdAt(saved.getCreatedAt())
                .note(saved.getReason())
                .items(itemDtos)
                .totalAmount(null)
                .build();
    }

    private ManualTransactionResponse handleReceipt(ManualTransactionRequest request, boolean isDraft) {
        // Tạo PurchaseRequest với trạng thái reviewStatus = PENDING
        PurchaseRequest purchaseRequest = PurchaseRequest.builder()
                .code(codeSequenceService.generateCode("PR"))
                .relatedQuotation(null)
                .stockReceipt(null)
                .totalEstimatedAmount(BigDecimal.ZERO)
                .reviewStatus(ManagerReviewStatus.PENDING)
                .reason(request.getReason())
                .items(new ArrayList<>())
                .createdBy(null)
                .build();

        BigDecimal totalEstimated = BigDecimal.ZERO;
        List<ManualTransactionItemResponse> itemDtos = new ArrayList<>();

        for (ManualTransactionItemRequest ir : request.getItems()) {
            Part part = partRepository.findById(ir.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy linh kiện ID=" + ir.getPartId()));

            double qty = ir.getQuantity() == null ? 0.0 : ir.getQuantity();
            BigDecimal unitPrice = ir.getPrice() == null ? BigDecimal.ZERO : ir.getPrice();

            BigDecimal expectedTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            BigDecimal lineTotal = ir.getTotalPrice();

            if (lineTotal != null) {
                // Kiểm tra chênh lệch giữa FE gửi và giá * số lượng
                BigDecimal diff = lineTotal.subtract(expectedTotal).abs();
                if (diff.compareTo(TOTAL_PRICE_TOLERANCE) > 0) {
                    throw new RuntimeException(
                            String.format("Tổng tiền dòng không khớp: totalPrice=%.0f, price*qty=%.0f",
                                    lineTotal, expectedTotal));
                }
            } else {
                // Nếu FE không gửi, tự tính
                lineTotal = expectedTotal;
            }

            totalEstimated = totalEstimated.add(lineTotal);

            // Tạo PurchaseRequestItem tương ứng
            PurchaseRequestItem prItem = PurchaseRequestItem.builder()
                    .purchaseRequest(purchaseRequest)
                    .quotationItem(null)
                    .part(part)
                    .partName(part.getName())
                    .quantity(qty)
                    .unit(part.getUnit() != null ? part.getUnit().getName() : null)
                    .estimatedPurchasePrice(unitPrice)
                    .quantityReceived(0.0)
                    .reviewStatus(ManagerReviewStatus.PENDING)
                    .note(ir.getNote())
                    .createdBy(null)
                    .build();
            purchaseRequest.getItems().add(prItem);

            ManualTransactionItemResponse res = new ManualTransactionItemResponse();
            res.setId(null);
            res.setPartId(part.getPartId());
            res.setPartSku(part.getSku());
            res.setPartName(part.getName());
            res.setQuantity(qty);
            res.setUnit(part.getUnit() != null ? part.getUnit().getName() : null);
            res.setUnitPrice(unitPrice);
            res.setTotalPrice(lineTotal);
            res.setQuantityInStock(part.getQuantityInStock());
            res.setReservedQuantity(part.getReservedQuantity());
            res.setNote(ir.getNote());
            itemDtos.add(res);
        }

        purchaseRequest.setTotalEstimatedAmount(totalEstimated);
        PurchaseRequest savedPr = purchaseRequestRepository.save(purchaseRequest);

        return ManualTransactionResponse.builder()
                .id(savedPr.getId())
                .code(savedPr.getCode())
                .type("RECEIPT")
                .isDraft(isDraft)
                .status(savedPr.getReviewStatus().name())
                .createdBy(request.getCreatedBy())
                .createdAt(savedPr.getCreatedAt())
                .note(savedPr.getReason())
                .items(itemDtos)
                .totalAmount(totalEstimated)
                .build();
    }

    private void validateRequest(ManualTransactionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request không được null");
        }
        if (request.getType() == null || request.getType().isBlank()) {
            throw new IllegalArgumentException("type là bắt buộc (EXPORT | RECEIPT)");
        }
        if (!"EXPORT".equalsIgnoreCase(request.getType()) && !"RECEIPT".equalsIgnoreCase(request.getType())) {
            throw new IllegalArgumentException("type phải là EXPORT hoặc RECEIPT");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Danh sách items không được rỗng");
        }
        // Validate từng item
        for (ManualTransactionItemRequest item : request.getItems()) {
            if (item.getPartId() == null) {
                throw new IllegalArgumentException("Mỗi item phải có partId");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Số lượng phải > 0");
            }
            // Với RECEIPT, kiểm tra price >= 0
            if ("RECEIPT".equalsIgnoreCase(request.getType())) {
                if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Đơn giá phải >= 0");
                }
            }
        }
    }
}
