package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailResDto {

    private ServiceTicketResponseDto serviceTicket;

    private BigDecimal depositReceived;
    private BigDecimal finalAmount;
    private BigDecimal paidAmount;
    private String amountInWords;
}
