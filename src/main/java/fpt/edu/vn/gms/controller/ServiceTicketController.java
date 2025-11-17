package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.service.ServiceTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static fpt.edu.vn.gms.utils.AppRoutes.SERVICE_TICKETS_PREFIX;

@Tag(name = "service-tickets", description = "Quản lý phiếu dịch vụ và bảo trì xe")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = SERVICE_TICKETS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ServiceTicketController {

        private final ServiceTicketService serviceTicketService;

        @PostMapping
        @Operation(summary = "Tạo phiếu dịch vụ", description = "Tạo một phiếu dịch vụ mới.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo phiếu dịch vụ thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> createServiceTicket(
                        @RequestBody ServiceTicketRequestDto req) {

                ServiceTicketResponseDto created = serviceTicketService.createServiceTicket(req);
                return ResponseEntity.status(201)
                                .body(ApiResponse.created("Service Ticket Created", created));
        }

        @GetMapping("/{serviceTicketId}")
        @Operation(summary = "Lấy phiếu dịch vụ theo ID", description = "Lấy thông tin chi tiết của một phiếu dịch vụ bằng ID.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy phiếu dịch vụ thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> getById(
                        @PathVariable("serviceTicketId") Long serviceTicketId) {

                ServiceTicketResponseDto dto = serviceTicketService.getServiceTicketById(serviceTicketId);

                return ResponseEntity.status(200)
                                .body(ApiResponse.created("Get service ticket successfully!", dto));
        }

        @GetMapping("createAt")
        @Operation(summary = "Lấy phiếu dịch vụ theo ngày tạo", description = "Lấy danh sách các phiếu dịch vụ được tạo vào một ngày cụ thể với phân trang.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách phiếu dịch vụ thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ngày tạo không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Page<ServiceTicketResponseDto>>> getServiceTicketsByCreatedAt(
                        @RequestParam("createdAt") LocalDateTime createdAt,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size) {

                Pageable pageable = PageRequest.of(page, size);
                Page<ServiceTicketResponseDto> responseDtos = serviceTicketService
                                .getServiceTicketsByCreatedAt(createdAt, pageable);

                return ResponseEntity.status(200)
                                .body(ApiResponse.created("Lấy phiếu dịch vụ ngày " + createdAt, responseDtos));
        }

        @GetMapping
        @Operation(summary = "Lấy tất cả phiếu dịch vụ", description = "Lấy danh sách tất cả các phiếu dịch vụ với phân trang.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách phiếu dịch vụ thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Page<ServiceTicketResponseDto>>> getAllServiceTicket(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size) {

                Page<ServiceTicketResponseDto> dtos = serviceTicketService.getAllServiceTicket(page, size);

                return ResponseEntity.status(200)
                                .body(ApiResponse.created("Get all service tickets", dtos));
        }

        @PutMapping("/{serviceTicketId}")
        @Operation(summary = "Cập nhật phiếu dịch vụ", description = "Cập nhật thông tin của một phiếu dịch vụ đã có.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật phiếu dịch vụ thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> updateServiceTicket(
                        @PathVariable("serviceTicketId") Long serviceTicketId,
                        @RequestBody ServiceTicketRequestDto dto) {

                ServiceTicketResponseDto updated = serviceTicketService.updateServiceTicket(serviceTicketId, dto);

                return ResponseEntity.status(200)
                                .body(ApiResponse.created("Update service ticket successfully!", updated));
        }

        @PatchMapping("/{id}/delivery-at")
        @Operation(summary = "Cập nhật ngày dự kiến giao xe", description = "Cập nhật ngày dự kiến giao xe cho một phiếu dịch vụ.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật ngày giao xe thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ngày không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> updateDeliveryAt(
                        @PathVariable Long id,
                        @RequestBody LocalDate deliveryAt

        ) {

                ServiceTicketResponseDto updated = serviceTicketService.updateDeliveryAt(id, deliveryAt);
                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Cập nhật ngày dự kiến giao xe!!", updated));
        }

        @GetMapping("/status/{status}")
        @Operation(summary = "Lấy phiếu dịch vụ theo trạng thái", description = "Lấy danh sách các phiếu dịch vụ theo một trạng thái cụ thể với phân trang.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách phiếu dịch vụ thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Page<ServiceTicketResponseDto>>> getServiceTicketsByStatus(
                        @PathVariable("status") ServiceTicketStatus status,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size) {

                Page<ServiceTicketResponseDto> tickets = serviceTicketService.getServiceTicketsByStatus(status, page,
                                size);
                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Service Ticket với " + status, tickets));
        }
}
