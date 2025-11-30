package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceListResDto {

    private Long id;
    private String code;
    private String serviceTicketCode;
    private String customerName;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;
    private ServiceTicketStatus serviceTicketStatus;
}
