package fpt.edu.vn.gms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
public class PriceQuotationController {

//    private final PriceQuotationService service;
//
//    @PostMapping
//    public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> create(@RequestBody PriceQuotationRequestDto dto) {
//        PriceQuotationResponseDto resp = service.createQuotation(dto);
//        return ResponseEntity.status(200)
//                .body(ApiResponse.created("Created", resp));
//    }
}