package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.enums.LedgerVoucherType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateVoucherRequest {
    private LedgerVoucherType type;
    private BigDecimal amount;
    private Long relatedEmployeeId;
    private Long relatedSupplierId;
    private String description;
}