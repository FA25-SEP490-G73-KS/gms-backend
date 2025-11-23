package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.dto.response.StockReceiptItemResponseDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = AppRoutes.PURCHASE_REQUEST_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "${fe-local-host}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "purchase-requests", description = "Quản lý yêu cầu mua hàng")
public class PurchaseRequestController {

    private final PurchaseRequestService prService;
    private final StockReceiptService stockReceiptService;

    @Operation(
            summary = "Lấy danh sách yêu cầu mua hàng (PR)",
            description = "Trả về danh sách PR có phân trang, sắp xếp theo ngày tạo giảm dần."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách PR thành công",
                    content = @Content(schema = @Schema(implementation = PurchaseRequestResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server nội bộ")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseRequestResponseDto>>> listPR(
            @Parameter(description = "Số trang (bắt đầu từ 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng bản ghi mỗi trang")
            @RequestParam(defaultValue = "6") int size
    ) {

        log.info("[PR] Fetch list page={} size={}", page, size);

        return ResponseEntity.ok(ApiResponse.success("Danh sách PR", prService.getPurchaseRequests(page, size)));
    }


    @Operation(
            summary = "Lấy danh sách chi tiết các mục trong PR",
            description = "Trả về toàn bộ item thuộc một yêu cầu mua hàng."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy chi tiết item thành công",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PurchaseRequestItemResponseDto.class)))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy PR"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server nội bộ")
    })
    @GetMapping("/{prId}")
    public ResponseEntity<ApiResponse<List<PurchaseRequestItemResponseDto>>> listPRItems(
            @Parameter(description = "ID của Purchase Request cần lấy chi tiết")
            @PathVariable Long prId) {

        log.info("[PR] Fetch items for PR {}", prId);

        return ResponseEntity.ok(ApiResponse.success("Chi tiết PR items", prService.getPurchaseRequestItems(prId)));
    }


    @Operation(
            summary = "Xác nhận nhập kho một item trong PR",
            description = "Kho tiến hành nhập hàng cho một item trong yêu cầu mua hàng."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nhập kho thành công",
                    content = @Content(schema = @Schema(implementation = StockReceiptItemResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy PR Item"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền nhập kho"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server nội bộ")
    })
    @PostMapping("/items/{prItemId}/receive")
    public ResponseEntity<ApiResponse<StockReceiptItemResponseDto>> receiveItem(
            @Parameter(description = "ID của Purchase Request Item cần nhập kho")
            @PathVariable Long prItemId,
            @Parameter(hidden = true) @CurrentUser Employee employee
    ) {

        log.info("[PR] Stock receiving for itemId={} by employee={}", prItemId, employee.getEmployeeId());

        StockReceiptItemResponseDto responseDto = stockReceiptService.receiveItem(prItemId, employee);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Nhập kho thành công", responseDto));
    }

}
