package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
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
    private Long serviceTicketId;
    private String serviceTicketCode;
    private String licensePlate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate deliveryDate;
    private BigDecimal totalAmount;
    private ServiceTicketStatus status;
}
