package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private ServiceTicket serviceTicket;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "time_slot_id")
    private TimeSlot timeSlot;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id", referencedColumnName = "service_type_id")
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    // đánh dấu field trong entity là kiểu dữ liệu lớn
    @Lob
    @Column(name = "description", columnDefinition = "nvarchar(255)")
    private String description;

    // NEW: createdAt - set once when inserting
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
