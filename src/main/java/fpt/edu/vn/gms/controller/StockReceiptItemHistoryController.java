package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.StockReceiptItemHistoryListResponse;
import fpt.edu.vn.gms.dto.response.StockReceiptItemHistoryPaymentResponse;
import fpt.edu.vn.gms.service.StockReceiptItemHistoryService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppRoutes.ACCOUNTING_STOCK_RECEIPT_PREFIX + "/receipt-history")
@RequiredArgsConstructor
@Tag(name = "stock-receipt-item-history-controller", description = "Lịch sử nhập kho")
public class StockReceiptItemHistoryController {

    private final StockReceiptItemHistoryService historyService;

    @GetMapping
    @Operation(summary = "Danh sách lịch sử nhập kho")
    public ApiResponse<Page<StockReceiptItemHistoryListResponse>> getAllHistories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockReceiptItemHistoryListResponse> result = historyService.getHistories(keyword, supplierId, fromDate, toDate, pageable);
        return ApiResponse.success("Lấy danh sách lịch sử nhập kho thành công", result);
    }

    @GetMapping("/{id}/payment-detail")
    @Operation(summary = "Chi tiết thanh toán lịch sử nhập kho")
    public ApiResponse<StockReceiptItemHistoryPaymentResponse> getPaymentDetail(@PathVariable Long id) {
        return ApiResponse.success("Lấy thông tin thanh toán thành công", historyService.getPaymentDetail(id));
    }
}
