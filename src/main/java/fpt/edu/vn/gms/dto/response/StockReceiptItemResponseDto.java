package fpt.edu.vn.gms.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockReceiptItemResponseDto {

    private Long receiptItemId;
    private Long receiptId;

    private Long purchaseRequestItemId;
    private String purchaseRequestCode;

    private String partName;
    private String unit;

    private Double requestedQuantity;
    private Double quantityReceived;      // đợt này
    private Double totalQuantityReceived; // tổng đã nhận cho PR item

    private String attachmentUrl;
    private String note;

    private LocalDateTime receivedAt;
}
