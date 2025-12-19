package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTicketResponseDto {

    private Long serviceTicketId;
    private String serviceTicketCode;
    private List<String> serviceType;
    private CustomerResponseDto customer;
    private VehicleResponseDto vehicle;
    private String createdBy;
    private List<String> technicians;
    private String receiveCondition;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime updatedAt;
    private LocalDate deliveryAt;
    private String status;
    private PriceQuotationResponseDto priceQuotation;
}
