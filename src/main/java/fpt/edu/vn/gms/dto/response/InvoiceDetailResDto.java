package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class InvoiceDetailResDto {

    private ServiceTicketResponseDto serviceTicket;

    private BigDecimal depositReceived;
    private BigDecimal finalAmount;
    private String amountInWords;
}
