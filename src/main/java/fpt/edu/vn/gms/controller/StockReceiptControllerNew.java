package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.dto.request.CreateReceiptItemHistoryRequest;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.service.StockReceiptService;
import fpt.edu.vn.gms.service.impl.FileStorageService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(AppRoutes.ACCOUNTING_STOCK_RECEIPT_PREFIX)
@RequiredArgsConstructor
@Tag(name = "stock-receipt-controller", description = "Quản lý phiếu nhập kho và lịch sử nhận hàng")
public class StockReceiptControllerNew {

    private final @Qualifier("stockReceiptServiceImplNew") StockReceiptService stockReceiptService;
    private final FileStorageService fileStorageService;

    @GetMapping
    @Operation(summary = "Danh sách phiếu nhập kho")
    public ApiResponse<Page<StockReceiptListResponse>> getReceipts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockReceiptListResponse> result = stockReceiptService.getReceipts(status, keyword, fromDate, toDate,
                supplierId, pageable);
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
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockReceiptItemResponse> result = stockReceiptService.getReceiptItems(id, pageable);
        return ApiResponse.success("Lấy danh sách dòng nhập kho thành công", result);
    }

    @GetMapping("/receipt-items/{itemId}")
    @Operation(summary = "Chi tiết 1 dòng nhập kho")
    public ApiResponse<StockReceiptItemDetailResponse> getReceiptItemDetail(@PathVariable Long itemId) {
        return ApiResponse.success("Lấy chi tiết dòng nhập kho thành công",
                stockReceiptService.getReceiptItemDetail(itemId));
    }

    @GetMapping("/receipt-items/{itemId}/history")
    @Operation(summary = "Lịch sử nhận hàng của 1 dòng nhập kho")
    public ApiResponse<StockReceiptItemDetailResponse> getReceiptItemHistory(@PathVariable Long itemId) {
        return ApiResponse.success("Lấy lịch sử nhập kho thành công",
                stockReceiptService.getReceiptItemHistory(itemId));
    }

    @PostMapping(value = "/receipt-items/{itemId}/receive", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Nhận hàng (nhập kho) cho 1 dòng nhập kho")
    public ApiResponse<StockReceiptItemDetailResponse> createReceiptItemHistory(
            @PathVariable Long itemId,
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CreateReceiptItemHistoryRequest request = mapper.readValue(data, CreateReceiptItemHistoryRequest.class);
        return ApiResponse.success("Nhập kho thành công",
                stockReceiptService.createReceiptItemHistory(itemId, request, file));
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

    @GetMapping("/receipt-history/{historyId}/attachment")
    @Operation(summary = "Tải xuống file đính kèm của lịch sử nhập kho")
    public ResponseEntity<org.springframework.core.io.Resource> downloadReceiptHistoryAttachment(
            @PathVariable Long historyId) {
        StockReceiptItemHistoryDetailResponse history = stockReceiptService.getReceiptItemHistoryDetail(historyId);

        if (history.getAttachmentUrl() == null || history.getAttachmentUrl().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            org.springframework.core.io.Resource resource = fileStorageService.download(history.getAttachmentUrl());

            String contentType = "application/octet-stream";
            String fileName = history.getAttachmentUrl();
            if (fileName.contains("/")) {
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }

            // Xác định content type dựa trên extension
            String lowerFileName = fileName.toLowerCase();
            if (lowerFileName.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (lowerFileName.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerFileName.endsWith(".doc") || lowerFileName.endsWith(".docx")) {
                contentType = "application/msword";
            } else if (lowerFileName.endsWith(".xls") || lowerFileName.endsWith(".xlsx")) {
                contentType = "application/vnd.ms-excel";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
