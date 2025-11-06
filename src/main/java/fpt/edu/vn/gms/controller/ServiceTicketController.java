package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.service.ServiceTicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/{serviceTicketId}/send-to-customer")
    public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> sendServiceTicketToCustomer(
            @PathVariable Long serviceTicketId
    ) {
        ServiceTicketResponseDto response = serviceTicketService.sendQuotationToCustomer(serviceTicketId);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Successfully", response));
    }

}
