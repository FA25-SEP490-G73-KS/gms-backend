package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "employee_id", "date" })
})
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    private Employee employee;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "is_present")
    private Boolean isPresent;

    @Column(name = "note", length = 200)
    private String note;

    @Column(name = "recorded_by")
    private Long recordedBy;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
}
