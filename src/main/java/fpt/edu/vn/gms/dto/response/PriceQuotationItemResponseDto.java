package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.WarehouseReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PriceQuotationItemResponseDto {

    private Long id;
    private Long partId;
    private String partName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private PriceQuotationItemStatus status;
    private WarehouseReviewStatus warehouseReviewStatus;
    private String warehouseNote;
}
