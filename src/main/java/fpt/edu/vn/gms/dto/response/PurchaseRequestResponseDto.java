package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.PurchaseRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PurchaseRequestResponseDto {

    private Long id;
    private String code;
    private PurchaseRequestStatus status;
    private BigDecimal totalEstimatedAmount;
    private LocalDateTime createdAt;
}
