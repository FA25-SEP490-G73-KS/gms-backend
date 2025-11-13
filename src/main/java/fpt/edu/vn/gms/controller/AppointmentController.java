package fpt.edu.vn.gms.controller;

import com.google.protobuf.Api;
import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.response.AppointmentBySlotResponse;
import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.TimeSlotDto;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDto>>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Page<AppointmentResponseDto> appointments = service.getAllAppointments(page, size);
        return ResponseEntity.ok(ApiResponse.success("Appointments fetched successfully", appointments));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDto>>> getAppointmentStatus(
            @RequestParam("status")AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointment_date").descending());

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Lấy lịch hẹn theo trạng thái!!", service.getAppointmentsByStatus(status, pageable)));
    }

    @GetMapping("/time-slots")
    public ResponseEntity<ApiResponse<List<TimeSlotDto>>> getSlots(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<TimeSlotDto> slots = service.getTimeSlotsByDate(date);
        return ResponseEntity.ok(ApiResponse.success("Retrieved available time slots", slots));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> getAppointmentById(
            @PathVariable Long id
    ) {
        AppointmentResponseDto appointment = service.getAppointmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Appointment found", appointment));
    }

//    @GetMapping("/date")
//    public ResponseEntity<ApiResponse<Page<AppointmentResponseDto>>> getAppByDate(
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "6") int size
//    ) {
//
//        Page<AppointmentResponseDto> appointments = service.getAppByDate(date, page, size);
//        return ResponseEntity.ok(ApiResponse.success("Lấy lịch hẹn theo ngày", appointments));
//    }

    @GetMapping("/date")
    public ResponseEntity<ApiResponse<List<AppointmentBySlotResponse>>> getAppointmentsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date

    ) {

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Khung giờ theo ngày", service.getAppointmentsByDate(date)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> createAppointment(
            @RequestBody AppointmentRequestDto dto
    ) {
        AppointmentResponseDto response = service.createAppointment(dto);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("Appointment created successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> updateArrivedStatus(
            @PathVariable Long id
    ) {

        AppointmentResponseDto updated = service.updateArrivedStatus(id);

        ServiceTicket serviceTicket = serviceTicketRepo.findByAppointment_AppointmentId(id);

        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật & Tạo phiếu dịch vụ # "
                + serviceTicket.getServiceTicketCode()
                        + " thành công", updated));
    }


}
