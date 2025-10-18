package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.ServiceType;
import fpt.edu.vn.gms.common.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "time_slot_id")
    private TimeSlot timeSlot;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Lob
    @Column(name = "description", columnDefinition = "nvarchar(255)")
    private String description;
}
