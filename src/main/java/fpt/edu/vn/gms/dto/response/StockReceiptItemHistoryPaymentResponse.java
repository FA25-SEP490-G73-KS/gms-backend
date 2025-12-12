package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReceiptItemHistoryPaymentResponse {

    private Long historyId;
    private Long supplierId;
    private String supplierName;
    private BigDecimal amount;
    private String attachmentUrl;
    private LocalDateTime receivedAt;
}

