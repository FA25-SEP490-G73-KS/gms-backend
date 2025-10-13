package fpt.edu.vn.gms.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceTicketDto {
    private Long serviceTicketId;
    private Long appointmentId;
    private Long employeeId;
    private Long customerId;
    private Long vehicleId;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime deliveryAt;
}
