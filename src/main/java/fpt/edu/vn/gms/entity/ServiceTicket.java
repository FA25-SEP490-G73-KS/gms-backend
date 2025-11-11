package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Column(name = "service_ticket_code", unique = true, nullable = false, length = 20)
    private String serviceTicketCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", referencedColumnName = "appointmentId", unique = true)
    private Appointment appointment;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ticket_service_type",
            joinColumns = @JoinColumn(name = "service_ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "service_type_id")
    )
    private List<ServiceType> serviceTypes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

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
    private LocalDate deliveryAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_employee_id")
    private Employee createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
