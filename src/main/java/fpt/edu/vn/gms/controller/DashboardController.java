package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.DashboardResponse;
import fpt.edu.vn.gms.dto.response.WarehouseDashboardResponse;
import fpt.edu.vn.gms.dto.response.dashboard.DashboardOverviewResponse;
import fpt.edu.vn.gms.dto.response.dashboard.StatisticsResponse;
import fpt.edu.vn.gms.service.DashboardService;
import fpt.edu.vn.gms.service.WarehouseDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "dashboard", description = "Tổng quan số liệu hệ thống")
@RestController
@RequestMapping(path = "/api/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final WarehouseDashboardService warehouseDashboardService;

    @GetMapping("/service-advisor/overview")
    @Operation(summary = "Lấy tổng quan dashboard", description = "Trả về số liệu tổng hợp cho màn hình dashboard.")
    public ResponseEntity<ApiResponse<DashboardResponse>> getOverview(
            @RequestParam(value = "year", required = false) Integer year) {
        DashboardResponse response = dashboardService.getDashboardOverview(year);
        return ResponseEntity.ok(ApiResponse.success("Dashboard overview", response));
    }

    @GetMapping("/warehouse/overview")
    @Operation(summary = "Lấy thống kê kho", description = "Lấy tổng hợp các số liệu thống kê kho theo năm/tháng.")
    public ResponseEntity<ApiResponse<WarehouseDashboardResponse>> getWarehouseOverview(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        WarehouseDashboardResponse response = warehouseDashboardService.getDashboard(year, month);
        return ResponseEntity.ok(ApiResponse.success("Warehouse dashboard", response));
    }

    @GetMapping("/financial/overview")
    @Operation(summary = "Thống kê tài chính", description = "Tổng doanh thu, chi phí, lợi nhuận, công nợ và chuỗi doanh thu/chi phí theo tháng")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> getFinancialOverview(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {

        DashboardOverviewResponse overview = dashboardService.getFinancialOverview(year, month);
        return ResponseEntity.ok(ApiResponse.success("Financial dashboard", overview));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Thống kê tổng hợp", description = "Thống kê profit, tổng số phiếu dịch vụ, phụ tùng sắp hết/hết hàng, công nợ, doanh thu theo năm và chi tiêu theo service type. Filter từ năm đến năm cho revenueByYear. Filter tháng trong một năm cho các metrics khác.")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getStatistics(
            @RequestParam(value = "fromYear", required = false) Integer fromYear,
            @RequestParam(value = "toYear", required = false) Integer toYear,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {

        StatisticsResponse statistics = dashboardService.getStatistics(fromYear, toYear, year, month);
        return ResponseEntity.ok(ApiResponse.success("Statistics", statistics));
    }
}
