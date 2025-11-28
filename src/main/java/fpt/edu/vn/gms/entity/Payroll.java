package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.PayrollStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "employee_id", "month", "year" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payroll_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "month")
    private Integer month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "base_salary", precision = 18, scale = 2)
    private BigDecimal baseSalary;
    // dailySalary * workingDays

    @Column(name = "total_allowance", precision = 18, scale = 2)
    private BigDecimal totalAllowance;

    @Column(name = "total_deduction", precision = 18, scale = 2)
    private BigDecimal totalDeduction;

    @Column(name = "total_advance", precision = 18, scale = 2)
    private BigDecimal totalAdvanceSalary;

    @Column(name = "working_days")
    private Integer workingDays;

    @Column(name = "net_salary", precision = 18, scale = 2)
    private BigDecimal netSalary;  // base + allowance - deduction - advance

    // --- APPROVAL WORKFLOW ---
    @Enumerated(EnumType.STRING)
    private PayrollStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by")
    private Employee paidBy;

    private LocalDateTime paidAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
