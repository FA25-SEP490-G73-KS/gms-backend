package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ServiceTicketService {

    ServiceTicketResponseDto createServiceTicket(ServiceTicketRequestDto req, Employee currEmployee);

    ServiceTicketResponseDto getServiceTicketById(Long serviceTicketId);

    Page<ServiceTicketResponseDto> getAllServiceTicket(int page, int size);

    Page<ServiceTicketResponseDto> getServiceTicketsByCreatedAt(LocalDateTime createdAt, Pageable pageable);

    ServiceTicketResponseDto updateServiceTicket(Long serviceTicketId, ServiceTicketRequestDto dto);

    ServiceTicketResponseDto updateDeliveryAt(Long serviceTicketId, LocalDate deliveryAt);

    Page<ServiceTicketResponseDto> getServiceTicketsByStatus(ServiceTicketStatus status, int page, int size);

    long countServiceTicketByDate(LocalDate date);

    List<Map<String, Object>> getCompletedTicketsByMonth();

    List<Map<String, Object>> getTicketCountsByType(int year, int month);
}
