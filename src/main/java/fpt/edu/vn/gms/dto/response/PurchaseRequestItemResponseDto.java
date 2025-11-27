package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.common.enums.PurchaseReqItemStatus;
import fpt.edu.vn.gms.common.enums.PurchaseRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PurchaseRequestItemResponseDto {

    private Long itemId;
    private Long partId;
    private String partName;
    private Double quantity;
    private Double quantityReceived;
    private BigDecimal estimatedPurchasePrice;
    private ManagerReviewStatus reviewStatus;
    private PurchaseReqItemStatus status;
    private String note;
}
