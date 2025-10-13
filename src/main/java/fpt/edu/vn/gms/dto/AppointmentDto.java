package fpt.edu.vn.gms.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDto {
    private Long appointmentId;
    private Long customerId;
    private Long vehicleId;
    private String serviceType;
    private LocalDateTime appointmentDate;
    private String status;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
}
