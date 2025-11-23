package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.PartDuringReviewDto;
import fpt.edu.vn.gms.dto.request.WarehouseReviewItemDto;
import fpt.edu.vn.gms.dto.response.*;
import org.springframework.data.domain.Page;

public interface WarehouseQuotationService {

    PriceQuotationItemResponseDto rejectItemDuringWarehouseReview(Long itemId, String warehouseNote);

    Page<PriceQuotationResponseDto> getPendingQuotations(int page, int size);

    PartReqDto updatePartDuringWarehouseReview(Long itemId, PartDuringReviewDto dto);

    PartReqDto createPartDuringWarehouseReview(Long itemId, PartDuringReviewDto dto);
}
