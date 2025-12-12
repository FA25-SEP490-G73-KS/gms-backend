package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PayrollOverviewDto {

    private BigDecimal baseSalary;
    private Double totalWorkingDays;
    private Integer leaveDays;
    private BigDecimal totalAllowance;
    private BigDecimal totalDeduction;
    private BigDecimal netSalary;
}
