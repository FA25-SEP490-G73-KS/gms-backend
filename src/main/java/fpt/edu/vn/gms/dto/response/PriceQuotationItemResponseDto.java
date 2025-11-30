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
    private String categoryName;
    private String marketName;
    private String supplierName;
    private String brandName;
    private String modelName;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;

    private Double quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private PriceQuotationItemType itemType;
    private PriceQuotationItemStatus inventoryStatus;
    private WarehouseReviewStatus warehouseReviewStatus;
    private String warehouseNote;
}
