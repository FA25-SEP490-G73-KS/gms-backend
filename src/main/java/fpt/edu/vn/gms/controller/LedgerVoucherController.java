package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.dto.request.ApproveVoucherRequest;
import fpt.edu.vn.gms.dto.request.CreateVoucherRequest;
import fpt.edu.vn.gms.dto.request.UpdateVoucherRequest;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.LedgerVoucherDetailResponse;
import fpt.edu.vn.gms.dto.response.LedgerVoucherListResponse;
import fpt.edu.vn.gms.service.LedgerVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ledger-vouchers")
@RequiredArgsConstructor
public class LedgerVoucherController {

    private final LedgerVoucherService ledgerVoucherService;

    @PostMapping("/manual")
    public ResponseEntity<ApiResponse<LedgerVoucherDetailResponse>> createManualVoucher(@RequestBody CreateVoucherRequest request) {
        LedgerVoucherDetailResponse response = ledgerVoucherService.createManualVoucher(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo phiếu thu/chi thủ công thành công", response));
    }

    @PostMapping(value = "/receipt-payment/{receiptHistoryId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LedgerVoucherDetailResponse>> createPaymentVoucherFromReceiptHistory(
            @PathVariable Long receiptHistoryId,
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        CreateVoucherRequest request = mapper.readValue(data, CreateVoucherRequest.class);

        LedgerVoucherDetailResponse response =
                ledgerVoucherService.createPaymentVoucherFromReceiptHistory(receiptHistoryId, request, file);

        return ResponseEntity.ok(ApiResponse.success("Tạo phiếu chi thanh toán nhập kho thành công", response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<LedgerVoucherDetailResponse>> updateVoucher(
            @PathVariable Long id,
            @RequestBody UpdateVoucherRequest request) {
        LedgerVoucherDetailResponse response = ledgerVoucherService.updateVoucher(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật phiếu thu/chi thành công", response));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<LedgerVoucherDetailResponse>> approveVoucher(
            @PathVariable Long id,
            @RequestBody ApproveVoucherRequest request) {
        LedgerVoucherDetailResponse response = ledgerVoucherService.approveVoucher(id, request);
        return ResponseEntity.ok(ApiResponse.success("Duyệt phiếu thu/chi thành công", response));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<LedgerVoucherDetailResponse>> rejectVoucher(@PathVariable Long id) {
        LedgerVoucherDetailResponse response = ledgerVoucherService.rejectVoucher(id);
        return ResponseEntity.ok(ApiResponse.success("Từ chối phiếu thu/chi thành công", response));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<LedgerVoucherDetailResponse>> payVoucher(@PathVariable Long id) {
        LedgerVoucherDetailResponse response = ledgerVoucherService.payVoucher(id);
        return ResponseEntity.ok(ApiResponse.success("Thanh toán phiếu thu/chi thành công", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LedgerVoucherListResponse>>> getVoucherList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<LedgerVoucherListResponse> result = ledgerVoucherService.getVoucherList(
                keyword, type, status, fromDate, toDate, supplierId, employeeId, pageable);

        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách phiếu thu/chi thành công", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LedgerVoucherDetailResponse>> getVoucherDetail(@PathVariable Long id) {
        LedgerVoucherDetailResponse response = ledgerVoucherService.getVoucherDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết phiếu thu/chi thành công", response));
    }
}
