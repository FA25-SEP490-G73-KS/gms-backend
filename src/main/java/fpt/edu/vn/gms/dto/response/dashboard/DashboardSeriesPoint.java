package fpt.edu.vn.gms.dto.response.dashboard;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSeriesPoint {
    private int month; // 1-12
    private BigDecimal revenue;
    private BigDecimal expense;
}

