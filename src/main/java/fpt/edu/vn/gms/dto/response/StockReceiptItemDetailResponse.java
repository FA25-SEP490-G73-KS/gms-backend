package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StockReceiptItemDetailResponse {

    private Long id;
    private Long receiptId;
    private String partCode;
    private String partName;
    private Double requestedQty;
    private Double receivedQty;
    private Double remainingQty;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String status;

    private List<HistoryRecord> history;

    @Data
    @Builder
    public static class HistoryRecord {
        private Long id;
        private Double quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private String attachmentUrl;
        private String note;
        private LocalDateTime receivedAt;
        private String receivedBy;
    }
}

