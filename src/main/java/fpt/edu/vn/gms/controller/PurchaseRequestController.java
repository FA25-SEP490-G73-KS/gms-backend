package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.PartRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
public class PurchaseRequestController {
//
//    private final PurchaseRequestService purchaseRequestService;
//
//    // Lấy danh sách tất cả phiếu yêu cầu
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<PurchaseRequestResponseDto>>> getAll() {
//        return ResponseEntity.status(200)
//                .body(ApiResponse.success("Get all successully", purchaseRequestService.getAllPurchaseRequests()));
//    }
//
//    // Lấy chi tiết phiếu yêu cầu
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<PurchaseRequestResponseDto>> getById(@PathVariable Long id) {
//        return ResponseEntity.status(200)
//                .body(ApiResponse.success("Successfully", purchaseRequestService.getPurchaseRequestById(id)));
//    }
//
//    // Kho xác nhận item (cần sửa bởi vì cần update part)
//    @PutMapping("/items/{itemId}/confirm")
//    public ResponseEntity<PurchaseRequestResponseDto> confirmItem(
//            @PathVariable Long itemId,
//            @RequestBody PartRequestDto partRequestDto
//    ) {
//        return ResponseEntity.ok(purchaseRequestService.confirmPurchaseRequestItem(itemId, partRequestDto));
//    }
//
//    // Kho từ chối item
//    @PutMapping("/items/{itemId}/reject")
//    public ResponseEntity<ApiResponse<PurchaseRequestResponseDto>> rejectItem(
//            @PathVariable Long itemId,
//            @RequestParam(required = false) String note
//    ) {
//        return ResponseEntity.status(200)
//                .body(ApiResponse.success("Reject successfully", purchaseRequestService.rejectPurchaseRequestItem(itemId, note)));
//    }
}
