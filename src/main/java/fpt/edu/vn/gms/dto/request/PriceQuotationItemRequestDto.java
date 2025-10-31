package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.PriceQuotationItemType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PriceQuotationItemRequestDto {

    private Long priceQuotationItemId;
    private Long partId;
    private String itemName;
    private Integer quantity;
    private PriceQuotationItemStatus status;
    private PriceQuotationItemType type;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
