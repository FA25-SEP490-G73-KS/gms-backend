package fpt.edu.vn.gms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVoucherDto {

    private Long id;
    private String customerName;
    private String vehicle;
    private String quotationCode;
    private BigDecimal amount;
    private BigDecimal amountPaid;
    private String type;
    private String status;
}
