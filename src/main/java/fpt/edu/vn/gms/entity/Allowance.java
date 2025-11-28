package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.AllowanceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "allowance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allowance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allowance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Loại phụ cấp (ăn trưa, tăng ca, thưởng…)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50)
    private AllowanceType type;

    @Column(name = "amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "note", length = 255)
    private String note;

    // Tháng - Năm áp dụng
    @Column(name = "month")
    private Integer month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String createdBy;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
