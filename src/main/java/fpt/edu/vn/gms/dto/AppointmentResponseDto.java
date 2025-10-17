package fpt.edu.vn.gms.dto;

import fpt.edu.vn.gms.common.ServiceType;
import fpt.edu.vn.gms.common.AppointmentStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDto {
    private Long appointmentId;
    private String customerName;
    private String licensePlate;
    private LocalDate appointmentDate;
    private String timeSlotLabel;
    private ServiceType serviceType;
    private AppointmentStatus status;
    private String note;
}
