package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.request.StockReceiveRequest;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final ObjectMapper objectMapper;

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
            summary = "Nhập kho cho từng item trong Purchase Request",
            description = """
                Chức năng nhập kho linh kiện theo yêu cầu mua hàng (PR).
                
                - Upload file chứng từ (ảnh, pdf, ...).
                - Nhập số lượng thực nhận (có thể nhận từng phần).
                - Cập nhật tồn kho.
                - Gửi thông báo đến Service Advisor & Kế toán.
                
                **Multipart form-data gồm 2 phần:**
                
                - `data`: JSON string (StockReceiveRequest)
                - `file`: File upload (optional)
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nhập kho thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy PR item")
    })
    @PostMapping(
            path = "/items/{prItemId}/receive",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<StockReceiptItemResponseDto>> receive(
            @PathVariable Long prItemId,
            @RequestPart("data") String jsonData,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @CurrentUser Employee employee
    ) throws Exception {

        StockReceiveRequest request = objectMapper.readValue(jsonData, StockReceiveRequest.class);

        log.info("[RECEIVE] prItemId={} data={} file={}", prItemId, request, file != null);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Nhập kho thành công",
                        stockReceiptService.receiveItem(prItemId, request, file, employee)
                )
        );
    }

}
