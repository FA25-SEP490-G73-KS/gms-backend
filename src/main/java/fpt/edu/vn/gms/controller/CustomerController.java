package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping
    @Operation(
            summary = "Get all customers (paginated)",
            description = """
                Retrieves a paginated list of all customers in the system.
                Each customer includes information such as name, phone, Zalo ID, address, customer type, and loyalty level.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of customers",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDto.class),
                            examples = @ExampleObject(
                                    name = "Customer List Example",
                                    value = """
                        {
                          "content": [
                            {
                              "customerId": 1,
                              "fullName": "Nguyen Van A",
                              "phone": "0987654321",
                              "zaloId": "zalo_12345",
                              "address": "123 Nguyen Trai, Ha Noi",
                              "customerType": "INDIVIDUAL",
                              "loyaltyLevel": "NORMAL"
                            },
                            {
                              "customerId": 2,
                              "fullName": "Tran Thi B",
                              "phone": "0912345678",
                              "zaloId": "zalo_67890",
                              "address": "456 Le Loi, Da Nang",
                              "customerType": "COMPANY",
                              "loyaltyLevel": "VIP"
                            }
                          ],
                          "pageable": { "pageNumber": 0, "pageSize": 6 },
                          "totalElements": 2
                        }
                        """
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Page index must not be negative\"}")
                    )),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<CustomerDto>> getAllCustomer(
            @Parameter(description = "Page number for pagination (default = 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page (default = 6)", example = "6")
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(customerService.getAllCustumer(page, size));
    }

    // ================== GET CUSTOMER BY ID ==================
    @GetMapping("/{customerId}")
    @Operation(
            summary = "Get customer by ID",
            description = """
                Retrieves details of a specific customer by their unique ID.
                Returns full customer profile including type and loyalty level.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDto.class),
                            examples = @ExampleObject(
                                    name = "Customer Example",
                                    value = """
                        {
                          "customerId": 1,
                          "fullName": "Nguyen Van A",
                          "phone": "0987654321",
                          "zaloId": "zalo_12345",
                          "address": "123 Nguyen Trai, Ha Noi",
                          "customerType": "INDIVIDUAL",
                          "loyaltyLevel": "PLATINUM"
                        }
                        """
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Customer with ID 99 not found\"}")
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid customer ID format",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Customer ID must be a number\"}")
                    ))
    })
    public ResponseEntity<CustomerDto> getCustumerByCustomerId(
            @Parameter(description = "Unique identifier of the customer", example = "1")
            @PathVariable Long customerId
    ) {
        return ResponseEntity.ok(customerService.getCustumerByCustomerId(customerId));
    }
}
