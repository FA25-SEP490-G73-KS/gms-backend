package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;

public interface QuotaitonItemService {

    PriceQuotationItemResponseDto getQuotationItem(Long itemId);

    void deleteQuotationItem(Long itemId);
}
