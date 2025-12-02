package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.*;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.request.StockReceiveRequest;
import fpt.edu.vn.gms.dto.response.StockReceiptItemResponseDto;
import fpt.edu.vn.gms.dto.response.StockReceiptResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.StockReceiptItemMapper;
import fpt.edu.vn.gms.mapper.StockReceiptMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.StockReceiptService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StockReceiptServiceImpl implements StockReceiptService {

    StockReceiptRepository stockReceiptRepo;
    StockReceiptItemRepository stockReceiptItemRepo;
    PurchaseRequestRepository purchaseRequestRepo;
    PurchaseRequestItemRepository purchaseRequestItemRepo;
    PartRepository partRepository;
    PriceQuotationItemRepository quotationItemRepo;
    AccountRepository accountRepository;
    NotificationService notificationService;
    CodeSequenceService codeSequenceService;
    FileStorageService fileStorageService;
    StockReceiptItemMapper stockReceiptItemMapper;
    StockReceiptMapper stockReceiptMapper;

    @Transactional
    @Override
    public StockReceiptItemResponseDto receiveItem(
            Long prItemId,
            StockReceiveRequest request,
            MultipartFile file,
            Employee employee
    ) {

        log.info("[RECEIVE-ITEM] prItemId={} qtyReceived={} byEmployee={}",
                prItemId, request.getQuantityReceived(), employee.getEmployeeId());

        PurchaseRequestItem prItem = loadPurchaseRequestItem(prItemId);
        PurchaseRequest pr = prItem.getPurchaseRequest();

        validateReceivedQuantity(prItem, request);

        String fileUrl = fileStorageService.upload(file);

        StockReceipt receipt = loadOrCreateReceipt(pr, employee);
        StockReceiptItem receiptItem = createReceiptItem(prItem, request, fileUrl, employee, receipt);

        updatePartStock(prItem, request);
        updatePurchaseRequestItem(prItem, request);
        updatePurchaseRequestStatus(pr);

        updateQuotationItemInventory(prItem);

        sendNotificationToAdvisor(prItem, pr);
        sendNotificationToAccountant(prItem, pr, receiptItem);

        return stockReceiptItemMapper.toDto(receiptItem);
    }

    @Override
    public Page<StockReceiptResponseDto> getReceiptsForAccounting(int page, int size, String search) {
        log.info("[ACCOUNTING][STK] list receipts page={} size={} search={}", page, size, search);

        Pageable pageable = PageRequest.of(page, size);
        Page<StockReceipt> pageEntity = stockReceiptRepo.searchForAccounting(search, pageable);

        return pageEntity.map(stockReceiptMapper::toDto);
    }

    @Override
    public Page<StockReceiptItemResponseDto> getReceiptItems(Long receiptId, int page, int size) {
        log.info("[ACCOUNTING][STK] list items receiptId={} page={} size={}", receiptId, page, size);

        StockReceipt receipt = stockReceiptRepo.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập kho"));

        Pageable pageable = PageRequest.of(page, size);

        return stockReceiptItemRepo.findByStockReceipt(receipt, pageable)
                .map(stockReceiptItemMapper::toDto);
    }

    @Override
    public StockReceiptItemResponseDto getReceiptItemDetail(Long itemId) {
        log.info("[ACCOUNTING][STK] get item detail itemId={}", itemId);

        StockReceiptItem item = stockReceiptItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng nhập kho"));

        return stockReceiptItemMapper.toDto(item);
    }

    // =======================================================================
    //  STEP FUNCTIONS
    // =======================================================================

    private PurchaseRequestItem loadPurchaseRequestItem(Long id) {
        return purchaseRequestItemRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Không tìm thấy PR Item id={}", id);
                    return new ResourceNotFoundException("Không tìm thấy chi tiết yêu cầu mua hàng");
                });
    }

    private void validateReceivedQuantity(PurchaseRequestItem prItem, StockReceiveRequest request) {

        double alreadyReceived = Optional.ofNullable(prItem.getQuantityReceived()).orElse(0.0);
        double remaining = prItem.getQuantity() - alreadyReceived;

        if (request.getQuantityReceived() <= 0) {
            throw new IllegalArgumentException("Số lượng nhận phải > 0");
        }

        if (request.getQuantityReceived() > remaining) {
            throw new IllegalArgumentException("Số lượng nhận vượt quá còn lại");
        }
    }

    private StockReceipt loadOrCreateReceipt(PurchaseRequest pr, Employee employee) {

        return stockReceiptRepo.findByPurchaseRequest(pr)
                .orElseGet(() -> {
                    StockReceipt newReceipt = StockReceipt.builder()
                            .code(codeSequenceService.generateCode("SR"))
                            .purchaseRequest(pr)
                            .createdBy(employee)
                            .createdAt(LocalDateTime.now())
                            .status(StockReceiptStatus.CREATED)
                            .totalAmount(BigDecimal.ZERO)
                            .build();
                    return stockReceiptRepo.save(newReceipt);
                });
    }

    private StockReceiptItem createReceiptItem(
            PurchaseRequestItem prItem,
            StockReceiveRequest req,
            String fileUrl,
            Employee employee,
            StockReceipt receipt
    ) {

        if (prItem.getReviewStatus().equals(ManagerReviewStatus.PENDING)) {
            throw new RuntimeException("Quản lý chưa duyệt không thể nhập kho!");
        }

        StockReceiptItem item = StockReceiptItem.builder()
                .stockReceipt(receipt)
                .purchaseRequestItem(prItem)
                .requestedQuantity(prItem.getQuantity())
                .quantityReceived(req.getQuantityReceived())
                .note(req.getNote())
                .attachmentUrl(fileUrl)
                .receivedAt(LocalDateTime.now())
                .receivedById(employee.getEmployeeId())
                .receivedByName(employee.getFullName())
                .actualUnitPrice(prItem.getQuotationItem().getPart().getPurchasePrice())
                .actualTotalPrice(prItem.getQuotationItem().getPart().getPurchasePrice().multiply(BigDecimal.valueOf(req.getQuantityReceived())))
                .build();

        return stockReceiptItemRepo.save(item);
    }


