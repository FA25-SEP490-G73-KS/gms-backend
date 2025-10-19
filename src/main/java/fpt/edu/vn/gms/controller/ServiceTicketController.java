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
     * Tạo mới phiếu dịch vụ.
     */
    @PostMapping
    public ResponseEntity<ServiceTicketDto> create(@RequestBody ServiceTicketDto dto) {
        ServiceTicketDto created = service.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

}
