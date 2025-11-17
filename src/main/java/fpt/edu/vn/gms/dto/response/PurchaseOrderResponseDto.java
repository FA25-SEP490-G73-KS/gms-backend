package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.PurchaseOrderStatus;
import fpt.edu.vn.gms.entity.PurchaseOrderItem;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PurchaseOrderResponseDto {

    private Long id;
    private String code;
    private PurchaseRequest purchaseRequest;
    private PurchaseOrderStatus status;
    private BigDecimal totalAmount;
    private List<PurchaseOrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime receivedAt;
}
