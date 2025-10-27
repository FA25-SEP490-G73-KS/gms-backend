package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.request.VehicleRequestDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ServiceTicketResponseDto {

    private CustomerRequestDto customer;
    private VehicleRequestDto vehicle;
    private Long advisorId;
    private List<Long> assignedTechnicianIds;
    private String receiveCondition;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expectedDeliveryAt;


}
