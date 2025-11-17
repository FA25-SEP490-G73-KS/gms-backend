package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.StockReceiptStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PurchaseRequestItemResponseDto {

    private Long itemId;
    private String partName;
    private double quantity;
    private BigDecimal estimatedPurchasePrice;
    private StockReceiptStatus.ManagerReviewStatus reviewStatus;
    private String note;
}
