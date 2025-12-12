package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class PayrollMonthlySummaryDto {

    private List<PayrollListItemDto> items;
    private BigDecimal totalNetSalary;
}
