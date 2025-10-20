package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ServiceTicket")
/**
 * Thực thể Phiếu Dịch Vụ, ánh xạ tới bảng ServiceTicket.
 */
public class ServiceTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_ticket_id")
    private Long serviceTicketId;

    @ManyToOne
    @JoinColumn(name = "appointment_id", referencedColumnName = "appointmentId") // <-- sửa thành field name
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicle_id")
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private ServiceTicketStatus status;

    @Column(name = "notes", columnDefinition = "char(255)")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "delivery_at")
    private LocalDateTime deliveryAt;
}
