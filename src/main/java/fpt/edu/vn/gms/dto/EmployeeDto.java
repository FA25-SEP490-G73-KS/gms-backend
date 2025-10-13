package fpt.edu.vn.gms.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private Long employeeId;
    private String fullName;
    private String position;
    private String phone;
    private BigDecimal salaryBase;
    private BigDecimal paidAmount;
    private LocalDateTime hireDate;
    private String status;
}
