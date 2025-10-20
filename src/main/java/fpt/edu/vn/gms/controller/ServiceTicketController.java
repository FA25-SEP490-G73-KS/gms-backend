package fpt.edu.vn.gms.controller;
import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.ServiceTicketDto;
import fpt.edu.vn.gms.service.CustomerService;
import fpt.edu.vn.gms.service.ServiceTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho Phiếu Dịch Vụ (ServiceTicket).
 */
@RestController
@RequestMapping("/api/service-tickets")
@RequiredArgsConstructor
public class ServiceTicketController {
    private final ServiceTicketService serviceTicketService;


    /**
     *  tạo phiếu cho khách mới (chưa có trong hệ thống), đồng thời tạo Customer và Vehicle.
     * appointmentId = null, createdAt = now, deliveryAt = null, notes = null, status = CHO_BAO_GIA.
     */
    @PostMapping("/new-service-tickets")
    public ResponseEntity<ServiceTicketDto> createServiceTicket(
            @RequestBody ServiceTicketDto req,
            @RequestHeader(value = "X-Employee-Id", required = false) Long employeeIdOfServiceAdvisor
    ) {
        ServiceTicketDto created = serviceTicketService.createServiceTicket(req, employeeIdOfServiceAdvisor);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Lấy chi tiết phiếu dịch vụ theo ID.
     */
    @GetMapping("/{serviceTicketId}")
    public ResponseEntity<ServiceTicketDto> getById(@PathVariable("id") Long employeeIdOfServiceAvidor) {
        return ResponseEntity.ok(serviceTicketService.getServiceTicketByServiceTicketId(employeeIdOfServiceAvidor));
    }

    /**
     * Lấy danh sách phiếu dịch vụ có phân trang.
     */
    @GetMapping
    public ResponseEntity<Page<ServiceTicketDto>> getAllServiceTicket(Pageable pageable) {
        return ResponseEntity.ok(serviceTicketService.getAllServiceTicket(pageable));
    }

    /**
     * Cập nhật phiếu dịch vụ theo ID.
     */
    @PutMapping("/update-service-ticket/{serviceTicketId}")
    public ResponseEntity<ServiceTicketDto> update(@PathVariable("serviceTicketId") Long serviceTicketId, @RequestBody ServiceTicketDto dto) {
        return ResponseEntity.ok(serviceTicketService.updateServiceTicket(serviceTicketId, dto));
    }
}
