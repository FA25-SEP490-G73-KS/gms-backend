package fpt.edu.vn.gms.controller;

import lombok.RequiredArgsConstructor;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.service.QuotaitonItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quotation-items")
@RequiredArgsConstructor
public class QuotationItemController {

    private final QuotaitonItemService quotaitonItemService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PriceQuotationItemResponseDto>> getById(
            @PathVariable Long id
    ) {

        PriceQuotationItemResponseDto response = quotaitonItemService.getQuotationItem(id);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Lấy chi tiết mục báo giá thành công!", response));
    }



}
