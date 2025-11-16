package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.ConfirmPRItemRequest;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.dto.response.StockReceiptItemResponseDto;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import fpt.edu.vn.gms.service.StockReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService prService;
    private final StockReceiptService stockReceiptService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseRequestResponseDto>>> listPR(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success("Danh sách PR", prService.getPurchaseRequests(page, size)));
    }

    @GetMapping("/{prId}")
    public ResponseEntity<ApiResponse<List<PurchaseRequestItemResponseDto
                >>> listPRItems(@PathVariable Long prId) {
        return ResponseEntity.ok(ApiResponse.success("Chi tiết PR items", prService.getPurchaseRequestItems(prId)));
    }

    @PostMapping("/items/{itemId}/confirm")
    public ResponseEntity<ApiResponse<PurchaseRequestItemResponseDto>> confirmPRItem(
            @PathVariable Long itemId,
            @RequestBody ConfirmPRItemRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Xác nhận PR item", prService.confirmPurchaseRequestItem(itemId, request.isApproved(), request.getNote())));
    }

    @PostMapping("/items/{prItemId}/receive")
    public ResponseEntity<ApiResponse<StockReceiptItemResponseDto>> receiveItem(
            @PathVariable Long prItemId
           ) {

        StockReceiptItemResponseDto responseDto = stockReceiptService.receiveItem(prItemId);

        return ResponseEntity.status(200)
                .body(ApiResponse.success(" Nhập kho thành công", responseDto));

    }

}
