package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.request.VehicleRequestDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ServiceTicketResponseDto {

    private Long id;
    private List<String> serviceType;
    private CustomerResponseDto customer;
    private VehicleResponseDto vehicle;
    private String serviceAdvisor;
    private List<String> technicians;
    private String receiveCondition;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate deliveryAt;
    private String status;
}
