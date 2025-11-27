package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.enums.LedgerVoucherCategory;
import fpt.edu.vn.gms.common.enums.ManualVoucherType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ManualVoucherCreateRequest {
    private ManualVoucherType type;
    private LedgerVoucherCategory category;
    private BigDecimal amount;
    private String description;
    private String attachmentUrl;
    private Long approvedByEmployeeId;
}

