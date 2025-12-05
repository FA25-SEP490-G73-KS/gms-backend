package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.response.StockReceiptItemHistoryListResponse;
import fpt.edu.vn.gms.dto.response.StockReceiptItemHistoryPaymentResponse;
import fpt.edu.vn.gms.entity.StockReceiptItem;
import fpt.edu.vn.gms.entity.StockReceiptItemHistory;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.StockReceiptItemHistoryRepository;
import fpt.edu.vn.gms.service.StockReceiptItemHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockReceiptItemHistoryServiceImpl implements StockReceiptItemHistoryService {

    private final StockReceiptItemHistoryRepository historyRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Page<StockReceiptItemHistoryListResponse> getHistories(String keyword,
                                                                  Long supplierId,
                                                                  String fromDate,
                                                                  String toDate,
                                                                  Pageable pageable) {
        Specification<StockReceiptItemHistory> spec = buildSpecification(keyword, supplierId, fromDate, toDate);

        List<StockReceiptItemHistory> all = historyRepository.findAll(spec);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<StockReceiptItemHistoryListResponse> content = all.subList(start, end)
                .stream()
                .map(this::toListDto)
                .toList();

        return new PageImpl<>(content, pageable, all.size());
    }

    @Override
    public StockReceiptItemHistoryPaymentResponse getPaymentDetail(Long historyId) {
        StockReceiptItemHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("StockReceiptItemHistory not found with id: " + historyId));

        return toPaymentDto(history);
    }

    private Specification<StockReceiptItemHistory> buildSpecification(String keyword,
                                                                      Long supplierId,
                                                                      String fromDate,
                                                                      String toDate) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            // Join to related entities
            var itemJoin = root.join("stockReceiptItem");
            var receiptJoin = itemJoin.join("stockReceipt");

            // keyword: search receipt code or part SKU
            if (keyword != null && !keyword.isBlank()) {
                String likeKeyword = "%" + keyword.trim() + "%";
                var partJoin = itemJoin.join("purchaseRequestItem").join("part");
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(receiptJoin.get("code")), likeKeyword.toLowerCase()),
                        cb.like(cb.lower(partJoin.get("sku")), likeKeyword.toLowerCase())
                ));
            }

            // filter by supplierId
            if (supplierId != null) {
                var supplierJoin = receiptJoin.join("supplier");
                predicates = cb.and(predicates, cb.equal(supplierJoin.get("id"), supplierId));
            }

            // date range filter by receivedAt
            if (fromDate != null && !fromDate.isBlank()) {
                LocalDate startDate = LocalDate.parse(fromDate, DATE_FORMATTER);
                LocalDateTime startDateTime = startDate.atStartOfDay();
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("receivedAt"), startDateTime));
            }
            if (toDate != null && !toDate.isBlank()) {
                LocalDate endDate = LocalDate.parse(toDate, DATE_FORMATTER);
                LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("receivedAt"), endDateTime));
            }

            return predicates;
        };
    }

    private StockReceiptItemHistoryListResponse toListDto(StockReceiptItemHistory history) {
        StockReceiptItem item = history.getStockReceiptItem();
        String receiptCode = item != null && item.getStockReceipt() != null ? item.getStockReceipt().getCode() : null;
        String sku = null;
        if (item != null && item.getPurchaseRequestItem() != null && item.getPurchaseRequestItem().getPart() != null) {
            sku = item.getPurchaseRequestItem().getPart().getSku();
        }

        return StockReceiptItemHistoryListResponse.builder()
                .historyId(history.getId())
                .receiptCode(receiptCode)
                .sku(sku)
                .quantity(history.getQuantity())
                .totalPrice(history.getTotalPrice())
                .receivedAt(history.getReceivedAt())
                .paymentStatus(history.getPaymentStatus())
                .build();
    }

    private StockReceiptItemHistoryPaymentResponse toPaymentDto(StockReceiptItemHistory history) {
        StockReceiptItem item = history.getStockReceiptItem();
        Long supplierId = null;
        String supplierName = null;

        if (item != null && item.getStockReceipt() != null && item.getStockReceipt().getSupplier() != null) {
            supplierId = item.getStockReceipt().getSupplier().getId();
            supplierName = item.getStockReceipt().getSupplier().getName();
        }

        return StockReceiptItemHistoryPaymentResponse.builder()
                .historyId(history.getId())
                .supplierId(supplierId)
                .supplierName(supplierName)
                .amount(history.getTotalPrice())
                .attachmentUrl(history.getAttachmentUrl())
                .receivedAt(history.getReceivedAt())
                .build();
    }
}
