package fpt.edu.vn.gms.dto.response;

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
public class EmployeeDetailResponse {

    private Long employeeId;
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String province;
    private String ward;
    private String addressDetail;
    private String position;
    private BigDecimal dailySalary;
    private LocalDateTime hireDate;
    private LocalDateTime terminationDate;
    private String status;
}

