package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CustomerServiceHistoryDto {

    // Lịch sử dịch vụ của khách hàng (Manager)
    private String serviceTicketCode;
    private String licensePlate;
    private LocalDateTime createdDate;
    private LocalDate deliveryDate;
    private BigDecimal totalAmount;
    private ServiceTicketStatus status;
}
