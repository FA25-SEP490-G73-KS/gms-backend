package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.WarehouseReviewItemDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.service.WarehouseQuotationService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/quotations")
@RequiredArgsConstructor
public class WarehouseQuotationController {

    private final WarehouseQuotationService warehouseQuotationService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<PriceQuotationResponseDto>>> getPendingQuotations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        Page<PriceQuotationResponseDto> quotations = warehouseQuotationService.getPendingQuotations(page, size);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Success", quotations));
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<ApiResponse<PriceQuotationItemResponseDto>> reviewSingleItem(
            @PathVariable Long id,
            @RequestBody WarehouseReviewItemDto requestDto
    ) {
        PriceQuotationItemResponseDto updatedQuotation =
                warehouseQuotationService.updateWarehouseReview(id, requestDto);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Success", updatedQuotation));
    }

}
