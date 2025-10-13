package fpt.edu.vn.gms.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarrantyDto {
    private Long warrantyId;
    private Long serviceTicketId;
    private Long customerId;
    private String type;
    private BigDecimal costDn;
    private BigDecimal costTech;
    private BigDecimal costCustomer;
    private Integer approvedBy;
    private String supplierStatus;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String description;
    private String attachmentUrl;
}
