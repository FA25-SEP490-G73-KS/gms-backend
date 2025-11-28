package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fpt.edu.vn.gms.common.enums.PurchaseRequestStatus;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestResponseDto {

    private Long id;
    private String code;
    private String quotationCode;
    private String customerName;
    private String licensePlate;
    private String createdBy;
    private PurchaseRequestStatus status;
    private ManagerReviewStatus reviewStatus;
    private BigDecimal totalEstimatedAmount;
    private LocalDateTime createdAt;
}
