package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.response.TimeSlotDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    List<TimeSlotDto> getTimeSlotsByDate(LocalDate date);

    AppointmentResponseDto createAppointment(AppointmentRequestDto dto);

    // Get all appointment
    Page<AppointmentResponseDto> getAllAppointments(int page, int size);

    // Get appointment by id
    AppointmentResponseDto getAppointmentById(Long id);

    // Update appointment status
    AppointmentResponseDto updateStatus(Long id, AppointmentStatus status);

}
