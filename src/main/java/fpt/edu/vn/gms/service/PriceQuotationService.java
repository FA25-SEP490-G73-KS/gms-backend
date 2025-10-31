package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.WarehouseReviewStatus;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;

public interface PriceQuotationService {

    PriceQuotationResponseDto createQuotation(PriceQuotationRequestDto dto);
    PriceQuotationResponseDto getById(Long id);
    void warehouseReviewItem(Long itemId, WarehouseReviewStatus status, String note);
    void confirmByCustomer(Long quotationId, boolean accepted);
}
