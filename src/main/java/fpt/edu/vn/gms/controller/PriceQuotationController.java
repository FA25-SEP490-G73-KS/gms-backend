package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.service.PriceQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
public class PriceQuotationController {

    private final PriceQuotationService priceQuotationService;

    // === Cập nhật / thêm danh sách item cho báo giá ===
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> updateItems(
            @PathVariable Long id,
            @RequestBody PriceQuotationRequestDto dto) {

        PriceQuotationResponseDto response = priceQuotationService.updateQuotationItems(id, dto);

        return ResponseEntity.ok(ApiResponse.success("Cập nhật báo giá thành công!", response));
    }

    // === Lấy chi tiết báo giá ===
    @GetMapping("/{quotationId}")
    public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> getById(@PathVariable Long quotationId) {
        PriceQuotationResponseDto response = priceQuotationService.getById(quotationId);
        return ResponseEntity.ok(ApiResponse.success("Lấy báo giá thành công!", response));
    }
}