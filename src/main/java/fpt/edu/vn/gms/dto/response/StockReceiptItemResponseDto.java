package fpt.edu.vn.gms.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockReceiptItemResponseDto {

    private Long id;
    private Long purchaseRequestItemId;
    private Double quantityReceived;
    private String note;
}
