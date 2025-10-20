package fpt.edu.vn.gms.dto;

import fpt.edu.vn.gms.common.ServiceType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDto {
    private String customerName;
    private String phoneNumber;
    private String licensePlate;
    private LocalDate appointmentDate;
    private Integer timeSlotIndex;
    private ServiceType serviceType;
    private String note;
}
