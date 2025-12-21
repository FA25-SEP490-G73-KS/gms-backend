package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ExportItemStatus;
import fpt.edu.vn.gms.common.enums.ExportStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.common.enums.StockLevelStatus;
import fpt.edu.vn.gms.common.enums.StockReceiptStatus;
import fpt.edu.vn.gms.dto.request.CreateReceiptItemHistoryRequest;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.StockReceiptItemHistoryMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.StockReceiptService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StockReceiptServiceImplNew implements StockReceiptService {

    StockReceiptRepository stockReceiptRepository;
    StockReceiptItemRepository stockReceiptItemRepository;
    StockReceiptItemHistoryRepository stockReceiptItemHistoryRepository;
    PurchaseRequestRepository purchaseRequestRepository;
    CodeSequenceService codeSequenceService;
    StockReceiptItemHistoryMapper historyMapper;
    StockExportItemRepository stockExportItemRepository; // thêm repository để cập nhật trạng thái xuất kho
    StockExportRepository stockExportRepository; // thêm repository để tạo/cập nhật StockExport
    PartRepository partRepository; // thêm repository để cập nhật tồn kho linh kiện
    ServiceTicketRepository serviceTicketRepository;
    PriceQuotationItemRepository priceQuotationItemRepository;
    FileStorageService fileStorageService;

    @Override
    public Page<StockReceiptListResponse> getReceipts(String status, String keyword, String fromDate, String toDate,
            Long supplierId, Pageable pageable) {
        Page<StockReceipt> page = stockReceiptRepository.findAll(pageable);

        List<StockReceipt> filtered = page.getContent().stream()
                .filter(r -> {
                    if (status == null || status.isBlank())
                        return true;
                    return r.getStatus() != null && r.getStatus().name().equalsIgnoreCase(status);
                })
                .filter(r -> {
                    if (keyword == null || keyword.isBlank())
                        return true;
                    String lower = keyword.toLowerCase();
                    boolean matchCode = r.getCode() != null && r.getCode().toLowerCase().contains(lower);
                    boolean matchPr = r.getPurchaseRequest() != null && r.getPurchaseRequest().getCode() != null
                            && r.getPurchaseRequest().getCode().toLowerCase().contains(lower);
                    return matchCode || matchPr;
                })
                .filter(r -> supplierFilter(r, supplierId))
                .filter(r -> filterByDateRange(r, fromDate, toDate))
                .toList();

        List<StockReceiptListResponse> dtos = filtered.stream()
                .map(this::toListDto)
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    private boolean supplierFilter(StockReceipt r, Long supplierId) {
        if (supplierId == null)
            return true;
        return r.getSupplier() != null && r.getSupplier().getId().equals(supplierId);
    }

    private boolean filterByDateRange(StockReceipt r, String fromDate, String toDate) {
        if ((fromDate == null || fromDate.isBlank()) && (toDate == null || toDate.isBlank())) {
            return true;
        }
        LocalDate createdDate = Optional.ofNullable(r.getCreatedAt())
                .map(LocalDateTime::toLocalDate)
                .orElse(null);
        if (createdDate == null)
            return false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (fromDate != null && !fromDate.isBlank()) {
            LocalDate from = LocalDate.parse(fromDate, formatter);
            if (createdDate.isBefore(from))
                return false;
        }
        if (toDate != null && !toDate.isBlank()) {
            LocalDate to = LocalDate.parse(toDate, formatter);
            if (createdDate.isAfter(to))
                return false;
        }
        return true;
    }

    private StockReceiptListResponse toListDto(StockReceipt r) {
        double totalRequested = r.getItems().stream()
                .mapToDouble(i -> Optional.ofNullable(i.getRequestedQuantity()).orElse(0.0))
                .sum();
        double totalReceived = r.getItems().stream()
                .mapToDouble(i -> Optional.ofNullable(i.getQuantityReceived()).orElse(0.0))
                .sum();

        return StockReceiptListResponse.builder()
                .id(r.getReceiptId())
                .code(r.getCode())
                .supplierName(r.getSupplier() != null ? r.getSupplier().getName() : null)
                .purchaseRequestCode(r.getPurchaseRequest() != null ? r.getPurchaseRequest().getCode() : null)
                .lineCount((long) r.getItems().size())
                .receivedQty(totalReceived)
                .totalQty(totalRequested)
                .createdAt(r.getCreatedAt())
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .build();
    }

    @Override
    public StockReceiptDetailResponse getReceiptDetail(Long id) {
        StockReceipt r = stockReceiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập kho"));

        List<StockReceiptItemResponse> items = r.getItems().stream()
                .map(this::toItemDto)
                .toList();

        return StockReceiptDetailResponse.builder()
                .id(r.getReceiptId())
                .code(r.getCode())
                .supplierName(r.getSupplier() != null ? r.getSupplier().getName() : null)
                .purchaseRequestCode(r.getPurchaseRequest() != null ? r.getPurchaseRequest().getCode() : null)
                .createdBy(r.getCreatedBy())
                .createdAt(r.getCreatedAt())
                .receivedBy(r.getReceivedBy())
                .receivedAt(r.getReceivedAt())
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .totalAmount(r.getTotalAmount())
                .note(r.getNote())
                .items(items)
                .build();
    }

    @Override
    public Page<StockReceiptItemResponse> getReceiptItems(Long receiptId, Pageable pageable) {
        StockReceipt r = stockReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập kho"));

        List<StockReceiptItemResponse> items = r.getItems().stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());

        return new PageImpl<>(items, pageable, items.size());
    }

    private StockReceiptItemResponse toItemDto(StockReceiptItem item) {
        Part part = item.getPurchaseRequestItem() != null ? item.getPurchaseRequestItem().getPart() : null;
        return StockReceiptItemResponse.builder()
                .id(item.getId())
                .partCode(part != null ? part.getSku() : null)
                .partName(item.getPurchaseRequestItem() != null ? item.getPurchaseRequestItem().getPartName() : null)
                .requestedQty(item.getRequestedQuantity())
                .receivedQty(item.getQuantityReceived())
                .unitPrice(item.getActualUnitPrice())
                .totalPrice(item.getActualTotalPrice())
                .status(item.getStatus() != null ? item.getStatus().name() : null)
                .build();
    }

    @Override
    public StockReceiptItemDetailResponse getReceiptItemDetail(Long itemId) {
        StockReceiptItem item = stockReceiptItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng nhập kho"));
        List<StockReceiptItemHistory> histories = stockReceiptItemHistoryRepository.findByStockReceiptItem_Id(itemId);
        return historyMapper.toDetailDto(item, histories);
    }

    @Override
    public StockReceiptItemDetailResponse getReceiptItemHistory(Long itemId) {
        return getReceiptItemDetail(itemId);
    }

    @Transactional
    @Override
    public StockReceiptItemDetailResponse createReceiptItemHistory(Long itemId,
            CreateReceiptItemHistoryRequest request, MultipartFile file) {
        StockReceiptItem item = stockReceiptItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng nhập kho"));

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Số lượng nhận phải > 0");
        }

        double alreadyReceived = Optional.ofNullable(item.getQuantityReceived()).orElse(0.0);
        double remaining = Optional.ofNullable(item.getRequestedQuantity()).orElse(0.0) - alreadyReceived;

        if (request.getQuantity() > remaining) {
            throw new IllegalArgumentException("Số lượng nhận vượt quá số lượng yêu cầu");
        }

        // Upload file nếu có
        String attachmentUrl = null;
        if (file != null && !file.isEmpty()) {
            attachmentUrl = fileStorageService.upload(file);
        }

        // Tạo history
        StockReceiptItemHistory history = StockReceiptItemHistory.builder()
                .stockReceiptItem(item)
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .attachmentUrl(attachmentUrl)
                .note(request.getNote())
                .receivedBy(request.getReceivedBy())
                .build();
        stockReceiptItemHistoryRepository.save(history);

        // Cập nhật item
        double newReceived = alreadyReceived + request.getQuantity();
        item.setQuantityReceived(newReceived);
        item.setActualUnitPrice(request.getUnitPrice());
        item.setActualTotalPrice(request.getUnitPrice().multiply(BigDecimal.valueOf(newReceived)));
        item.setReceivedAt(LocalDateTime.now());
        item.setReceivedBy(request.getReceivedBy());

        // Cập nhật tồn kho linh kiện (Part.quantityInStock += quantity nhận lần này)
        PurchaseRequestItem prItem = item.getPurchaseRequestItem();
        if (prItem != null && prItem.getPart() != null) {
            Part part = prItem.getPart();
            Double currentStock = Optional.ofNullable(part.getQuantityInStock()).orElse(0.0);
            part.setQuantityInStock(currentStock + request.getQuantity());
            updateStockLevelStatus(part);
            partRepository.save(part);

            // Cập nhật PriceQuotationItem tương ứng từ PurchaseRequestItem
            if (prItem.getQuotationItem() != null) {
                PriceQuotationItem quotationItem = prItem.getQuotationItem();

                // Cập nhật inventoryStatus dựa trên số lượng tồn kho hiện tại
                double itemQuantity = Optional.ofNullable(quotationItem.getQuantity()).orElse(0.0);
                double reservedQty = Optional.ofNullable(quotationItem.getReservedQuantity()).orElse(0.0);
                double availableStock = part.getQuantityInStock() - part.getReservedQuantity();

                PriceQuotationItemStatus oldStatus = quotationItem.getInventoryStatus();

                // Nếu số lượng tồn kho khả dụng >= số lượng cần của quotation item
                if (availableStock >= (itemQuantity - reservedQty) && availableStock >= 0) {
                    quotationItem.setInventoryStatus(PriceQuotationItemStatus.AVAILABLE);
                } else if (availableStock < 0) {
                    quotationItem.setInventoryStatus(PriceQuotationItemStatus.OUT_OF_STOCK);
                }

                priceQuotationItemRepository.save(quotationItem);

                // Nếu inventoryStatus chuyển thành AVAILABLE, tạo hoặc cập nhật StockExport
                if (quotationItem.getInventoryStatus() == PriceQuotationItemStatus.AVAILABLE
                        && oldStatus != PriceQuotationItemStatus.AVAILABLE) {
                    PriceQuotation quotation = quotationItem.getPriceQuotation();
                    if (quotation != null) {
                        // Tìm StockExport của quotation này
                        Optional<StockExport> existingExport = stockExportRepository.findByQuotationId(
                                quotation.getPriceQuotationId());

                        StockExport stockExport;
                        if (existingExport.isPresent()) {
                            stockExport = existingExport.get();

                            // Kiểm tra xem đã có StockExportItem cho quotationItem này chưa
                            boolean itemExists = stockExport.getExportItems().stream()
                                    .anyMatch(exportItem -> exportItem.getQuotationItem() != null
                                            && exportItem.getQuotationItem().getPriceQuotationItemId()
                                                    .equals(quotationItem.getPriceQuotationItemId()));

                            // Nếu chưa có thì tạo mới StockExportItem
                            if (!itemExists && quotationItem.getPart() != null) {
                                StockExportItem exportItem = StockExportItem.builder()
                                        .stockExport(stockExport)
                                        .quotationItem(quotationItem)
                                        .part(quotationItem.getPart())
                                        .quantity(quotationItem.getQuantity())
                                        .quantityExported(0.0)
                                        .status(ExportItemStatus.EXPORTING)
                                        .build();
                                stockExport.getExportItems().add(exportItem);
                                stockExportRepository.save(stockExport);
                            }
                        } else {
                            // Tạo StockExport mới
                            stockExport = StockExport.builder()
                                    .code(codeSequenceService.generateCode("XK"))
                                    .quotation(quotation)
                                    .reason("Tự động tạo khi nhập kho")
                                    .status(ExportStatus.WAITING_TO_EXECUTE)
                                    .createdBy(request.getReceivedBy())
                                    .build();

                            // Tạo StockExportItem cho quotationItem này
                            if (quotationItem.getPart() != null) {
                                StockExportItem exportItem = StockExportItem.builder()
                                        .stockExport(stockExport)
                                        .quotationItem(quotationItem)
                                        .part(quotationItem.getPart())
                                        .quantity(quotationItem.getQuantity())
                                        .quantityExported(0.0)
                                        .status(ExportItemStatus.EXPORTING)
                                        .build();
                                List<StockExportItem> exportItems = new ArrayList<>();
                                exportItems.add(exportItem);
                                stockExport.setExportItems(exportItems);
                            }

                            stockExportRepository.save(stockExport);
                        }
                    }
                }
            }

            // Tự động duyệt các price quotation item sau khi nhập kho (cho các tickets
            // khác)
            autoUpdateQuotationItemsAfterReceipt(part);
        }

        // Update status item
        if (newReceived == 0) {
            item.setStatus(StockReceiptStatus.PENDING);
        } else if (newReceived < Optional.ofNullable(item.getRequestedQuantity()).orElse(0.0)) {
            item.setStatus(StockReceiptStatus.PARTIAL_RECEIVED);
        } else {
            item.setStatus(StockReceiptStatus.RECEIVED);
        }

        stockReceiptItemRepository.save(item);

        // Auto update receipt
        updateReceiptStatusAndTotal(item.getStockReceipt());

        List<StockReceiptItemHistory> histories = stockReceiptItemHistoryRepository.findByStockReceiptItem_Id(itemId);
        return historyMapper.toDetailDto(item, histories);
    }

    @Transactional
    @Override
    public StockReceiptDetailResponse cancelReceipt(Long id) {
        StockReceipt r = stockReceiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập kho"));
        r.setStatus(StockReceiptStatus.PENDING);
        stockReceiptRepository.save(r);
        return getReceiptDetail(id);
    }

    @Transactional
    @Override
    public StockReceiptDetailResponse completeReceipt(Long id) {
        StockReceipt r = stockReceiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập kho"));
        updateReceiptStatusAndTotal(r);
        return getReceiptDetail(id);
    }

    @Transactional
    @Override
    public StockReceiptDetailResponse createReceiptFromPurchaseRequest(Long purchaseRequestId) {
        PurchaseRequest pr = purchaseRequestRepository.findById(purchaseRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu mua hàng"));

        // Kiểm tra xem đã có StockReceipt cho PurchaseRequest này chưa
        Optional<StockReceipt> existingReceipt = stockReceiptRepository.findByPurchaseRequest(pr);
        if (existingReceipt.isPresent()) {
            // Nếu đã có, trả về receipt hiện tại để tránh duplicate
            log.info("Đã tồn tại phiếu nhập kho cho yêu cầu mua hàng {}, trả về phiếu hiện có", purchaseRequestId);
            return getReceiptDetail(existingReceipt.get().getReceiptId());
        }

        if (pr.getItems() == null || pr.getItems().isEmpty()) {
            throw new RuntimeException("Phiếu yêu cầu mua hàng không có item");
        }

        Supplier supplier = null;
        for (PurchaseRequestItem item : pr.getItems()) {
            if (item.getPart() != null && item.getPart().getSupplier() != null) {
                supplier = item.getPart().getSupplier();
                break;
            }
        }

        StockReceipt receipt = StockReceipt.builder()
                .code(codeSequenceService.generateCode("NK"))
                .purchaseRequest(pr)
                .supplier(supplier)
                .createdBy(pr.getCreatedBy() != null ? pr.getCreatedBy().toString() : null)
                .createdAt(LocalDateTime.now())
                .status(StockReceiptStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .note(pr.getReason())
                .build();

        stockReceiptRepository.save(receipt);

        for (PurchaseRequestItem prItem : pr.getItems()) {
            StockReceiptItem item = StockReceiptItem.builder()
                    .stockReceipt(receipt)
                    .purchaseRequestItem(prItem)
                    .requestedQuantity(prItem.getQuantity())
                    .quantityReceived(0.0)
                    .actualUnitPrice(prItem.getEstimatedPurchasePrice())
                    .actualTotalPrice(
                            prItem.getEstimatedPurchasePrice().multiply(BigDecimal.valueOf(prItem.getQuantity())))
                    .status(StockReceiptStatus.PENDING)
                    .build();
            stockReceiptItemRepository.save(item);
            receipt.getItems().add(item);
        }

        updateReceiptStatusAndTotal(receipt);

        return getReceiptDetail(receipt.getReceiptId());
    }

    private void updateReceiptStatusAndTotal(StockReceipt receipt) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (StockReceiptItem item : receipt.getItems()) {
            if (item.getActualTotalPrice() != null) {
                totalAmount = totalAmount.add(item.getActualTotalPrice());
            }
        }

        receipt.setTotalAmount(totalAmount);

        boolean allReceived = receipt.getItems().stream()
                .allMatch(i -> i.getStatus() == StockReceiptStatus.RECEIVED);
        boolean anyPartial = receipt.getItems().stream()
                .anyMatch(i -> i.getStatus() == StockReceiptStatus.PARTIAL_RECEIVED);

        if (allReceived) {
            receipt.setStatus(StockReceiptStatus.RECEIVED);
        } else if (anyPartial) {
            receipt.setStatus(StockReceiptStatus.PARTIAL_RECEIVED);
        } else {
            receipt.setStatus(StockReceiptStatus.PENDING);
        }

        stockReceiptRepository.save(receipt);

        // Sau khi cập nhật phiếu nhập, nếu đã nhận đủ một số linh kiện
        // thì chuyển các StockExportItem tương ứng từ WAITING_TO_RECEIPT sang EXPORTING
        try {
            receipt.getItems().forEach(item -> {
                PurchaseRequestItem prItem = item.getPurchaseRequestItem();
                if (prItem == null || prItem.getPart() == null) {
                    return; // không có link tới Part thì bỏ qua
                }

                Part part = prItem.getPart();

                // Ở đây giả sử: nếu quantityReceived >= requestedQuantity thì coi là đủ.
                Double requested = Optional.ofNullable(item.getRequestedQuantity()).orElse(0.0);
                Double received = Optional.ofNullable(item.getQuantityReceived()).orElse(0.0);
                if (requested <= 0 || received < requested) {
                    return; // chưa nhận đủ thì chưa kích hoạt xuất
                }

                // Tìm các dòng xuất kho đang chờ nhập kho cho linh kiện này
                List<StockExportItem> waitingExportItems = stockExportItemRepository
                        .findByPartAndStatus(part, ExportItemStatus.WAITING_TO_RECEIPT);

                if (waitingExportItems.isEmpty()) {
                    return;
                }

                waitingExportItems.forEach(exportItem -> {
                    exportItem.setStatus(ExportItemStatus.EXPORTING);
                });

                stockExportItemRepository.saveAll(waitingExportItems);
            });
        } catch (Exception ex) {
            log.error("Lỗi khi cập nhật trạng thái StockExportItem sau khi nhập kho: {}", ex.getMessage(), ex);
        }
    }

    private void updateStockLevelStatus(Part part) {
        if (part == null)
            return;

        double inStock = Optional.ofNullable(part.getQuantityInStock()).orElse(0.0);
        double reserved = Optional.ofNullable(part.getReservedQuantity()).orElse(0.0);
        double threshold = Optional.ofNullable(part.getReorderLevel()).orElse(0.0);

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

    private void autoUpdateQuotationItemsAfterReceipt(Part part) {
        if (part == null) {
            return;
        }

        try {
            // 1. Tìm các ServiceTicket đang ở trạng thái UNDER_REPAIR, sắp xếp theo
            // createdAt ASC (oldest first)
            List<ServiceTicket> tickets = serviceTicketRepository.findByStatus(
                    ServiceTicketStatus.UNDER_REPAIR,
                    PageRequest.of(0, Integer.MAX_VALUE, Sort.by("createdAt").ascending())).getContent();

            if (tickets.isEmpty()) {
                return;
            }

            // 2. Lấy quantityInStock và reservedQuantity của part
            double quantityInStock = Optional.ofNullable(part.getQuantityInStock()).orElse(0.0);
            double reservedQuantity = Optional.ofNullable(part.getReservedQuantity()).orElse(0.0);

            // 3. Duyệt từng ticket (từ oldest đến newest)
            for (ServiceTicket ticket : tickets) {
                PriceQuotation quotation = ticket.getPriceQuotation();
                if (quotation == null) {
                    continue;
                }

                // 4. Duyệt các PriceQuotationItem có partId trùng với part vừa nhập
                List<PriceQuotationItem> items = priceQuotationItemRepository.findAllByPriceQuotation(quotation);

                for (PriceQuotationItem item : items) {
                    // Chỉ xử lý item có part trùng với part vừa nhập
                    if (item.getPart() == null || !item.getPart().getPartId().equals(part.getPartId())) {
                        continue;
                    }

                    // Chỉ xử lý item là PART type
                    if (item.getItemType() != PriceQuotationItemType.PART) {
                        continue;
                    }

                    // 5. Kiểm tra điều kiện: quantityInStock > (item.getQuantity() -
                    // reservedQuantity) && quantityInStock > 0
                    double itemQuantity = Optional.ofNullable(item.getQuantity()).orElse(0.0);
                    double requiredAvailable = itemQuantity - reservedQuantity;

                    if (quantityInStock > requiredAvailable && quantityInStock > 0) {
                        // 6. Cập nhật inventoryStatus = AVAILABLE
                        if (item.getInventoryStatus() != PriceQuotationItemStatus.AVAILABLE) {
                            item.setInventoryStatus(PriceQuotationItemStatus.AVAILABLE);
                            priceQuotationItemRepository.save(item);
                            log.info(
                                    "Đã cập nhật inventoryStatus = AVAILABLE cho PriceQuotationItem {} (partId: {}, ticketId: {})",
                                    item.getPriceQuotationItemId(), part.getPartId(), ticket.getServiceTicketId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi tự động cập nhật PriceQuotationItem sau khi nhập kho (partId: {}): {}",
                    part.getPartId(), e.getMessage(), e);
        }
    }

    @Override
    public StockReceiptItemHistoryDetailResponse getReceiptItemHistoryDetail(Long historyId) {
        StockReceiptItemHistory history = stockReceiptItemHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch sử nhập kho"));

        return StockReceiptItemHistoryDetailResponse.builder()
                .attachmentUrl(history.getAttachmentUrl())
                .build();
    }
}
