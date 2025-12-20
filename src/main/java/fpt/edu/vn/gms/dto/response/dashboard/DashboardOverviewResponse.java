package fpt.edu.vn.gms.dto.response.dashboard;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewResponse {
    private int year;
    private Integer month; // null nếu không filter theo tháng
    private BigDecimal totalRevenue;
    private BigDecimal totalExpense;
    private BigDecimal profit;
    private BigDecimal totalDebt;
    private List<DashboardSeriesPoint> series;
    private List<ServiceTypeExpenseDto> serviceTypeExpenseDistribution;
}
