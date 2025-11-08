package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.WarehouseReviewItemDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;

import java.util.List;

public interface WarehouseQuotationService {

    PriceQuotationItemResponseDto updateWarehouseReview(Long quotationId, WarehouseReviewItemDto reviewItems);

    List<PriceQuotationResponseDto> getPendingQuotations();

}
