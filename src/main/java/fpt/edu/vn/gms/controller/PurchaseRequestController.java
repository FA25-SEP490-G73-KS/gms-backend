package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.request.PurchaseRequestCreateDto;
import fpt.edu.vn.gms.dto.request.StockReceiveRequest;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import fpt.edu.vn.gms.service.StockReceiptService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = AppRoutes.PURCHASE_REQUEST_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "${fe-local-host}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "purchase-requests", description = "Quản lý yêu cầu mua hàng")
public class PurchaseRequestController {

    private final PurchaseRequestService prService;
    private final StockReceiptService stockReceiptService;
    private final ObjectMapper objectMapper;

//    @Operation(
//            summary = "Lấy danh sách yêu cầu mua hàng (PR)",
//            description = "Trả về danh sách PR có phân trang, sắp xếp theo ngày tạo giảm dần."
//    )
//    @ApiResponses({
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách PR thành công",
//                    content = @Content(schema = @Schema(implementation = PurchaseRequestResponseDto.class))),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server nội bộ")
//    })
//    @GetMapping
//    public ResponseEntity<ApiResponse<Page<PurchaseRequestResponseDto>>> listPR(
//            @Parameter(description = "Số trang (bắt đầu từ 0)")
//            @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "Số lượng bản ghi mỗi trang")
//            @RequestParam(defaultValue = "6") int size
//    ) {
//
//        log.info("[PR] Fetch list page={} size={}", page, size);
//
//        return ResponseEntity.ok(ApiResponse.success("Danh sách PR", prService.getPurchaseRequests(page, size)));
//    }


//    @Operation(
//            summary = "Lấy danh sách chi tiết các mục trong PR",
//            description = "Trả về toàn bộ item thuộc một yêu cầu mua hàng."
//    )
//    @ApiResponses({
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy chi tiết item thành công",
//                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PurchaseRequestItemResponseDto.class)))),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy PR"),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server nội bộ")
//    })
//    @GetMapping("/{prId}")
//    public ResponseEntity<ApiResponse<PrDetailInfoReviewDto>> prItems(
//            @Parameter(description = "ID của Purchase Request cần lấy chi tiết")
//            @PathVariable Long prId) {
//
//        log.info("[PR] Fetch items for PR {}", prId);
//
//        return ResponseEntity.ok(ApiResponse.success("Chi tiết PR items", prService.getPurchaseRequestItems(prId)));
//    }

//    @Operation(
//            summary = "Chi tiết từng đơn mua hàng trong pr"
//    )
//    @ApiResponses({
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy chi tiết item thành công"),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy PR"),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server nội bộ")
//    })
//    @GetMapping("/item/{itemId}")
//    public ResponseEntity<ApiResponse<PurchaseRequestItemResponseDto>> prItem(
//            @Parameter(description = "ID của Purchase Request Item cần lấy chi tiết")
//            @PathVariable Long itemId) {
//
//        log.info("[PR] Fetch items for PR {}", itemId);
//
//        return ResponseEntity.ok(ApiResponse.success("Chi tiết PR item", prService.getItem(itemId)));
//    }

//    @PostMapping
//    @Operation(
//            summary = "Tạo yêu cầu mua hàng",
//            description = """
//                API dùng để tạo phiếu yêu cầu mua hàng trong các trường hợp:
//                - Kho hết linh kiện
//                - Cần nhập mới để bổ sung tồn kho
//                - Báo giá yêu cầu nhập hàng (không đủ tồn kho)
//
//                **Nghiệp vụ xử lý:**
//                - Tạo mới PurchaseRequest
//                - Thêm danh sách PurchaseRequestItem
//                - Tính tổng estimatedAmount dựa trên purchasePrice của linh kiện
//                - Không tạo deduction
//                - Không liên quan đến xuất kho
//                """
//    )
//    @ApiResponses(value = {
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                    responseCode = "200",
//                    description = "Tạo phiếu yêu cầu mua hàng thành công",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ApiResponse.class))
//            ),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                    responseCode = "400",
//                    description = "Dữ liệu đầu vào không hợp lệ",
//                    content = @Content(schema = @Schema(hidden = true))
//            ),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                    responseCode = "404",
//                    description = "Không tìm thấy dữ liệu liên quan (nhân viên, part...)",
//                    content = @Content(schema = @Schema(hidden = true))
//            ),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                    responseCode = "500",
//                    description = "Lỗi hệ thống",
//                    content = @Content(schema = @Schema(hidden = true))
//            )
//    })
//    public ResponseEntity<ApiResponse<?>> createPurchaseRequest(
//            @RequestBody @Valid PurchaseRequestCreateDto dto
//    ) {
//        return ResponseEntity.ok(
//                ApiResponse.success("Tạo yêu cầu mua hàng thành công",
//                        prService.createRequest(dto))
//        );
//    }

}
