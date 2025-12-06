package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.WarehouseDashboardResponse;

public interface WarehouseDashboardService {

    WarehouseDashboardResponse getDashboard(Integer year, Integer month);
}

