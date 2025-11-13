package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ServiceTicketService {

    ServiceTicketResponseDto createServiceTicket(ServiceTicketRequestDto req);

    ServiceTicketResponseDto getServiceTicketById(Long serviceTicketId);

    Page<ServiceTicketResponseDto> getAllServiceTicket(int page, int size);

    ServiceTicketResponseDto updateServiceTicket(Long serviceTicketId, ServiceTicketRequestDto dto);

    Page<ServiceTicketResponseDto> getServiceTicketsByStatus(ServiceTicketStatus status, int page, int size);

    ServiceTicketResponseDto sendQuotationToCustomer(Long serviceTicketId);
}
