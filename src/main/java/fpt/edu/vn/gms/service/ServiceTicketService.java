package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ServiceTicketService {

    ServiceTicketResponseDto createServiceTicket(ServiceTicketRequestDto req);

    ServiceTicketResponseDto getServiceTicketById(Long serviceTicketId);

    Page<ServiceTicketResponseDto> getAllServiceTicket(int page, int size);

    Page<ServiceTicketResponseDto> getServiceTicketsByCreatedAt(LocalDateTime createdAt, Pageable pageable);

    ServiceTicketResponseDto updateServiceTicket(Long serviceTicketId, ServiceTicketRequestDto dto);

    ServiceTicketResponseDto updateDeliveryAt(Long serviceTicketId, LocalDate deliveryAt);

    Page<ServiceTicketResponseDto> getServiceTicketsByStatus(ServiceTicketStatus status, int page, int size);
}
