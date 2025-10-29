package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "service_ticket")
public class ServiceTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_ticket_id")
    private Long serviceTicketId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", referencedColumnName = "appointmentId", unique = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id", referencedColumnName = "service_type_id")
    private ServiceType serviceType;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicle_id")
    private Vehicle vehicle;

    @ManyToMany
    @JoinTable(
            name = "service_ticket_technicians",
            joinColumns = @JoinColumn(name = "service_ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> technicians = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "advisor_id", referencedColumnName = "employee_id")
    private Employee serviceAdvisor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quotation_id")
    private PriceQuotation priceQuotation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private ServiceTicketStatus status;

    @Column(name = "receive_condition", columnDefinition = "TEXT")
    private String receiveCondition;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "delivery_at")
    private LocalDateTime deliveryAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
