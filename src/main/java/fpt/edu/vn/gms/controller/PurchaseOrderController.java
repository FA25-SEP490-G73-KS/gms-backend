package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PurchaseOrderResponseDto;
import fpt.edu.vn.gms.service.PurchaseOrderService;
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

import static fpt.edu.vn.gms.utils.AppRoutes.PURCHASE_ORDERS_PREFIX;

@Tag(name = "purchase-orders", description = "Quản lý đơn đặt hàng từ nhà cung cấp")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = PURCHASE_ORDERS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PurchaseOrderController {

        private final PurchaseOrderService purchaseOrderService;

        @PostMapping("/from-request/{requestId}")
        @Operation(summary = "Tạo đơn đặt hàng từ yêu cầu", description = "Tạo một đơn đặt hàng mới từ một yêu cầu mua hàng đã có.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo đơn đặt hàng thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy yêu cầu mua hàng", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> createPurchaseOrderFromRequest(
                        @PathVariable Long requestId) {

                PurchaseOrderResponseDto response = purchaseOrderService.createFromPurchaseRequest(requestId);

                return ResponseEntity.status(201)
                                .body(ApiResponse.success("Phiếu mua hàng " + response.getCode() + " đã được tạo!!!",
                                                response));
        }

        @GetMapping
        @Operation(summary = "Lấy tất cả đơn đặt hàng", description = "Lấy danh sách tất cả các đơn đặt hàng với phân trang.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách đơn đặt hàng thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Page<PurchaseOrderResponseDto>>> getAllPurchaseOrders(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size) {

                Page<PurchaseOrderResponseDto> responseDtos = purchaseOrderService.getAll(page, size);

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Thành công!!", responseDtos));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Lấy đơn đặt hàng theo ID", description = "Lấy thông tin chi tiết của một đơn đặt hàng bằng ID.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy chi tiết đơn đặt hàng thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy đơn đặt hàng", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> getPurchaseOrderById(
                        @PathVariable Long id) {

                PurchaseOrderResponseDto response = purchaseOrderService.getById(id);

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Chi tiết phiếu mua hàng!!!", response));
        }

}
