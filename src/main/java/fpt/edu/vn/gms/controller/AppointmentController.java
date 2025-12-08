package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.Public;
import fpt.edu.vn.gms.common.enums.AppointmentStatus;
import fpt.edu.vn.gms.dto.ZnsAppointmentInfo;
import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.AppointmentService;
import fpt.edu.vn.gms.service.auth.JwtService;
import fpt.edu.vn.gms.service.zalo.OneTimeTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.tomcat.Jar;
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
import java.util.Objects;

import static fpt.edu.vn.gms.utils.AppRoutes.APPOINTMENTS_PREFIX;

@Tag(name = "appointments", description = "Quản lý các cuộc hẹn và lịch hẹn")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = APPOINTMENTS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentController {

        JwtService jwtService;
        AppointmentService service;
        OneTimeTokenService oneTimeTokenService;
        ServiceTicketRepository serviceTicketRepo;

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

        @Public
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

        @Public
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

        @Public
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

        @PatchMapping("/{id}/arrived")
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

        @PatchMapping("/{id}/status")
        @Operation(
                summary = "Cập nhật trạng thái cuộc hẹn",
                description = "Cập nhật trạng thái mới cho một cuộc hẹn dựa trên ID được cung cấp."
        )
        public ResponseEntity<ApiResponse<AppointmentResponseDto>> updateStatus(
                @Parameter(description = "Mã định danh (ID) của cuộc hẹn", example = "1")
                @PathVariable Long id,
                @Parameter(description = "Trạng thái mới của cuộc hẹn", example = "CONFIRMED")
                @RequestParam AppointmentStatus status
        ) {
                AppointmentResponseDto updated = service.updateStatus(id, status);
                return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái cuộc hẹn thành công", updated));
        }

//        @PostMapping("/{id}/confirm")
//        @Operation(
//                summary = "Xác nhận cuộc hẹn",
//                description = "Đánh dấu cuộc hẹn là đã được khách hàng xác nhận."
//        )
//        public ResponseEntity<ApiResponse<AppointmentResponseDto>> confirmAppointment(
//                @Parameter(description = "Mã định danh (ID) của cuộc hẹn", example = "1")
//                @PathVariable Long id
//        ) {
//                AppointmentResponseDto confirmed = service.confirmAppointment(id);
//                return ResponseEntity.ok(ApiResponse.success("Xác nhận cuộc hẹn thành công", confirmed));
//        }

        @PostMapping("/confirm/{one_time_token}")
        @Operation(summary = "Xác nhận cuộc hẹn", description = "Xác nhận cuộc hẹn thông qua liên kết ZNS.")
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xác nhận cuộc hẹn thành công"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Không thể xác nhận cuộc hẹn", content = @Content(schema = @Schema(hidden = true))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy cuộc hẹn hoặc mã một lần không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<String>> confirmAppointment(
                @PathVariable("one_time_token") String oneTimeToken) {

                try {
                        ZnsAppointmentInfo info = jwtService.parseZnsToken(oneTimeToken);

                        boolean result = service.confirmByCode(info.getAppointmentCode());

                        if (result) {
                                oneTimeTokenService.deleteToken(oneTimeToken);
                        }

                        return ResponseEntity.ok(ApiResponse.success("Khách đã xác nhận lịch hẹn!", "OK"));
                } catch (Exception e) {
                        return ResponseEntity.badRequest()
                                .body(ApiResponse.error(400, "Xác nhận cuộc hẹn thất bại: " + e.getMessage()));
                }
        }
}
