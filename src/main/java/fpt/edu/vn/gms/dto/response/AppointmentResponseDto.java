package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.entity.ServiceType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDto {

    private Long appointmentId;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long vehicleId;
    private String licensePlate;
    private LocalDate appointmentDate;
    private String timeSlotLabel;
    private List<String> serviceType;
    private AppointmentStatus status;
    private String note;

    // NEW
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
