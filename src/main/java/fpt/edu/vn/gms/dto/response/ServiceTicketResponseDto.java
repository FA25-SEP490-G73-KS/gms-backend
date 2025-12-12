package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ServiceTicketResponseDto {

    private Long serviceTicketId;
    private String serviceTicketCode;
    private List<String> serviceType;
    private CustomerResponseDto customer;
    private VehicleResponseDto vehicle;
    private String createdBy;
    private List<String> technicians;
    private String receiveCondition;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate deliveryAt;
    private String status;
    private PriceQuotationResponseDto priceQuotation;
}
