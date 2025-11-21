package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PaymentDetailResDto {

    private Long paymentId;
    private String paymentCode;

    private Long serviceTicketId;
    private String customerName;

    private List<PaymentItemDto> items;

    // Các thông số thanh toán
    private BigDecimal totalItemPrice;
    private BigDecimal laborCost;
    private BigDecimal discount;
    private BigDecimal previousDebt;
    private BigDecimal depositReceived;
    private BigDecimal finalAmount;
    private String amountInWords;

}
