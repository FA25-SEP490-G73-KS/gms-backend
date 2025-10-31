package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PriceQuotationItemRequestDto {

    private Long partId;       // optional - if new part, backend can accept partName instead
    private String partName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountRate;
}
