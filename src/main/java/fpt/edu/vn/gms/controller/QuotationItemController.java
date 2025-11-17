package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.service.QuotaitonItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
public class QuotationItemController {

    private final QuotaitonItemService quotaitonItemService;

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

}
