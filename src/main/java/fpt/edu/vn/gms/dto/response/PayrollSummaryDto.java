package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollSummaryDto {
    private BigDecimal totalPayroll;
    private BigDecimal totalApproved;
    private BigDecimal totalPending;
    private BigDecimal totalAllowance;
    private BigDecimal totalDeduction;
}
