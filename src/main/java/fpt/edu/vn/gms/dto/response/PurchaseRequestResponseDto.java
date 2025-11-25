package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fpt.edu.vn.gms.common.enums.PurchaseRequestStatus;

@Data
@Builder
public class PurchaseRequestResponseDto {

    private Long id;
    private String code;
    private String licensePlate;
    private PurchaseRequestStatus status;
    private ManagerReviewStatus reviewStatus;
    private BigDecimal totalEstimatedAmount;
    private LocalDateTime createdAt;
}
