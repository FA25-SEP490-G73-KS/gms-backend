package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.ExportItemRequest;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.StockExportItemResponse;
import fpt.edu.vn.gms.dto.response.StockExportResponse;
import fpt.edu.vn.gms.service.StockExportService;
import fpt.edu.vn.gms.service.WarehouseQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/exports")
@RequiredArgsConstructor
public class WarehouseExportController {

    private final StockExportService stockExportService;

    @GetMapping()
    public ResponseEntity<ApiResponse<Page<StockExportResponse>>> getStockOutQuotations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        Page<StockExportResponse> quotations = stockExportService.getExportingQuotations(page, size);
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Danh sách stock-out", quotations));
    }

    @GetMapping("/quotation/{id}")
    public ResponseEntity<ApiResponse<List<StockExportItemResponse>>> getStockOutQuotationById(
            @PathVariable Long id
    ) {

        List<StockExportItemResponse> quotation = stockExportService.getExportingQuotationById(id);
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Chi tiết stock-out", quotation));
    }

    @PostMapping("/item/{itemId}/export")
    public ResponseEntity<ApiResponse<StockExportItemResponse>> exportItem(
            @PathVariable Long itemId,
            @RequestBody ExportItemRequest request
    ) {
        StockExportItemResponse response = stockExportService.exportItem(
                itemId,
                request.getQuantity(),
                request.getReceiverId()
        );

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Xuất kho thành công", response));
    }

}