//    private void updateReceiptTotalAmount(
//            StockReceipt receipt,
//            PurchaseRequestItem prItem,
//            StockReceiveRequest request
//    ) {
//
//        if (prItem.getEstimatedPurchasePrice() == null
//                || prItem.getQuantity() == null || prItem.getQuantity() == 0) return;
//
//        BigDecimal unitPrice = prItem.getEstimatedPurchasePrice()
//                .divide(BigDecimal.valueOf(prItem.getQuantity()), 2, RoundingMode.HALF_UP);
//
//        BigDecimal lineAmount = unitPrice.multiply(
//                BigDecimal.valueOf(request.getQuantityReceived()));
//
//        receipt.setTotalAmount(
//                Optional.ofNullable(receipt.getTotalAmount()).orElse(BigDecimal.ZERO).add(lineAmount)
//        );
//
//        stockReceiptRepo.save(receipt);
//    }

    private void updatePartStock(PurchaseRequestItem prItem, StockReceiveRequest request) {

        Part part = prItem.getPart();
        if (part == null) return;

        double newInStock = Optional.ofNullable(part.getQuantityInStock()).orElse(0.0)
                + request.getQuantityReceived();

        double newReserved = Optional.ofNullable(part.getReservedQuantity()).orElse(0.0)
                + request.getQuantityReceived();

        part.setQuantityInStock(newInStock);
        part.setReservedQuantity(newReserved);

        partRepository.save(part);
    }

    private void updatePurchaseRequestItem(PurchaseRequestItem prItem, StockReceiveRequest request) {

        double alreadyReceived = Optional.ofNullable(prItem.getQuantityReceived()).orElse(0.0);
        double updated = alreadyReceived + request.getQuantityReceived();

        prItem.setQuantityReceived(updated);

        prItem.setStatus(
                updated >= prItem.getQuantity()
                        ? PurchaseReqItemStatus.RECEIVED
                        : PurchaseReqItemStatus.PENDING
        );

        purchaseRequestItemRepo.save(prItem);
    }

    private void updatePurchaseRequestStatus(PurchaseRequest pr) {

        boolean allReceived = pr.getItems().stream()
                .allMatch(i -> i.getStatus() == PurchaseReqItemStatus.RECEIVED);

        pr.setStatus(allReceived
                ? PurchaseRequestStatus.COMPLETED
                : PurchaseRequestStatus.PENDING);

        purchaseRequestRepo.save(pr);
    }

    private void updateQuotationItemInventory(PurchaseRequestItem prItem) {

        PriceQuotationItem quotationItem = prItem.getQuotationItem();
        if (quotationItem == null) return;

        if (prItem.getQuantityReceived() >= prItem.getQuantity()) {
            quotationItem.setInventoryStatus(PriceQuotationItemStatus.AVAILABLE);

            quotationItem.setExportStatus(ExportStatus.WAITING_TO_EXPORT);

            quotationItemRepo.save(quotationItem);
            log.info("QuotationItem {} set to AVAILABLE + WAITING_EXPORT", quotationItem.getPriceQuotationItemId());
        }
    }

    // =======================================================================
    //  NOTIFICATION
    // =======================================================================

    private void sendNotificationToAdvisor(PurchaseRequestItem prItem, PurchaseRequest pr) {

        Employee advisor = pr.getRelatedQuotation()
                .getServiceTicket()
                .getCreatedBy();

        notificationService.createNotification(
                advisor.getEmployeeId(),
                NotificationTemplate.STOCK_RECEIPT_ITEM_RECEIVED.getTitle(),
                NotificationTemplate.STOCK_RECEIPT_ITEM_RECEIVED.format(
                        prItem.getPartName(),
                        pr.getRelatedQuotation().getPriceQuotationId()
                ),
                NotificationType.STOCK_RECEIVED,
                pr.getId().toString(),
                "/service-tickets/" + pr.getRelatedQuotation().getServiceTicket().getServiceTicketId()
        );

        log.info("Advisor {} notified for stock receipt.", advisor.getEmployeeId());
    }

    private void sendNotificationToAccountant(
            PurchaseRequestItem prItem,
            PurchaseRequest pr,
            StockReceiptItem receiptItem
    ) {

        accountRepository.findByRole(Role.ACCOUNTANT)
                .forEach(acc -> {

                    if (acc.getEmployee() == null) return;

                    notificationService.createNotification(
                            acc.getEmployee().getEmployeeId(),
                            "Phiếu nhập kho mới",
                            String.format("Linh kiện %s đã được nhập kho. Vui lòng xử lý phiếu thu/chi.",
                                    prItem.getPartName()),
                            NotificationType.STOCK_RECEIVED,
                            pr.getId().toString(),
                            "/stock-receipts/" + receiptItem.getStockReceipt().getReceiptId()
                    );
                });

        log.info("Accountant notified for stock receipt.");
    }
}
