package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.DashboardResponse;
import fpt.edu.vn.gms.dto.response.dashboard.DashboardOverviewResponse;

public interface DashboardService {
    DashboardResponse getDashboardOverview();

    DashboardOverviewResponse getFinancialOverview(Integer year);
}
