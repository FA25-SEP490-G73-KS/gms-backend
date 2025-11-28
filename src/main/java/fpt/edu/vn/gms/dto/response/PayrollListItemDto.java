package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.PayrollStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PayrollListItemDto {

    // Dto cho màn lương nhân viên (kế toán)
    private Long employeeId;
    private String employeeName;
    private String phone;

    private BigDecimal baseSalary;      // Lương cơ bản
    private BigDecimal allowance;       // Tổng phụ cấp
    private BigDecimal deduction;       // Tổng khấu trừ
    private BigDecimal advanceSalary;   // Tổng ứng lương
    private BigDecimal netSalary;       // Lương ròng

    private Integer workingDays;        // số ngày làm

    private PayrollStatus status;
}
