package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.Public;
import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.request.NotMeRequest;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fpt.edu.vn.gms.utils.AppRoutes.CUSTOMERS_PREFIX;


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

        @Public
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
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDetailDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng hoặc xe", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<CustomerDetailDto> getCustomerServiceHistoryByPhone(@RequestParam("phone") String phone) {
            return ResponseEntity.ok(customerService.getCustomerServiceHistoryByPhone(phone));
        }


        @Operation(
                summary = "Lấy danh sách khách hàng (manager)",
                description = "API trả về danh sách khách hàng với tìm kiếm, lọc trạng thái, phân trang"
        )
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Danh sách khách hàng",
                content = @Content(schema = @Schema(implementation = CustomerDetailDto.class))
        )
        @GetMapping("/manager")
        public ResponseEntity<ApiResponse<Page<CustomerDetailDto>>> getCustomers(
                @RequestParam(defaultValue = "0") @Min(0) int page,
                @RequestParam(defaultValue = "6") @Min(1) int size
        ) {
                return ResponseEntity.ok(
                        ApiResponse.success("Danh sách khách hàng!", customerService.getCustomers(page, size))
                );
        }

        @Operation(
                summary = "Lấy thông tin chi tiết khách hàng (manager)",
                description = "Bao gồm thông tin cá nhân, tổng chi tiêu, số xe, số lần sửa xe và danh sách xe"
        )
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Thông tin chi tiết khách hàng",
                content = @Content(schema = @Schema(implementation = CustomerDetailDto.class))
        )
        @GetMapping("/manager/{customerId}/vehicles")
        public ResponseEntity<ApiResponse<CustomerDetailDto>> getCustomerDetail(
                @PathVariable Long customerId
        ) {
                return ResponseEntity.ok(ApiResponse.success("Chi tiết khách hàng!", customerService.getCustomerDetail(customerId)));
        }

        @Operation(
                summary = "Lịch sử dịch vụ của khách hàng (manager)",
                description = "Trả về danh sách Service Ticket theo customerId"
        )
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Danh sách lịch sử dịch vụ",
                content = @Content(schema = @Schema(implementation = CustomerServiceHistoryDto.class))
        )
        @GetMapping("/manager/{customerId}/service-history")
        public ResponseEntity<ApiResponse<CustomerDetailDto>> getServiceHistory(
                @PathVariable Long customerId
        ) {
                return ResponseEntity.ok(ApiResponse.success("Chi tiết khách hàng!", customerService.getServiceHistory(customerId)));
        }

        @Public
        @GetMapping("/check")
        @Operation(
                summary = "Kiểm tra khách hàng theo số điện thoại",
                description = """
            Kiểm tra xem khách hàng đã tồn tại trong hệ thống hay chưa.
            
            • Nếu tồn tại → hiện popup trả Về thông tin chi tiết khách hàng  
            • Nếu chưa tồn tại → trả về DTO với các trường = null (FE tự hiểu là 'chưa có khách')
            
            API này không yêu cầu đăng nhập.
            """
        )
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Kiểm tra thành công",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CustomerDetailResponseDto.class)
                        )
                ),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Số điện thoại không hợp lệ",
                        content = @Content(schema = @Schema(hidden = true))
                ),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "Lỗi máy chủ nội bộ",
                        content = @Content(schema = @Schema(hidden = true))
                )
        })
        public ResponseEntity<ApiResponse<CustomerDetailResponseDto>> checkCustomerExists(
                @RequestParam String phone
        ) {
                CustomerDetailResponseDto dto = customerService.getByPhone(phone);

                return ResponseEntity.ok(ApiResponse.success("Kiểm tra khách đã tồn tại?", dto));
        }

        @Operation(
                summary = "Xử lý trường hợp khách hàng chọn 'Không phải tôi'",
                description = """
            Khi khách hàng xác thực OTP nhưng chọn 'Không phải tôi', hệ thống sẽ:
            
            • Vô hiệu hóa khách hàng cũ (isActive = false)  
            • Đổi số điện thoại của bản ghi cũ để tránh trùng lặp  
            • Tạo một khách hàng mới với đúng số điện thoại đã nhập  
            
            API này giúp tách biệt dữ liệu của khách cũ – khách mới mà không gây xung đột.
            
            Không yêu cầu đăng nhập.
            """
        )
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Tạo khách hàng mới thành công",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CustomerResponseDto.class)
                        )
                ),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Số điện thoại không hợp lệ",
                        content = @Content(schema = @Schema(hidden = true))
                ),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "Lỗi máy chủ nội bộ",
                        content = @Content(schema = @Schema(hidden = true))
                )
        })
        @PostMapping("/not-me")
        @Public
        public ResponseEntity<ApiResponse<CustomerResponseDto>> handleNotMe(@RequestBody NotMeRequest req) {
                CustomerResponseDto result = customerService.handleNotMe(req.getPhone());
                return ResponseEntity.ok(ApiResponse.success("Tạo khách mới!", result));
        }

}
