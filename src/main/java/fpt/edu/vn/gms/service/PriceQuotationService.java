package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.ChangeQuotationStatusReqDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;

public interface PriceQuotationService {

    PriceQuotationResponseDto updateQuotationItems(Long quotationId, PriceQuotationRequestDto dto);

    PriceQuotationResponseDto getById(Long id);

    PriceQuotationResponseDto updateQuotationStatusManual(Long id, ChangeQuotationStatusReqDto reqDto);

    PriceQuotationResponseDto confirmQuotationByCustomer(Long quotationId);

    PriceQuotationResponseDto rejectQuotationByCustomer(Long quotationId, String reason);
}
