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
@Table(name = "Payroll")
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payroll_id")
    private Long payrollId;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    private Employee employee;

    @Column(name = "month")
    private Integer month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "total_salary", precision = 18, scale = 2)
    private BigDecimal totalSalary;

    @Column(name = "advance_deduction", precision = 18, scale = 2)
    private BigDecimal advanceDeduction;

    @Column(name = "warranty_deduction", precision = 18, scale = 2)
    private BigDecimal warrantyDeduction;

    @Column(name = "salary_bonus", precision = 18, scale = 2)
    private BigDecimal salaryBonus;

    @Column(name = "net_salary", precision = 18, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "status", length = 50)
    private String status;
}
