package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.LedgerVoucherStatus;
import fpt.edu.vn.gms.common.enums.LedgerVoucherType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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