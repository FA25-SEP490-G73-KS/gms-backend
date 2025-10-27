package fpt.edu.vn.gms.dto.request;

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
    private Long serviceType;
    private String note;
}
