package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;

public interface PriceQuotationService {

    PriceQuotationResponseDto createQuotationFromServiceTicket(Long id, PriceQuotationRequestDto request);
}
