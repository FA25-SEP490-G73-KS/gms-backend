package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

import fpt.edu.vn.gms.common.enums.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.common.enums.WarehouseReviewStatus;

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
    private PriceQuotationItemStatus inventoryStatus;
    private WarehouseReviewStatus warehouseReviewStatus;
    private String warehouseNote;
}
