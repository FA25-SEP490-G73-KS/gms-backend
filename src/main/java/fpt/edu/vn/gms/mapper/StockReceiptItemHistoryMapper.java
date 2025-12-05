package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.StockReceiptItemDetailResponse;
import fpt.edu.vn.gms.entity.StockReceiptItem;
import fpt.edu.vn.gms.entity.StockReceiptItemHistory;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StockReceiptItemHistoryMapper {

    default StockReceiptItemDetailResponse toDetailDto(StockReceiptItem item, List<StockReceiptItemHistory> histories) {
        double requested = item.getRequestedQuantity() != null ? item.getRequestedQuantity() : 0.0;
        double received = item.getQuantityReceived() != null ? item.getQuantityReceived() : 0.0;
        double remaining = requested - received;

        List<StockReceiptItemDetailResponse.HistoryRecord> historyDtos = histories.stream()
                .map(this::toHistoryRecord)
                .collect(Collectors.toList());

        return StockReceiptItemDetailResponse.builder()
                .id(item.getId())
                .receiptId(item.getStockReceipt() != null ? item.getStockReceipt().getReceiptId() : null)
                .partCode(item.getPurchaseRequestItem() != null && item.getPurchaseRequestItem().getPart() != null
                        ? item.getPurchaseRequestItem().getPart().getSku() : null)
                .partName(item.getPurchaseRequestItem() != null ? item.getPurchaseRequestItem().getPartName() : null)
                .requestedQty(requested)
                .receivedQty(received)
                .remainingQty(remaining)
                .unitPrice(item.getActualUnitPrice())
                .totalPrice(item.getActualTotalPrice())
                .status(item.getStatus() != null ? item.getStatus().name() : null)
                .history(historyDtos)
                .build();
    }

    default StockReceiptItemDetailResponse.HistoryRecord toHistoryRecord(StockReceiptItemHistory history) {
        return StockReceiptItemDetailResponse.HistoryRecord.builder()
                .id(history.getId())
                .quantity(history.getQuantity())
                .unitPrice(history.getUnitPrice())
                .totalPrice(history.getTotalPrice())
                .attachmentUrl(history.getAttachmentUrl())
                .note(history.getNote())
                .receivedAt(history.getReceivedAt())
                .receivedBy(history.getReceivedBy())
                .build();
    }
}

