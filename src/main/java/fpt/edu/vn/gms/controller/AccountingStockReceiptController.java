package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.request.ExpenseVoucherCreateRequest;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.ExpenseVoucherService;
import fpt.edu.vn.gms.service.StockReceiptService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = AppRoutes.ACCOUNTING_STOCK_RECEIPT_PREFIX,
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "${fe-local-host}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "accounting-stock-receipts", description = "Kế toán xử lý tiền vật tư")
public class AccountingStockReceiptController {

    private final StockReceiptService stockReceiptService;
    private final ExpenseVoucherService expenseVoucherService;

    // ====== LIST HEADER ======
    @Operation(
            summary = "Danh sách phiếu nhập kho cho kế toán",
            description = """
                Trả về danh sách Stock Receipt (STK) để kế toán theo dõi tiền vật tư.
                Có thể tìm kiếm theo mã phiếu hoặc biển số xe.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
                    content = @Content(schema =
                    @Schema(implementation = StockReceiptResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<fpt.edu.vn.gms.dto.response.ApiResponse<Page<StockReceiptResponseDto>>> listReceipts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(
                fpt.edu.vn.gms.dto.response.ApiResponse.success(
                        "Danh sách phiếu nhập kho",
                        stockReceiptService.getReceiptsForAccounting(page, size, search)
                )
        );
    }

    // ====== LIST ITEMS ======
    @Operation(
            summary = "Lấy danh sách dòng nhập kho theo STK",
            description = "Dùng cho bảng con hiển thị các linh kiện trong một phiếu nhập."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
                    content = @Content(schema =
                    @Schema(implementation = StockReceiptItemResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu nhập kho")
    })
    @GetMapping("/{receiptId}/items")
    public ResponseEntity<fpt.edu.vn.gms.dto.response.ApiResponse<List<StockReceiptItemResponseDto>>> listItems(
            @PathVariable Long receiptId
    ) {
        return ResponseEntity.ok(
                fpt.edu.vn.gms.dto.response.ApiResponse.success(
                        "Danh sách dòng nhập kho",
                        stockReceiptService.getReceiptItems(receiptId)
                )
        );
    }

    // ====== PAY ONE ITEM ======
    @Operation(
            summary = "Kế toán xác nhận thanh toán cho một dòng nhập kho",
            description = """
                Khi kế toán tick 'Xác nhận thanh toán' trên từng dòng:
                - Hệ thống tạo phiếu chi type = NCC
                - Số tiền = quantityReceived * unitPrice
                - Trạng thái phiếu chi = HOÀN TẤT (COMPLETED)
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thanh toán thành công",
                    content = @Content(schema =
                    @Schema(implementation = ExpenseVoucherResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy dòng nhập kho")
    })
    @PostMapping("/items/{itemId}/pay")
    public ResponseEntity<fpt.edu.vn.gms.dto.response.ApiResponse<ExpenseVoucherResponseDto>> payForItem(
            @PathVariable Long itemId,
            @RequestBody ExpenseVoucherCreateRequest request,
            @CurrentUser Employee accountant
    ) {
        log.info("[ACCOUNTING][PAY] itemId={} request={}", itemId, request);

        ExpenseVoucherResponseDto dto =
                expenseVoucherService.payForStockReceiptItem(itemId, request, accountant);

        return ResponseEntity.ok(
                fpt.edu.vn.gms.dto.response.ApiResponse.success("Thanh toán thành công", dto)
        );
    }
}
