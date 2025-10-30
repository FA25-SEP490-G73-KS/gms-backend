package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ServiceTicketRequestDto {

    private Long appointmentId;
    private Long serviceType;
    private CustomerRequestDto customer;
    private VehicleRequestDto vehicle;
    private Long advisorId;
    private List<Long> assignedTechnicianIds;
    private String receiveCondition;
    private String note;
    private LocalDate expectedDeliveryAt;
}
