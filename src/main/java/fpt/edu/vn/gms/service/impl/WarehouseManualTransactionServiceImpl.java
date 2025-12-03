package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ExportItemStatus;
import fpt.edu.vn.gms.common.enums.ExportStatus;
import fpt.edu.vn.gms.common.enums.StockReceiptStatus;
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
    StockReceiptRepository stockReceiptRepository;
    SupplierRepository supplierRepository;
    CodeSequenceService codeSequenceService;
    WarehouseManualTransactionMapper mapper;

    private static final BigDecimal TOTAL_PRICE_TOLERANCE = new BigDecimal("1"); // cho phép lệch 1 VND

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
        partRepository.save(part);
    }

    private void applyReceiptToStock(Part part, double qty) {
        double inStock = part.getQuantityInStock() == null ? 0.0 : part.getQuantityInStock();
        part.setQuantityInStock(inStock + qty);
        partRepository.save(part);
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
        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp ID=" + request.getSupplierId()));
        }

        StockReceipt receipt = StockReceipt.builder()
                .code(codeSequenceService.generateCode("XK"))
                .supplier(supplier)
                .createdBy(request.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .status(isDraft ? StockReceiptStatus.DRAFT : StockReceiptStatus.RECEIVED)
                .totalAmount(BigDecimal.ZERO)
                .note(request.getNote())
                .build();

        List<StockReceiptItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

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

            total = total.add(lineTotal);

            StockReceiptItem item = StockReceiptItem.builder()
                    .stockReceipt(receipt)
                    .purchaseRequestItem(null)
                    .requestedQuantity(qty)
                    .quantityReceived(isDraft ? 0.0 : qty)
                    .actualUnitPrice(unitPrice)
                    .actualTotalPrice(lineTotal)
                    .note(ir.getNote())
                    .status(isDraft ? StockReceiptStatus.DRAFT : StockReceiptStatus.RECEIVED)
                    .build();
            items.add(item);

            if (!isDraft) {
                applyReceiptToStock(part, qty);
            }
        }

        receipt.setItems(items);
        receipt.setTotalAmount(total);
        StockReceipt saved = stockReceiptRepository.save(receipt);

        List<ManualTransactionItemResponse> itemDtos = new ArrayList<>();
        for (int i = 0; i < saved.getItems().size(); i++) {
            StockReceiptItem item = saved.getItems().get(i);
            ManualTransactionItemRequest reqItem = request.getItems().get(i);
            Part part = partRepository.findById(reqItem.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy linh kiện ID=" + reqItem.getPartId()));
            itemDtos.add(mapper.toResponseFromReceiptItem(item, part));
        }

        return ManualTransactionResponse.builder()
                .id(saved.getReceiptId())
                .code(saved.getCode())
                .type("RECEIPT")
                .isDraft(isDraft)
                .status(saved.getStatus().name())
                .createdBy(saved.getCreatedBy())
                .createdAt(saved.getCreatedAt())
                .note(saved.getNote())
                .items(itemDtos)
                .totalAmount(saved.getTotalAmount())
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
