package fpt.edu.vn.gms.dto.request;

import lombok.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CrossOrigin(origins = "http://localhost:5173")

public class AppointmentRequestDto {
    private String customerName;
    private String phoneNumber;
    private String licensePlate;
    private LocalDate appointmentDate;
    private Integer timeSlotIndex;
    private List<Long> serviceType;
    private String note;
}
