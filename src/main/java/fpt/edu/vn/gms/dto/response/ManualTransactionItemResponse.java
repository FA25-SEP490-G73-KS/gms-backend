package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualTransactionItemResponse {
    private Long id;
    private Long partId;
    private String partSku;
    private String partName;
    private Double quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Double quantityInStock;
    private Double reservedQuantity;
    private String note;
}
