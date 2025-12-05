package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StockReceiptItemResponse {

    private Long id;
    private String partCode;
    private String partName;
    private Double requestedQty;
    private Double receivedQty;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String status;
}

