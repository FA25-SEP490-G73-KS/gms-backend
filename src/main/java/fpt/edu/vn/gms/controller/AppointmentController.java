package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.AllowRoles;
import fpt.edu.vn.gms.common.enums.AppointmentStatus;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.response.AppointmentBySlotResponse;
import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.TimeSlotDto;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static fpt.edu.vn.gms.utils.AppRoutes.APPOINTMENTS_PREFIX;

@Tag(name = "appointments", description = "Quản lý các cuộc hẹn và lịch hẹn")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = APPOINTMENTS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AppointmentController {

        private final AppointmentService service;
        private final ServiceTicketRepository serviceTicketRepo;

        @GetMapping
        @Operation(summary = "Lấy tất cả các cuộc hẹn", description = "Lấy danh sách tất cả các cuộc hẹn với phân trang.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách cuộc hẹn thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Page<AppointmentResponseDto>>> getAllAppointments(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size) {
                Page<AppointmentResponseDto> appointments = service.getAllAppointments(page, size);
                return ResponseEntity.ok(ApiResponse.success("Appointments fetched successfully", appointments));
        }

        @GetMapping("/status")
        @Operation(summary = "Lấy cuộc hẹn theo trạng thái", description = "Lấy danh sách các cuộc hẹn theo trạng thái cụ thể với phân trang.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách cuộc hẹn thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Page<AppointmentResponseDto>>> getAppointmentStatus(
                        @RequestParam("status") AppointmentStatus status,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size) {

                Pageable pageable = PageRequest.of(page, size, Sort.by("appointment_date").descending());

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Lấy lịch hẹn theo trạng thái!!",
                                                service.getAppointmentsByStatus(status, pageable)));
        }

        @GetMapping("/time-slots")
        @Operation(summary = "Lấy các khung giờ trống", description = "Lấy danh sách các khung giờ còn trống trong một ngày cụ thể.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách khung giờ thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ngày không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<List<TimeSlotDto>>> getSlots(
                        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
                List<TimeSlotDto> slots = service.getTimeSlotsByDate(date);
                return ResponseEntity.ok(ApiResponse.success("Retrieved available time slots", slots));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Lấy cuộc hẹn theo ID", description = "Lấy thông tin chi tiết của một cuộc hẹn bằng ID.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tìm thấy cuộc hẹn"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy cuộc hẹn", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<AppointmentResponseDto>> getAppointmentById(
                        @PathVariable Long id) {
                AppointmentResponseDto appointment = service.getAppointmentById(id);
                return ResponseEntity.ok(ApiResponse.success("Appointment found", appointment));
        }

        @GetMapping("/date")
        @Operation(summary = "Lấy cuộc hẹn theo ngày", description = "Lấy danh sách các cuộc hẹn trong một ngày cụ thể, được nhóm theo khung giờ.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách cuộc hẹn thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ngày không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<List<AppointmentBySlotResponse>>> getAppointmentsByDate(
                        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date

        ) {

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Khung giờ theo ngày", service.getAppointmentsByDate(date)));
        }

        @PostMapping
        @Operation(summary = "Tạo cuộc hẹn mới", description = "Tạo một cuộc hẹn mới dựa trên thông tin được cung cấp.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo cuộc hẹn thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<AppointmentResponseDto>> createAppointment(
                        @RequestBody AppointmentRequestDto dto) {
                AppointmentResponseDto response = service.createAppointment(dto);
                return ResponseEntity.status(201)
                                .body(ApiResponse.created("Appointment created successfully", response));
        }

        @PatchMapping("/{id}/status")
        @Operation(summary = "Cập nhật trạng thái đến của khách hàng", description = "Cập nhật trạng thái của cuộc hẹn thành 'ĐÃ ĐẾN' và tạo một phiếu dịch vụ mới.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy cuộc hẹn", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<AppointmentResponseDto>> updateArrivedStatus(
                        @PathVariable Long id) {

                AppointmentResponseDto updated = service.updateArrivedStatus(id);

                ServiceTicket serviceTicket = serviceTicketRepo.findByAppointment_AppointmentId(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Cập nhật & Tạo phiếu dịch vụ # "
                                                + serviceTicket.getServiceTicketCode()
                                                + " thành công", updated));
        }

//        @AllowRoles({ Role.SERVICE_ADVISOR })
        @GetMapping("/count")
        @Operation(summary = "Đếm tổng số cuộc hẹn", description = "Đếm tổng số cuộc hẹn hiện có trong hệ thống.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đếm cuộc hẹn thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Long>> countAppointments(
                @Parameter(
                        description = "Ngày cần đếm số lượng lịch hẹn (định dạng yyyy-MM-dd)",
                        example = "2025-11-17"
                )
                @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
        ) {
                Long count = service.countAppointmentsByDate(date);
                return ResponseEntity.ok(
                        ApiResponse.success("Đếm số hẹn thành công", count)
                );
        }

}
