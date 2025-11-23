package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = AppRoutes.PURCHASE_REQUEST_ITEMS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "${fe-local-host}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "purchase-request-items", description = "Quản lý item trong yêu cầu mua hàng")
public class PurchaseRequestItemController {

    private final PurchaseRequestService purchaseRequestService;

    // -----------------------
    // MANAGER REVIEW ITEM
    // -----------------------
    @PatchMapping("/{itemId}/review")
    @Operation(summary = "Xác nhận hoặc từ chối item trong Purchase Request")
    public ResponseEntity<ApiResponse<PurchaseRequestItemResponseDto>> reviewItem(
            @PathVariable Long itemId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String note
    ) {
        log.info("[PR-ITEM] Manager reviewing itemId={} approved={} note={}", itemId, approved, note);

        PurchaseRequestItemResponseDto dto =
                purchaseRequestService.reviewItem(itemId, approved, note);

        return ResponseEntity.ok(ApiResponse.success("Review item thành công", dto));
    }
}
