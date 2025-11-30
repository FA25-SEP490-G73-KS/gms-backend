package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ExpenseVoucherCreateRequest {

    private Long supplierId;
    private BigDecimal amount;
    private String attachmentUrl;
}
