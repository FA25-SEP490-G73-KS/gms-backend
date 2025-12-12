package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InvoiceDetailResDto {

    private ServiceTicketResponseDto serviceTicket;

    private BigDecimal depositReceived;
    private BigDecimal finalAmount;
    private BigDecimal paidAmount;
    private String amountInWords;
}
