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
    private BigDecimal totalRevenue;
    private BigDecimal totalExpense;
    private BigDecimal profit;
    private BigDecimal totalDebt;
    private List<DashboardSeriesPoint> series;
}

