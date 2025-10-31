package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.WarehouseReviewStatus;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;

public interface PriceQuotationService {

    PriceQuotationResponseDto updateQuotationItems(PriceQuotationRequestDto dto);

    PriceQuotationResponseDto getById(Long id);
}
