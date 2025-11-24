package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.CustomerDetailResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerServiceHistoryResponseDto;
import fpt.edu.vn.gms.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fpt.edu.vn.gms.utils.AppRoutes.CUSTOMERS_PREFIX;

/**
 * REST Controller cho các thao tác liên quan đến khách hàng.
 * <p>
 * Quản lý thông tin khách hàng bao gồm: xem danh sách khách hàng, xem chi tiết
 * khách hàng theo ID.
 * Mỗi khách hàng bao gồm thông tin cá nhân, loại khách hàng và cấp độ thân
 * thiết.
 */

@Tag(name = "customers", description = "Quản lý thông tin khách hàng")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = CUSTOMERS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CustomerController {

        private final CustomerService customerService;

        @GetMapping
        @Operation(summary = "Lấy tất cả khách hàng", description = "Lấy danh sách tất cả khách hàng với phân trang.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách khách hàng thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Page<CustomerResponseDto>>> getALlCustomer(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size) {

                Page<CustomerResponseDto> customerList = customerService.getAllCustomers(page, size);
                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Danh sách khách hàng!!", customerList));
        }

        @GetMapping("/search")
        @Operation(summary = "Tìm kiếm khách hàng", description = "Tìm kiếm khách hàng theo số điện thoại.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tìm kiếm khách hàng thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<List<CustomerDto>> searchCustomers(@RequestParam("q") String query) {
                List<CustomerDto> customers = customerService.searchCustomersByPhone(query);
                return ResponseEntity.status(200).body(customers);
        }

        @GetMapping("/phone")
        @Operation(summary = "Lấy khách hàng theo số điện thoại", description = "Lấy thông tin chi tiết của khách hàng bằng số điện thoại.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin khách hàng thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<CustomerDetailResponseDto>> getCustomerByPhone(
                        @RequestParam("phone") String phone) {

                CustomerDetailResponseDto customers = customerService.getByPhone(phone);

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Thành công", customers));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Tìm chi tiết khách hàng theo ID", description = "Lấy thông tin chi tiết của một khách hàng bằng ID.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy chi tiết khách hàng thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<CustomerDetailResponseDto>> findCustomerDetailById(@PathVariable long id) {

                CustomerDetailResponseDto customerDetail = customerService.getCustomerDetailById(id);
                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Get customer detail successfully", customerDetail));
        }

        @PostMapping
        @Operation(summary = "Tạo khách hàng mới", description = "Tạo một khách hàng mới dựa trên thông tin được cung cấp.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tạo khách hàng thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<CustomerResponseDto>> createCustomer(@RequestBody CustomerRequestDto dto) {

                CustomerResponseDto response = customerService.createCustomer(dto);
                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Tạo thông tin khách hàng thành công!!", response));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Cập nhật thông tin khách hàng", description = "Cập nhật thông tin của một khách hàng đã có.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thông tin khách hàng thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<CustomerResponseDto>> updateCustomer(
                        @PathVariable Long id,
                        @RequestBody CustomerRequestDto dto) {

                CustomerResponseDto updated = customerService.updateCustomer(id, dto);
                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Cập nhật thông tin khách hàng thành công!!", updated));
        }

        @GetMapping("/service-history")
        @Operation(summary = "Lấy lịch sử sử dụng dịch vụ của khách hàng theo số điện thoại", description = "Trả về họ tên, số điện thoại, danh sách xe đã từng sửa chữa (biển số, model, hãng, ngày sửa gần nhất) dựa trên service ticket hoàn thành.")
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin thành công",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerServiceHistoryResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng hoặc xe", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<CustomerServiceHistoryResponseDto> getCustomerServiceHistoryByPhone(@RequestParam("phone") String phone) {
            return ResponseEntity.ok(customerService.getCustomerServiceHistoryByPhone(phone));
        }
}
