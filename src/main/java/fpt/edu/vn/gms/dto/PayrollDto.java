package fpt.edu.vn.gms.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollDto {
    private Long payrollId;
    private Long employeeId;
    private Integer month;
    private Integer year;
    private BigDecimal totalSalary;
    private BigDecimal advanceDeduction;
    private BigDecimal warrantyDeduction;
    private BigDecimal salaryBonus;
    private BigDecimal netSalary;
    private LocalDateTime createAt;
    private String status;
}
