package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.LedgerVoucherStatus;
import fpt.edu.vn.gms.common.enums.LedgerVoucherType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class LedgerVoucherListResponse {
    private Long id;
    private String code;
    private LedgerVoucherType type;
    private LedgerVoucherStatus status;
    private BigDecimal amount;
    private Long relatedEmployeeId;
    private Long relatedSupplierId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}