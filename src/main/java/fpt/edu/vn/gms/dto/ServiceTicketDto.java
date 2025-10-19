package fpt.edu.vn.gms.dto;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * DTO đại diện cho Phiếu Dịch Vụ dùng để trao đổi dữ liệu với client.
 */
public class ServiceTicketDto {
    private Long serviceTicketId;
    private Long appointmentId;
    private Long employeeId;
    private Long customerId;
    private Long vehicleId;
    private ServiceTicketStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime deliveryAt;
}
