package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.PartDuringReviewDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.service.QuotaitonItemService;
import fpt.edu.vn.gms.service.WarehouseQuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import static fpt.edu.vn.gms.utils.AppRoutes.QUOTATION_ITEMS_PREFIX;

@Tag(name = "quotation-items", description = "Quản lý chi tiết các mục trong báo giá")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = QUOTATION_ITEMS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class QuotationItemController {

    private final QuotaitonItemService quotaitonItemService;
    private final WarehouseQuotationService warehouseQuotationService;

    @GetMapping("/{id}")
    @Operation(summary = "Lấy mục báo giá theo ID", description = "Lấy thông tin chi tiết của một mục trong báo giá bằng ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy chi tiết mục báo giá thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy mục báo giá", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<PriceQuotationItemResponseDto>> getById(
            @PathVariable Long id) {

        PriceQuotationItemResponseDto response = quotaitonItemService.getQuotationItem(id);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Lấy chi tiết mục báo giá thành công!", response));
    }

    @PatchMapping("/{itemId}/reject")
    public ResponseEntity<ApiResponse<PriceQuotationItemResponseDto>> rejectItem(
            @PathVariable Long itemId,
            @RequestBody String warehouseNote
    ) {
        PriceQuotationItemResponseDto res = warehouseQuotationService.rejectItemDuringWarehouseReview(itemId, warehouseNote);

        return ResponseEntity.ok(ApiResponse.success("Từ chối item thành công", res));
    }

    @PatchMapping("/{itemId}/confirm/update")
    @Operation(
            summary = "Kho duyệt item báo giá & cập nhật Part",
            description = """
                API này dùng khi KHO duyệt item báo giá.
                - Cập nhật thông tin PART theo dữ liệu kho gửi
                - Merge dữ liệu của Part vào lại PriceQuotationItem
                - Cập nhật trạng thái Duyệt + Ghi chú kho
            """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "200", description = "Duyệt item thành công"),
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "400", description = "Request không hợp lệ",
                    content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "404", description = "Không tìm thấy item hoặc part",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<PartReqDto>> updatePartDuringReview(
            @PathVariable Long itemId,
            @Valid @RequestBody PartDuringReviewDto dto
    ) {

        log.info("API DUYỆT ITEM BÁO GIÁ -> itemId={}, dto={}", itemId, dto);

        PartReqDto updatedPart = warehouseQuotationService.updatePartDuringWarehouseReview(itemId, dto);

        return ResponseEntity.ok(ApiResponse.success("Duyệt item báo giá thành công", updatedPart));
    }

    @PostMapping("/{itemId}/confirm/create")
    public ResponseEntity<ApiResponse<PartReqDto>> createPartDuringReview(
            @PathVariable Long itemId,
            @RequestBody PartDuringReviewDto dto) {

        PartReqDto result = warehouseQuotationService.createPartDuringWarehouseReview(itemId, dto);

        return ResponseEntity.ok(ApiResponse.success("Tạo Part mới thành công", result));
    }

}
