package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.EmployeeRole;
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
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    // --- Thông tin cá nhân ---
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone", length = 20, unique = true)
    private String phone;

    @Column(name = "address", length = 200)
    private String address;

    // --- Thông tin công việc ---
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private EmployeeRole employeeRole;

    @Column(name = "hire_date")
    private LocalDateTime hireDate;

    // Active, Nghỉ việc, Tạm ngưng
    @Column(name = "status", length = 50)
    private String status;

    // nullable với TECHNICIANS
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private Account account;
}
