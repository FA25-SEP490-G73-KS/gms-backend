package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.DashboardResponse;
import fpt.edu.vn.gms.dto.response.dashboard.DashboardOverviewResponse;

public interface DashboardService {
    DashboardResponse getDashboardOverview(Integer year);

    DashboardOverviewResponse getFinancialOverview(Integer year, Integer month);

    fpt.edu.vn.gms.dto.response.dashboard.StatisticsResponse getStatistics(
            Integer fromYear, Integer toYear, Integer year, Integer month);
}
