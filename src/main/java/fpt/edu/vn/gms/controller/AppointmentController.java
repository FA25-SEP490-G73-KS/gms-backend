package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.dto.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.TimeSlotDto;
import fpt.edu.vn.gms.service.AppointmentService;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @GetMapping("/time-slots")
    public List<TimeSlotDto> getSlots(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getTimeSlotsByDate(date);
    }

    @PostMapping()
    public AppointmentResponseDto createAppointment(@RequestBody AppointmentRequestDto dto) {
        return service.createAppointment(dto);
    }

    // Get all appointment
    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDto>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(service.getAllAppointments(page, size));
    }

    // Get appointment by appointmentId
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointmentById(id));
    }

    // Update appointment status
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDto> updateStatus(@PathVariable Long id,
                                                               @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

}
