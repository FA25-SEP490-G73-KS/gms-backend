package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.WarehouseReviewStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseReviewRequestDto {

    private Long itemId;
    private WarehouseReviewStatus warehouseReviewStatus;
    private PriceQuotationItemStatus stockStatus;
    private String warehouseNote;
}
