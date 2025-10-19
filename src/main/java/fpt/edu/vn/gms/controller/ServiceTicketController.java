package fpt.edu.vn.gms.controller;
import fpt.edu.vn.gms.dto.ServiceTicketDto;
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
    private final ServiceTicketService service;


    /**
     *  tạo phiếu cho khách mới (chưa có trong hệ thống), đồng thời tạo Customer và Vehicle.
     * appointmentId = null, createdAt = now, deliveryAt = null, notes = null, status = CHO_BAO_GIA.
     */
    @PostMapping("/new-service-tickets")
    public ResponseEntity<ServiceTicketDto> createForNewCustomer(@RequestBody ServiceTicketDto req) {
        ServiceTicketDto created = service.createNewServiceTicket(req);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Lấy chi tiết phiếu dịch vụ theo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceTicketDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Lấy danh sách phiếu dịch vụ có phân trang.
     */
    @GetMapping
    public ResponseEntity<Page<ServiceTicketDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    /**
     * Cập nhật phiếu dịch vụ theo ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceTicketDto> update(@PathVariable("id") Long id, @RequestBody ServiceTicketDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }



}
