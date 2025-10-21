package fpt.edu.vn.gms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import fpt.edu.vn.gms.common.ServiceType;
import fpt.edu.vn.gms.common.AppointmentStatus;
import lombok.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentResponseDto {
    private Long appointmentId;
    private String customerName;
    private String customerPhone;
    private String licensePlate;
    private LocalDate appointmentDate;
    private String timeSlotLabel;
    private ServiceType serviceType;
    private AppointmentStatus status;
    private String note;

    // NEW
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
