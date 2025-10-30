package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.CustomerResponseDto;
import fpt.edu.vn.gms.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho các thao tác liên quan đến khách hàng.
 * <p>
 * Quản lý thông tin khách hàng bao gồm: xem danh sách khách hàng, xem chi tiết khách hàng theo ID.
 * Mỗi khách hàng bao gồm thông tin cá nhân, loại khách hàng và cấp độ thân thiết.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers in the Garage Management System. Includes viewing, filtering, and retrieving detailed customer info.")
public class CustomerController {

    private final CustomerService customerService;

    // ================== GET ALL CUSTOMERS ==================
//    @GetMapping
//    public ResponseEntity<Page<CustomerDto>> getAllCustomer(
//            @Parameter(description = "Page number for pagination (default = 0)", example = "0")
//            @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "Number of items per page (default = 6)", example = "6")
//            @RequestParam(defaultValue = "6") int size
//    ) {
//        return ResponseEntity.ok(customerService.getAllCustumer(page, size));
//    }
//
//    // ================== GET CUSTOMER BY ID ==================
//    @GetMapping("/{customerId}")
//    public ResponseEntity<CustomerDto> getCustumerByCustomerId(
//            @Parameter(description = "Unique identifier of the customer", example = "1")
//            @PathVariable Long customerId
//    ) {
//        return ResponseEntity.ok(customerService.getCustumerByCustomerId(customerId));
//    }


    @GetMapping("/search")
    public ResponseEntity<List<CustomerDto>> searchCustomers(@RequestParam("q") String query) {
        List<CustomerDto> customers = customerService.searchCustomersByPhone(query);
        return ResponseEntity.status(200).body(customers);
    }
}
