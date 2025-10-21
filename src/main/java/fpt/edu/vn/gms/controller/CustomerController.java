package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho các thao tác liên quan đến khách hàng.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    /**
     * Lấy danh sách khách hàng với phân trang
     *
     * @param page số trang
     * @param size kích thước trang
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<CustomerDto>> getAllCustomer( @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(customerService.getAllCustumer(page, size));
    }

    /**
     * Lấy khách hàng theo ID
     *
     * @param customerId
     * @return
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> getCustumerByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustumerByCustomerId(customerId));
    }
}
