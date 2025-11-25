package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.PayInvoiceRequestDto;
import fpt.edu.vn.gms.dto.TransactionResponseDto;
import fpt.edu.vn.gms.dto.request.CreateDebtFromPaymentReq;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.DebtResDto;
import fpt.edu.vn.gms.dto.response.InvoiceDetailResDto;
import fpt.edu.vn.gms.dto.response.InvoiceListResDto;
import fpt.edu.vn.gms.service.InvoiceService;
import fpt.edu.vn.gms.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static fpt.edu.vn.gms.utils.AppRoutes.INVOICE_PREFIX;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = INVOICE_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InvoiceController {

        InvoiceService invoiceService;
        TransactionService transactionService;

        @GetMapping
        @Operation(summary = "Lấy danh sách phiếu thanh toán (phân trang + sort)")
        public ResponseEntity<ApiResponse<Page<InvoiceListResDto>>> getPaymentList(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size,
                        @RequestParam(defaultValue = "createdAt,desc") String sort) {
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Lấy danh sách phiếu thanh toán thành công",
                                                invoiceService.getInvoiceList(page, size, sort)));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Lấy chi tiết phiếu thanh toán")
        public ResponseEntity<ApiResponse<InvoiceDetailResDto>> getPaymentDetail(@PathVariable Long id) {
                var result = invoiceService.getInvoiceDetail(id);
                return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết phiếu thanh toán thành công", result));
        }

        @PostMapping
        @Operation(summary = "Tạo phiếu thanh toán", description = "Tạo phiếu thanh toán cho phiếu dịch vụ và báo giá đã cho.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Tạo phiếu thanh toán thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ hoặc báo giá", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(hidden = true)))
        })
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void createPayment(
                        @RequestParam Long serviceTicketId,
                        @RequestParam Long quotationId) {
                invoiceService.createInvoice(serviceTicketId, quotationId);
        }

        @PostMapping("/{id}/pay")
        @Operation(summary = "Tạo giao dịch thanh toán", description = "Tạo giao dịch thanh toán cho hóa đơn (Tiền mặt hoặc Chuyển khoản)")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tạo giao dịch thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu thanh toán", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<TransactionResponseDto>> payInvoice(
                        @PathVariable Long id,
                        @RequestBody @Valid PayInvoiceRequestDto request) throws Exception {
                return ResponseEntity.ok(ApiResponse.success("Tạo giao dịch thành công",
                                invoiceService.payInvoice(id, request)));
        }

        @PostMapping("/{paymentId}/debt")
        @Operation(summary = "Tạo công nợ mới từ phiếu thanh toán", description = "Dùng khi bảng giao dịch đã có (PayOS callback xong). "
                        +
                        "API sẽ tính công nợ mới = finalAmount - tổng các transaction SUCCESS, " +
                        "tạo Debt và trả về thông tin công nợ cùng ngày hẹn trả.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo công nợ thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu thanh toán / khách hàng liên quan")
        })
        public ResponseEntity<ApiResponse<?>> createDebtFromPayment(
                        @PathVariable Long paymentId,
                        @RequestBody @Validated CreateDebtFromPaymentReq request) {
                log.info("Request create debt from paymentId={} dueDate={}", paymentId, request.getDueDate());

                DebtResDto debt = invoiceService.createDebtFromInvoice(paymentId, request.getDueDate());

                if (debt == null) {
                        return ResponseEntity.ok(ApiResponse.success("Không còn công nợ cần tạo", null));
                }

                return ResponseEntity
                                .status(201)
                                .body(ApiResponse.success("Tạo công nợ thành công", debt));
        }

}
