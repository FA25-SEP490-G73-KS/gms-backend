package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentListResDto {

    private Long id;
    private String code;
    private String serviceTicketCode;
    private String customerName;
    private String licensePlate;
    private BigDecimal finalAmount;
    private BigDecimal previousDebt;
}
