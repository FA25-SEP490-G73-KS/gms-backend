package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseRequestResponseDto>>> getAllPurchaseRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        Page<PurchaseRequestResponseDto> response = purchaseRequestService.getAllRequests(page, size);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Success", response));
    }

    @PutMapping("/{id}/items/{itemId}/approve")
    public ResponseEntity<?> approvePurchaseRequest(
            @PathVariable Long id,
            @PathVariable Long itemId
    ) {

        purchaseRequestService.approveRequestItem(id, itemId);
        return ResponseEntity.ok("Phiếu yêu cầu mua hàng được phê duyệt!!");
    }

    @PutMapping("/{id}/items/{itemId}/reject")
    public ResponseEntity<?> rejectPurchaseRequest(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @RequestBody String reason
    ) {

        purchaseRequestService.rejectRequestItem(id, itemId, reason);

        return ResponseEntity.ok("Phiếu yêu cầu mua hàng bị từ chối!!");
    }


}
