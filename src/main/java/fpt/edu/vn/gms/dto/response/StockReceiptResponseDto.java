package fpt.edu.vn.gms.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockReceiptResponseDto {

    private Long receiptId;
    private Long purchaseRequestId;
    private Long createdByEmployeeId;
    private LocalDateTime createdAt;
}
