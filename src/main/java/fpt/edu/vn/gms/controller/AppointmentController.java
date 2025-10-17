package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.TimeSlotDto;
import fpt.edu.vn.gms.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @GetMapping("/time-slots")
    public List<TimeSlotDto> getSlots(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getTimeSlotsByDate(date);
    }

    @PostMapping("/appointments")
    public AppointmentResponseDto createAppointment(@RequestBody AppointmentRequestDto dto) {
        return service.createAppointment(dto);
    }
}
