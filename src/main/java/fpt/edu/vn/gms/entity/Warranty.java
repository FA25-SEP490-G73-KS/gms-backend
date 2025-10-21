package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Warranty")
public class Warranty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warranty_id")
    private Long warrantyId;

    @ManyToOne
    @JoinColumn(name = "service_ticket_id", referencedColumnName = "service_ticket_id")
    private ServiceTicket serviceTicket;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @Column(name = "type", length = 30)
    private String type;

    @Column(name = "cost_dn", precision = 18, scale = 2)
    private BigDecimal costDn;

    @Column(name = "cost_tech", precision = 18, scale = 2)
    private BigDecimal costTech;

    @Column(name = "cost_customer", precision = 18, scale = 2)
    private BigDecimal costCustomer;

    @Column(name = "approved_by")
    private Integer approvedBy; // referenced to Employee optionally

    @Column(name = "supplier_status", length = 50)
    private String supplierStatus;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "description", columnDefinition = "nvarchar(255)")
    private String description;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;
}
