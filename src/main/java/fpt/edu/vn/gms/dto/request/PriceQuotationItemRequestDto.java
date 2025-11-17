package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;

@Data
@Builder
public class PriceQuotationItemRequestDto {

    private Long priceQuotationItemId;
    private Long partId;
    private String itemName;
    private Double quantity;
    private String unit;
    private PriceQuotationItemType type;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
