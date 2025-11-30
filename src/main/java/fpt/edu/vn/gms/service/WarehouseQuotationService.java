package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.PartUpdateReqDto;
import fpt.edu.vn.gms.dto.response.*;
import org.springframework.data.domain.Page;

public interface WarehouseQuotationService {

    PriceQuotationItemResponseDto rejectItemDuringWarehouseReview(Long itemId, String warehouseNote);

    PriceQuotationItemResponseDto confirmItemDuringWarehouseReview(Long itemId, String warehouseNote);

    Page<PriceQuotationResponseDto> getPendingQuotations(int page, int size);

    void updatePartDuringWarehouseReview(Long itemId, PartUpdateReqDto dto);

    PriceQuotationItemResponseDto createPartDuringWarehouseReview(Long itemId, PartUpdateReqDto dto);
}
