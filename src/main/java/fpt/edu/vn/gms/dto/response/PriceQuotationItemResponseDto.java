package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import fpt.edu.vn.gms.common.enums.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.common.enums.WarehouseReviewStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceQuotationItemResponseDto {

    private Long priceQuotationItemId;

    private PartReqDto part;

    private String itemName;
    private Double quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private PriceQuotationItemType itemType;
    private PriceQuotationItemStatus inventoryStatus;
    private WarehouseReviewStatus warehouseReviewStatus;
    private String warehouseNote;
}
