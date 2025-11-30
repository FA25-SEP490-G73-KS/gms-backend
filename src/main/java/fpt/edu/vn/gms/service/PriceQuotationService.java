package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.ChangeQuotationStatusReqDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PriceQuotationService {
    ServiceTicketResponseDto createQuotation(Long ticketId);

    Page<PriceQuotationResponseDto> findAllQuotations(Pageable pageable);

    ServiceTicketResponseDto updateQuotationItems(Long quotationId, PriceQuotationRequestDto dto);

    PriceQuotationResponseDto recalculateEstimateAmount(Long quotationId);

    PriceQuotationResponseDto getById(Long id);

    PriceQuotationResponseDto updateQuotationStatusManual(Long id, ChangeQuotationStatusReqDto reqDto);

    PriceQuotationResponseDto confirmQuotationByCustomer(Long quotationId);

    PriceQuotationResponseDto rejectQuotationByCustomer(Long quotationId, String reason);

    PriceQuotationResponseDto sendQuotationToCustomer(Long quotationId);

    long countWaitingCustomerConfirm();

    long countVehicleInRepairingStatus();

    PriceQuotationResponseDto updateLaborCost(Long id);

    byte[] exportPdfQuotation(Long quotationId);
}
