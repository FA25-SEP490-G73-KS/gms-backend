package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.ConfirmPRItemRequest;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static fpt.edu.vn.gms.utils.AppRoutes.PURCHASE_REQUESTS_PREFIX;

@Tag(name = "purchase-requests", description = "Quản lý yêu cầu mua hàng từ kho")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = PURCHASE_REQUESTS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService prService;

    @GetMapping
    @Operation(summary = "Liệt kê các yêu cầu mua hàng", description = "Lấy danh sách tất cả các yêu cầu mua hàng với phân trang.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách yêu cầu mua hàng thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<Page<PurchaseRequestResponseDto>>> listPR(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(ApiResponse.success("Danh sách PR", prService.getPurchaseRequests(page, size)));
    }

    @GetMapping("/{prId}/items")
    @Operation(summary = "Liệt kê các mục trong yêu cầu mua hàng", description = "Lấy danh sách chi tiết các mục trong một yêu cầu mua hàng cụ thể.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy chi tiết các mục thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy yêu cầu mua hàng", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<List<PurchaseRequestItemResponseDto>>> listPRItems(@PathVariable Long prId) {
        return ResponseEntity.ok(ApiResponse.success("Chi tiết PR items", prService.getPurchaseRequestItems(prId)));
    }

    @PostMapping("/items/{itemId}/confirm")
    @Operation(summary = "Xác nhận một mục trong yêu cầu mua hàng", description = "Xác nhận hoặc từ chối một mục trong yêu cầu mua hàng và ghi chú.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xác nhận thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy mục yêu cầu mua hàng", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<PurchaseRequestItemResponseDto>> confirmPRItem(
            @PathVariable Long itemId,
            @RequestBody ConfirmPRItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Xác nhận PR item",
                prService.confirmPurchaseRequestItem(itemId, request.isApproved(), request.getNote())));
    }

}
