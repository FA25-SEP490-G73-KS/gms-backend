package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.service.ServiceTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/service-tickets")
@RequiredArgsConstructor
public class ServiceTicketController {

    private final ServiceTicketService serviceTicketService;

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> createServiceTicket(
            @RequestBody ServiceTicketRequestDto req) {

        ServiceTicketResponseDto created = serviceTicketService.createServiceTicket(req);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("Service Ticket Created", created));
    }

    @GetMapping("/{serviceTicketId}")
    public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> getById(
            @PathVariable("serviceTicketId") Long serviceTicketId) {

        ServiceTicketResponseDto dto = serviceTicketService.getServiceTicketById(serviceTicketId);

        return ResponseEntity.status(200)
                .body(ApiResponse.created("Get service ticket successfully!", dto));
    }

    @GetMapping("createAt")
    public ResponseEntity<ApiResponse<Page<ServiceTicketResponseDto>>> getServiceTicketsByCreatedAt(
            @RequestParam("createdAt") LocalDateTime createdAt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceTicketResponseDto> responseDtos = serviceTicketService.getServiceTicketsByCreatedAt(createdAt, pageable);

        return ResponseEntity.status(200)
                .body(ApiResponse.created("Lấy phiếu dịch vụ ngày " + createdAt, responseDtos));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ServiceTicketResponseDto>>> getAllServiceTicket(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Page<ServiceTicketResponseDto> dtos = serviceTicketService.getAllServiceTicket(page, size);

        return ResponseEntity.status(200)
                .body(ApiResponse.created("Get all service tickets", dtos));
    }

    @PutMapping("/{serviceTicketId}")
    public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> updateServiceTicket(
            @PathVariable("serviceTicketId") Long serviceTicketId,
            @RequestBody ServiceTicketRequestDto dto) {

        ServiceTicketResponseDto updated = serviceTicketService.updateServiceTicket(serviceTicketId, dto);

        return ResponseEntity.status(200)
                .body(ApiResponse.created("Update service ticket successfully!", updated));
    }

    @PatchMapping("/{id}/delivery-at")
    public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> updateDeliveryAt(
            @PathVariable Long id,
            @RequestBody LocalDate deliveryAt

    ) {

        ServiceTicketResponseDto updated = serviceTicketService.updateDeliveryAt(id, deliveryAt);
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Cập nhật ngày dự kiến giao xe!!", updated));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<ServiceTicketResponseDto>>> getServiceTicketsByStatus(
            @PathVariable("status") ServiceTicketStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size)
    {

        Page<ServiceTicketResponseDto> tickets = serviceTicketService.getServiceTicketsByStatus(status, page, size);
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Service Ticket với " + status, tickets));
    }
}
