package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.ServiceTicketDto;
import fpt.edu.vn.gms.service.ServiceTicketService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho Phiếu Dịch Vụ (Service Ticket).
 * <p>
 * Cung cấp các API cho phép:
 * - Tạo phiếu mới cho khách hàng mới (bao gồm tạo Customer và Vehicle)
 * - Lấy danh sách phiếu dịch vụ có phân trang
 * - Lấy chi tiết phiếu dịch vụ theo ID
 * - Cập nhật thông tin phiếu dịch vụ
 */
@RestController
@RequestMapping("/api/service-tickets")
@RequiredArgsConstructor
@Tag(name = "Service Ticket Management", description = "Quản lý Phiếu Dịch Vụ của Garage (Service Ticket)")
public class ServiceTicketController {

    private final ServiceTicketService serviceTicketService;

    // ============================================================
    // 1️⃣ TẠO PHIẾU DỊCH VỤ MỚI
    // ============================================================

    @Operation(
            summary = "Tạo phiếu dịch vụ mới cho khách hàng mới",
            description = """
                    Tạo mới một phiếu dịch vụ cho khách hàng chưa tồn tại trong hệ thống.
                    API này đồng thời sẽ tạo bản ghi **Customer** và **Vehicle** mới.

                    - `appointmentId` = null  
                    - `createdAt` = thời gian hiện tại  
                    - `deliveryAt` = null  
                    - `status` mặc định là `CHO_BAO_GIA`
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Thông tin phiếu dịch vụ cần tạo",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServiceTicketDto.class),
                            examples = @ExampleObject(
                                    name = "ServiceTicketExample",
                                    value = """
                                            {
                                              "fullName": "Nguyen Van A",
                                              "phone": "0905123456",
                                              "zaloId": "zalo_123456",
                                              "address": "123 Tran Phu, Ha Noi",
                                              "licensePlate": "30A-12345",
                                              "brand": "Toyota",
                                              "model": "Vios",
                                              "year": 2022,
                                              "vin": "VN123456789",
                                              "notes": "Xe bị hỏng phanh"
                                            }
                                            """
                            )
                    )
            ),
            parameters = {
                    @Parameter(name = "X-Employee-Id", description = "ID của cố vấn dịch vụ (Service Advisor)", example = "5")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo phiếu dịch vụ thành công",
                    content = @Content(schema = @Schema(implementation = ServiceTicketDto.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = @Content),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống nội bộ", content = @Content)
    })
    @PostMapping("/new-service-tickets")
    public ResponseEntity<ServiceTicketDto> createServiceTicket(
            @RequestBody ServiceTicketDto req,
            @RequestHeader(value = "X-Employee-Id", required = false)
            Long employeeIdOfServiceAdvisor) {

        ServiceTicketDto created = serviceTicketService.createServiceTicket(req, employeeIdOfServiceAdvisor);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ============================================================
    // 2️⃣ LẤY CHI TIẾT PHIẾU DỊCH VỤ
    // ============================================================

    @Operation(
            summary = "Lấy chi tiết phiếu dịch vụ theo ID",
            description = "Trả về toàn bộ thông tin chi tiết phiếu dịch vụ theo `serviceTicketId`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy chi tiết phiếu dịch vụ thành công",
                    content = @Content(schema = @Schema(implementation = ServiceTicketDto.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ", content = @Content)
    })
    @GetMapping("/{serviceTicketId}")
    public ResponseEntity<ServiceTicketDto> getById(
            @Parameter(description = "ID của phiếu dịch vụ cần lấy", example = "10")
            @PathVariable("serviceTicketId") Long serviceTicketId) {

        return ResponseEntity.ok(serviceTicketService.getServiceTicketByServiceTicketId(serviceTicketId));
    }

    // ============================================================
    // 3️⃣ DANH SÁCH PHIẾU DỊCH VỤ (CÓ PHÂN TRANG)
    // ============================================================

    @Operation(
            summary = "Lấy danh sách phiếu dịch vụ (có phân trang)",
            description = "Trả về danh sách các phiếu dịch vụ trong hệ thống, có hỗ trợ phân trang."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Truy vấn danh sách phiếu dịch vụ thành công",
                    content = @Content(schema = @Schema(implementation = ServiceTicketDto.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<ServiceTicketDto>> getAllServiceTicket(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng bản ghi mỗi trang", example = "6")
            @RequestParam(defaultValue = "6") int size) {

        return ResponseEntity.ok(serviceTicketService.getAllServiceTicket(page, size));
    }

    // ============================================================
    // 4️⃣ CẬP NHẬT PHIẾU DỊCH VỤ
    // ============================================================

    @Operation(
            summary = "Cập nhật thông tin phiếu dịch vụ",
            description = "Cập nhật thông tin phiếu dịch vụ theo `serviceTicketId`.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Thông tin cần cập nhật cho phiếu dịch vụ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServiceTicketDto.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật phiếu dịch vụ thành công",
                    content = @Content(schema = @Schema(implementation = ServiceTicketDto.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = @Content)
    })
    @PutMapping("/update-service-ticket/{serviceTicketId}")
    public ResponseEntity<ServiceTicketDto> update(
            @Parameter(description = "ID của phiếu dịch vụ cần cập nhật", example = "10")
            @PathVariable("serviceTicketId") Long serviceTicketId,
            @RequestBody ServiceTicketDto dto) {

        return ResponseEntity.ok(serviceTicketService.updateServiceTicket(serviceTicketId, dto));
    }
}
