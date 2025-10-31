package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.service.PriceQuotationService;
import fpt.edu.vn.gms.service.ServiceTicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Phiếu Dịch Vụ (Service Ticket).
 * <p>
 * Cung cấp các API cho phép:
 * - Tạo phiếu mới cho khách hàng mới (bao gồm tạo Customer và Vehicle)
 * - Lấy danh sách phiếu dịch vụ có phân trang
 * - Lấy chi tiết phiếu dịch vụ theo ID
 * - Cập nhật thông tin phiếu dịch vụ
 */
@RestController
@RequestMapping("/api/service-tickets")
@RequiredArgsConstructor
@Tag(name = "Service Ticket Management", description = "Quản lý Phiếu Dịch Vụ của Garage (Service Ticket)")
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
    public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> update(
            @PathVariable("serviceTicketId") Long serviceTicketId,
            @RequestBody ServiceTicketRequestDto dto) {

        ServiceTicketResponseDto updated = serviceTicketService.updateServiceTicket(serviceTicketId, dto);

        return ResponseEntity.status(200)
                .body(ApiResponse.created("Update service ticket successfully!", updated));
    }

}
