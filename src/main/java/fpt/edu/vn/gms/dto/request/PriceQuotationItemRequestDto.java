package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PriceQuotationItemRequestDto {

    private Long partId;
    private String partName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal discount;
    private String description;
}
