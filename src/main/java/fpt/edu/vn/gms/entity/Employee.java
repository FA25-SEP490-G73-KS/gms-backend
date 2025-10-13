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
@Table(name = "Employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "position", length = 50)
    private String position;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "salary_base", precision = 18, scale = 2)
    private BigDecimal salaryBase;

    @Column(name = "paid_amount", precision = 18, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "hire_date")
    private LocalDateTime hireDate;

    @Column(name = "status", length = 50)
    private String status;
}
