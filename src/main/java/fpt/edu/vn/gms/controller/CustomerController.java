package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.CustomerDetailResponseDto;
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

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CustomerResponseDto>>> getALlCustomer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        Page<CustomerResponseDto> customerList = customerService.getAllCustomers(page, size);
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Danh sách khách hàng!!", customerList));
    }


    @GetMapping("/search")
    public ResponseEntity<List<CustomerDto>> searchCustomers(@RequestParam("q") String query) {
        List<CustomerDto> customers = customerService.searchCustomersByPhone(query);
        return ResponseEntity.status(200).body(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDetailResponseDto>> findCustomerDetailById(@PathVariable long id) {

            CustomerDetailResponseDto customerDetail = customerService.getCustomerDetailById(id);
            return ResponseEntity.status(200)
                    .body(ApiResponse.success("Get customer detail successfully", customerDetail));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDto>> createCustomer(@RequestBody CustomerRequestDto dto) {

        CustomerResponseDto response = customerService.createCustomer(dto);
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Tạo thông tin khách hàng thành công!!", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDto dto
    ) {

        CustomerResponseDto updated = customerService.updateCustomer(id, dto);
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Cập nhật thông tin khách hàng thành công!!", updated));
    }
}
