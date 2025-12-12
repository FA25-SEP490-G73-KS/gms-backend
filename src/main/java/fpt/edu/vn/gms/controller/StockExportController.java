package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.request.ExportItemRequest;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.StockExportService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppRoutes.STOCK_EXPORTS_PREFIX)
@RequiredArgsConstructor
@Tag(name = "export-controller", description = "Quản lý phiếu xuất kho và dòng xuất kho")
public class StockExportController {

    private final StockExportService stockExportService;

    @GetMapping
    @Operation(summary = "Danh sách phiếu xuất kho")
    public ApiResponse<Page<StockExportListResponse>> getExports(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockExportListResponse> result = stockExportService.getExports(keyword, status, fromDate, toDate, pageable);
        return ApiResponse.success("Lấy danh sách phiếu xuất kho thành công", result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết phiếu xuất kho")
    public ApiResponse<StockExportDetailResponse> getExportDetail(@PathVariable Long id) {
        return ApiResponse.success("Lấy chi tiết phiếu xuất kho thành công", stockExportService.getExportDetail(id));
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "Danh sách dòng xuất kho của 1 phiếu")
    public ApiResponse<Page<StockExportItemResponse>> getExportItems(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockExportItemResponse> result = stockExportService.getExportItems(id, pageable);
        return ApiResponse.success("Lấy danh sách dòng xuất kho thành công", result);
    }

    @GetMapping("/export-items/{id}")
    @Operation(summary = "Chi tiết 1 dòng xuất kho")
    public ApiResponse<ExportItemDetailResponse> getExportItemDetail(@PathVariable Long id) {
        return ApiResponse.success("Lấy chi tiết dòng xuất kho thành công", stockExportService.getExportItemDetail(id));
    }

    @PostMapping("/export-items/{id}/export")
    @Operation(summary = "Xuất kho 1 phần / toàn bộ cho 1 dòng xuất kho")
    public ApiResponse<StockExportItemResponse> exportItem(
            @PathVariable Long id,
            @RequestBody ExportItemRequest request,
            @CurrentUser Employee employee
    ) {
        return ApiResponse.success("Xuất kho thành công", stockExportService.exportItem(id, request, employee));
    }

    @GetMapping("/export-items/{id}/history")
    @Operation(summary = "Lịch sử xuất kho của 1 dòng")
    public ApiResponse<ExportItemDetailResponse> getExportItemHistory(@PathVariable Long id) {
        return ApiResponse.success("Lấy lịch sử xuất kho thành công", stockExportService.getExportItemHistory(id));
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Duyệt phiếu xuất kho")
    public ApiResponse<StockExportDetailResponse> approveExport(
            @PathVariable Long id,
            @CurrentUser Employee employee
    ) {
        return ApiResponse.success("Duyệt phiếu xuất kho thành công", stockExportService.approveExport(id, employee));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Hủy phiếu xuất kho")
    public ApiResponse<StockExportDetailResponse> cancelExport(
            @PathVariable Long id,
            @CurrentUser Employee employee
    ) {
        return ApiResponse.success("Hủy phiếu xuất kho thành công", stockExportService.cancelExport(id, employee));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Đánh dấu hoàn thành phiếu xuất kho")
    public ApiResponse<StockExportDetailResponse> completeExport(
            @PathVariable Long id,
            @CurrentUser Employee employee
    ) {
        return ApiResponse.success("Hoàn thành phiếu xuất kho thành công", stockExportService.markCompleted(id, employee));
    }
}

