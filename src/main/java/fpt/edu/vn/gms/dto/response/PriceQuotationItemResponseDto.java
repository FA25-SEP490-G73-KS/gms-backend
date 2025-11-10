package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.PriceQuotationItemType;
import fpt.edu.vn.gms.common.WarehouseReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PriceQuotationItemResponseDto {

    private Long priceQuotationItemId;
    private Long partId;
    private String partName;
    private PriceQuotationItemType itemType;
    private Integer quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String specification;
    private PriceQuotationItemStatus inventoryStatus;
    private WarehouseReviewStatus warehouseReviewStatus;
    private String warehouseNote;
}
