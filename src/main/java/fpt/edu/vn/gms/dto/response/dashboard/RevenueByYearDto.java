package fpt.edu.vn.gms.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueByYearDto {
    private Integer year;
    private BigDecimal totalRevenue;
    // Doanh thu theo tháng trong năm
    private List<MonthlyRevenueDto> monthlyRevenue;
}

