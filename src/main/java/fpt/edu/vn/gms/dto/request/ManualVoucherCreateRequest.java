package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.enums.ManualVoucherType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ManualVoucherCreateRequest {
    private ManualVoucherType type;
    private BigDecimal amount;
    private String target;
    private String description;
    private String attachmentUrl;
    private Long approvedByEmployeeId;
}

