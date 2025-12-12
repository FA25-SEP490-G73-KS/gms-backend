package fpt.edu.vn.gms.dto.request;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpenseVoucherCreateRequest {

    private Long supplierId;
    private BigDecimal amount;
    private String attachmentUrl;
}
