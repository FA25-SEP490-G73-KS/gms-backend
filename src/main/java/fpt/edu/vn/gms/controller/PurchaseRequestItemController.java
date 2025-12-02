package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.PurchaseRequestItemService;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = AppRoutes.PURCHASE_REQUEST_ITEMS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "${fe-local-host}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "purchase-request-items", description = "Quản lý item trong yêu cầu mua hàng")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PurchaseRequestItemController {

    PurchaseRequestService purchaseRequestService;
    PurchaseRequestItemService purchaseRequestItemService;


    @PatchMapping("/{itemId}/review")
    @Operation(summary = "Xác nhận hoặc từ chối item trong Purchase Request")
    public ResponseEntity<ApiResponse<PurchaseRequestItemResponseDto>> reviewItem(
            @PathVariable Long itemId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String note,
            @CurrentUser Employee currentUser
    ) {
        log.info("[PR-ITEM] Manager reviewing itemId={} approved={} note={}", itemId, approved, note);

        PurchaseRequestItemResponseDto dto =
                purchaseRequestService.reviewItem(itemId, approved, note, currentUser);

        return ResponseEntity.ok(ApiResponse.success("Review item thành công", dto));
    }

    @GetMapping("/{itemId}")
    @Operation(
            summary = "Lấy thông tin request item chi tiết để phê duyệt",
            description = "Quản lý sẽ phê duyệt purchase request"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách item thành công",
                    content = @Content(
                            schema = @Schema(implementation = PurchaseRequestItemDetailDto.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy request",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    public ResponseEntity<ApiResponse<List<PurchaseRequestItemDetailDto>>> getItems(
            @PathVariable Long itemId
    ) {
        List<PurchaseRequestItemDetailDto> items =
                purchaseRequestItemService.getItemsByPurchaseRequestId(itemId);

        return ResponseEntity.ok(ApiResponse.success("Yêu cầu mua hàng chi tiết", items));
    }


}