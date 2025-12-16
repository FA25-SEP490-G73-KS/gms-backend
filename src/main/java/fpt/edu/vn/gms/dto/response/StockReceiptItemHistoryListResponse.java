package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fpt.edu.vn.gms.common.enums.ReceiptPaymentStatus;
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
public class StockReceiptItemHistoryListResponse {

    private Long historyId;
    private String receiptCode;
    private String sku;
    private Double quantity;
    private BigDecimal totalPrice;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime receivedAt;
    private ReceiptPaymentStatus paymentStatus;
}

