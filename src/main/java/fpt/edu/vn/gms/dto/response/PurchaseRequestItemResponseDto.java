package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

import fpt.edu.vn.gms.common.enums.PurchaseReqItemStatus;

@Data
@Builder
public class PurchaseRequestItemResponseDto {

    private Long itemId;
    private String partName;
    private double quantity;
    private BigDecimal estimatedPurchasePrice;
    private PurchaseReqItemStatus status;
    private String note;
}
