package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Long id;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private String phone;
    private String address;
    private Role role;
    private BigDecimal dailySalary;
    private LocalDateTime hireDate;
    private LocalDateTime terminationDate;
    private String status;
}

