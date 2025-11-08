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
    private List<Long> serviceTypeIds;
    private CustomerRequestDto customer;
    private VehicleRequestDto vehicle;
    private List<Long> assignedTechnicianIds;
    private String receiveCondition;
    private LocalDate expectedDeliveryAt;
}
