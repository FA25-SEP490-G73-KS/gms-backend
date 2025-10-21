package fpt.edu.vn.gms.dto;

import fpt.edu.vn.gms.common.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.CustomerType;
import fpt.edu.vn.gms.common.ServiceTicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * ServiceTicketDto đại diện cho Phiếu Dịch Vụ dùng để trao đổi dữ liệu với client.
 */
public class ServiceTicketDto {
    private Long serviceTicketId;
    private Long appointmentId;
    private Long customerId;
    private Long vehicleId;
    private ServiceTicketStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime deliveryAt;

    // Customer fields
    private String fullName;
    private String phone;
    private String zaloId;
    private String address;
    private CustomerType customerType;     // optional
    private CustomerLoyaltyLevel loyaltyLevel;     // optional

    // Vehicle fields
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private String vin;

}
