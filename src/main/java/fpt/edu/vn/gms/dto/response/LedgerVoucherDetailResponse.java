package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fpt.edu.vn.gms.common.enums.LedgerVoucherStatus;
import fpt.edu.vn.gms.common.enums.LedgerVoucherType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LedgerVoucherDetailResponse {
    private Long id;
    private String code;
    private LedgerVoucherType type;
    private LedgerVoucherStatus status;
    private BigDecimal amount;
    private Long relatedEmployeeId;
    private String relatedEmployeeName;
    private Long relatedSupplierId;
    private String relatedSupplierName;
    private String description;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime approvedAt;
    private Long createdByEmployeeId;
    private String createdByEmployeeName;
    private Long approvedByEmployeeId;
    private String approvedByEmployeeName;
    private String attachmentUrl;
}