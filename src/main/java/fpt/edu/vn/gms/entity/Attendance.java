package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Attendance")
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

    @Column(name = "is_present_am")
    private Boolean isPresentAm;

    @Column(name = "is_present_pm")
    private Boolean isPresentPm;

    @Column(name = "note", length = 200)
    private String note;

    @Column(name = "recorded_by")
    private Integer recordedBy;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
}
