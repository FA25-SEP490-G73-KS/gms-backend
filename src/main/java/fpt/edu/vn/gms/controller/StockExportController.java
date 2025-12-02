package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.ExportItemRequest;
import fpt.edu.vn.gms.dto.request.StockExportCreateDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.StockExportItemResponse;
import fpt.edu.vn.gms.dto.response.StockExportResponse;
import fpt.edu.vn.gms.service.StockExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static fpt.edu.vn.gms.utils.AppRoutes.STOCK_EXPORTS_PREFIX;

@Tag(name = "stock-exports", description = "Quản lý xuất kho linh kiện và phụ tùng")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = STOCK_EXPORTS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StockExportController {

        private final StockExportService stockExportService;

        @GetMapping()
        @Operation(summary = "Lấy báo giá xuất kho", description = "Lấy danh sách các báo giá đang trong trạng thái xuất kho với phân trang.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách báo giá thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Page<StockExportResponse>>> getStockOutQuotations(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size) {

                Page<StockExportResponse> quotations = stockExportService.getExportingQuotations(page, size);
                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Danh sách stock-out", quotations));
        }

        @GetMapping("/quotation/{id}")
        @Operation(summary = "Lấy báo giá xuất kho theo ID", description = "Lấy thông tin chi tiết của một báo giá đang xuất kho bằng ID.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy chi tiết báo giá thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy báo giá", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<List<StockExportItemResponse>>> getStockOutQuotationById(
                        @PathVariable Long id) {

                List<StockExportItemResponse> quotation = stockExportService.getExportingQuotationById(id);
                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Chi tiết stock-out", quotation));
        }

        @PostMapping
        @Operation(
                summary = "Tạo phiếu xuất kho",
                description = """
                API dùng để tạo phiếu xuất kho trong các trường hợp:
                - Xuất kho phục vụ sửa chữa nội bộ
                - Xuất kho do nhân viên làm hỏng (sẽ tự động tạo deduction)
                
                **Nghiệp vụ:**
                - Trừ tồn kho của từng linh kiện
                - Tạo danh sách StockExportItem
                - Nếu lý do = `employee_damage` thì bắt buộc gửi `damagedById`
                - Tự động tạo bản ghi Deduction cho nhân viên gây lỗi
                """
        )
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Tạo phiếu xuất kho thành công",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponse.class))
                ),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Dữ liệu đầu vào không hợp lệ",
                        content = @Content(schema = @Schema(hidden = true))
                ),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "Không tìm thấy dữ liệu liên quan (nhân viên, part...)",
                        content = @Content(schema = @Schema(hidden = true))
                ),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "Lỗi hệ thống",
                        content = @Content(schema = @Schema(hidden = true))
                )
        })
        public ResponseEntity<ApiResponse<?>> createStockExport(
                @RequestBody @Valid StockExportCreateDto dto
        ) {
                return ResponseEntity.ok(
                        ApiResponse.success("Tạo phiếu xuất kho thành công",
                                stockExportService.createExport(dto))
                );
        }

        @PostMapping("/item/{itemId}/export")
        @Operation(summary = "Xuất kho một mục", description = "Thực hiện xuất kho cho một mục cụ thể trong báo giá.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xuất kho thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy mục báo giá", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<StockExportItemResponse>> exportItem(
                        @PathVariable Long itemId,
                        @RequestBody ExportItemRequest request) {
                StockExportItemResponse response = stockExportService.exportItem(
                                itemId,
                                request.getQuantity(),
                                request.getReceiverId());

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Xuất kho thành công", response));
        }

        @GetMapping("/item/{exportItemId}")
        @Operation(summary = "Lấy chi tiết dòng xuất kho", description = "Lấy chi tiết một dòng xuất kho theo exportItemId.")
        public ResponseEntity<ApiResponse<StockExportItemResponse>> getExportItemById(@PathVariable Long exportItemId) {
                StockExportItemResponse response = stockExportService.getExportItemById(exportItemId);
                return ResponseEntity.ok(ApiResponse.success("Chi tiết dòng xuất kho", response));
        }

}