package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.common.enums.PurchaseReqItemStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestItemDetailDto {

    private Long itemId;
    private String partName;
    private String origin;
    private String supplierName;
    private String vehicleModel;
    private Double quantityInStock;
    private Double quantityOrdered;
    private BigDecimal unitPrice;      // p.purchasePrice
    private BigDecimal totalPrice;     // pri.estimatedPurchasePrice
    private PurchaseReqItemStatus status;
    private ManagerReviewStatus reviewStatus;
    private String note;
}
