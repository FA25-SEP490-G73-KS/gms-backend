package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.TimeSlotDto;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;
    private final ServiceTicketRepository serviceTicketRepo;

    @GetMapping("/time-slots")
    public ResponseEntity<ApiResponse<List<TimeSlotDto>>> getSlots(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<TimeSlotDto> slots = service.getTimeSlotsByDate(date);
        return ResponseEntity.ok(ApiResponse.success("Retrieved available time slots", slots));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> createAppointment(
            @RequestBody AppointmentRequestDto dto
    ) {
        AppointmentResponseDto response = service.createAppointment(dto);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("Appointment created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDto>>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Page<AppointmentResponseDto> appointments = service.getAllAppointments(page, size);
        return ResponseEntity.ok(ApiResponse.success("Appointments fetched successfully", appointments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> getAppointmentById(
            @PathVariable Long id
    ) {
        AppointmentResponseDto appointment = service.getAppointmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Appointment found", appointment));
    }

    @GetMapping("/date")
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDto>>> getAppByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        Page<AppointmentResponseDto> appointments = service.getAppByDate(date, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy lịch hẹn theo ngày", appointments));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> updateStatusArrived(
            @PathVariable Long id
    ) {

        AppointmentResponseDto updated = service.updateStatus(id);

        ServiceTicket serviceTicket = serviceTicketRepo.findByAppointment_AppointmentId(id);

        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật & Tạo phiếu dịch vụ # "
                + serviceTicket.getServiceTicketCode()
                        + " thành công", updated));
    }

}
