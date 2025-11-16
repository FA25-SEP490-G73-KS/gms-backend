package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.WarehouseReviewItemDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.dto.response.StockExportItemResponse;
import fpt.edu.vn.gms.dto.response.StockExportResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WarehouseQuotationService {

    PriceQuotationItemResponseDto updateWarehouseReview(Long quotationId, WarehouseReviewItemDto reviewItems);

    Page<PriceQuotationResponseDto> getPendingQuotations(int page, int size);
}
