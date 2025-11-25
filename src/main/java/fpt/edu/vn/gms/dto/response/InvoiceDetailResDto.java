package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class InvoiceDetailResDto {

    private Long id;
    private String code;
    private String status;

    private ServiceTicketResponseDto serviceTicket;
    private String customerName;

    private List<PaymentItemDto> items;

    // Các thông số thanh toán
    private BigDecimal totalItemPrice;
    private BigDecimal discount;
    private BigDecimal depositReceived;
    private BigDecimal finalAmount;
    private String amountInWords;

}
