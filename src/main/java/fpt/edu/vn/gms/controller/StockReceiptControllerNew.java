package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.CreateReceiptItemHistoryRequest;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.service.StockReceiptService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppRoutes.ACCOUNTING_STOCK_RECEIPT_PREFIX)
@RequiredArgsConstructor
@Tag(name = "stock-receipt-controller", description = "Quản lý phiếu nhập kho và lịch sử nhận hàng")
public class StockReceiptControllerNew {

    private final @Qualifier("stockReceiptServiceImplNew") StockReceiptService stockReceiptService;

    @GetMapping
    @Operation(summary = "Danh sách phiếu nhập kho")
    public ApiResponse<Page<StockReceiptListResponse>> getReceipts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockReceiptListResponse> result = stockReceiptService.getReceipts(status, keyword, fromDate, toDate, supplierId, pageable);
        return ApiResponse.success("Lấy danh sách phiếu nhập kho thành công", result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết phiếu nhập kho")
    public ApiResponse<StockReceiptDetailResponse> getReceiptDetail(@PathVariable Long id) {
        return ApiResponse.success("Lấy chi tiết phiếu nhập kho thành công", stockReceiptService.getReceiptDetail(id));
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "Danh sách dòng nhập kho của 1 phiếu")
    public ApiResponse<Page<StockReceiptItemResponse>> getReceiptItems(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockReceiptItemResponse> result = stockReceiptService.getReceiptItems(id, pageable);
        return ApiResponse.success("Lấy danh sách dòng nhập kho thành công", result);
    }

    @GetMapping("/receipt-items/{itemId}")
    @Operation(summary = "Chi tiết 1 dòng nhập kho")
    public ApiResponse<StockReceiptItemDetailResponse> getReceiptItemDetail(@PathVariable Long itemId) {
        return ApiResponse.success("Lấy chi tiết dòng nhập kho thành công", stockReceiptService.getReceiptItemDetail(itemId));
    }

    @GetMapping("/receipt-items/{itemId}/history")
    @Operation(summary = "Lịch sử nhận hàng của 1 dòng nhập kho")
    public ApiResponse<StockReceiptItemDetailResponse> getReceiptItemHistory(@PathVariable Long itemId) {
        return ApiResponse.success("Lấy lịch sử nhập kho thành công", stockReceiptService.getReceiptItemHistory(itemId));
    }

    @PostMapping("/receipt-items/{itemId}/receive")
    @Operation(summary = "Nhận hàng (nhập kho) cho 1 dòng nhập kho")
    public ApiResponse<StockReceiptItemDetailResponse> createReceiptItemHistory(
            @PathVariable Long itemId,
            @RequestBody CreateReceiptItemHistoryRequest request
    ) {
        return ApiResponse.success("Nhập kho thành công", stockReceiptService.createReceiptItemHistory(itemId, request));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Hủy phiếu nhập kho")
    public ApiResponse<StockReceiptDetailResponse> cancelReceipt(@PathVariable Long id) {
        return ApiResponse.success("Hủy phiếu nhập kho thành công", stockReceiptService.cancelReceipt(id));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Hoàn thành phiếu nhập kho")
    public ApiResponse<StockReceiptDetailResponse> completeReceipt(@PathVariable Long id) {
        return ApiResponse.success("Hoàn thành phiếu nhập kho thành công", stockReceiptService.completeReceipt(id));
    }
}
