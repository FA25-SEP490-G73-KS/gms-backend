package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.ChangeQuotationStatusReqDto;
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

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> updateItems(
            @PathVariable Long id,
            @RequestBody PriceQuotationRequestDto dto) {

        PriceQuotationResponseDto response = priceQuotationService.updateQuotationItems(id, dto);

        return ResponseEntity.ok(ApiResponse.success("Cập nhật báo giá thành công!", response));
    }

    @GetMapping("/{quotationId}")
    public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> getById(@PathVariable Long quotationId) {
        PriceQuotationResponseDto response = priceQuotationService.getById(quotationId);
        return ResponseEntity.ok(ApiResponse.success("Lấy báo giá thành công!", response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> updateQuotationStatusManual(
            @PathVariable Long id,
            @RequestBody ChangeQuotationStatusReqDto dto
    ) {
         PriceQuotationResponseDto responseDto = priceQuotationService.updateQuotationStatusManual(id, dto);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Successfully!!", responseDto));
    }

    @PostMapping("/{id}/customer-confirm")
    public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> confirmByCustomer(
            @PathVariable Long id
    ) {

        PriceQuotationResponseDto response = priceQuotationService.confirmQuotationByCustomer(id);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Successfully!!", response));
    }

    @PostMapping("/{id}/customer-reject")
    public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> rejectByCustomer(
            @PathVariable Long id,
            @RequestBody String note
    ) {

        PriceQuotationResponseDto response = priceQuotationService.rejectQuotationByCustomer(id, note);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Successfully!!", response));
    }


    
}