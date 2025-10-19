package fpt.edu.vn.gms.controller;
import fpt.edu.vn.gms.dto.ServiceTicketDto;
import fpt.edu.vn.gms.service.ServiceTicketService;
import lombok.RequiredArgsConstructor;
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

}
