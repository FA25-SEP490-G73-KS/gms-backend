package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.*;
import fpt.edu.vn.gms.dto.response.StockReceiptItemResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.StockReceiptItemMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.StockReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockReceiptServiceImpl implements StockReceiptService {

    private final StockReceiptRepository stockReceiptRepo;
    private final StockReceiptItemRepository stockReceiptItemRepo;
    private final PurchaseRequestRepository purchaseRequestRepo;
    private final PurchaseRequestItemRepository purchaseRequestItemRepo;
    private final PartRepository partRepository;
    private final PriceQuotationItemRepository priceQuotationItemRepository;
    private final NotificationService notificationService;
    private final StockReceiptItemMapper stockReceiptItemMapper;

    @Transactional
    @Override
    public StockReceiptItemResponseDto receiveItem(Long prItemId, Employee employee) {

        PurchaseRequestItem prItem = purchaseRequestItemRepo.findById(prItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu mua hàng!!"));

        PurchaseRequest pr = prItem.getPurchaseRequest();

        // Lấy StockReceipt nếu đã có, nếu chưa tạo mới
        StockReceipt receipt = stockReceiptRepo.findByPurchaseRequest(pr)
                .orElseGet(() -> {
                    StockReceipt r = StockReceipt.builder()
                            .purchaseRequest(pr)
                            .createdBy(employee)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return stockReceiptRepo.save(r);
                });

        // Tạo StockReceiptItem (nhập đủ số lượng PR item)
        StockReceiptItem receiptItem = StockReceiptItem.builder()
                .stockReceipt(receipt)
                .purchaseRequestItem(prItem)
                .quantityReceived(prItem.getQuantity())
                .note("Nhập linh kiện cho báo giá: " + pr.getRelatedQuotation().getCode())
                .build();

        receipt.getItems().add(receiptItem);
        stockReceiptItemRepo.save(receiptItem);

        // ----------------------------
        // (1) Tăng quantityInStock của Part
        // ----------------------------
        Part part = prItem.getPart();
        if (part != null) {
            double newQty = part.getQuantityInStock() + prItem.getQuantity();
            part.setQuantityInStock(newQty);
            part.setReservedQuantity(prItem.getQuantity());
            partRepository.save(part);
        }

        // ----------------------------
        // (2) Chuyển status của PriceQuotationItem sang AVAILABLE
        // ----------------------------
        PriceQuotationItem quotationItem = prItem.getQuotationItem();
        if (quotationItem != null) {
            quotationItem.setInventoryStatus(PriceQuotationItemStatus.AVAILABLE);
            priceQuotationItemRepository.save(quotationItem);
        }

        // Cập nhật PR item
        prItem.setQuantityReceived(prItem.getQuantity());
        prItem.setStatus(PurchaseReqItemStatus.RECEIVED);
        purchaseRequestItemRepo.save(prItem);

        // Cập nhật PR tổng thể nếu tất cả item đã nhập
        if (pr.getItems().stream().allMatch(i -> i.getStatus() == PurchaseReqItemStatus.RECEIVED)) {
            pr.setStatus(PurchaseRequestStatus.COMPLETED);
            purchaseRequestRepo.save(pr);
        }

        // Notification đến Service Advisor
        Employee advisor = pr.getRelatedQuotation().getServiceTicket().getCreatedBy();
        notificationService.createNotification(
                advisor.getEmployeeId(),
                NotificationTemplate.STOCK_RECEIPT_ITEM_RECEIVED.getTitle(),
                NotificationTemplate.STOCK_RECEIPT_ITEM_RECEIVED.format(
                        prItem.getPartName(),
                        pr.getRelatedQuotation().getPriceQuotationId()
                ),
                NotificationType.STOCK_RECEIVED,
                prItem.getItemId().toString(),
                "/quotations/" + pr.getRelatedQuotation().getPriceQuotationId()
        );

        return stockReceiptItemMapper.toDto(receiptItem);
    }
}
