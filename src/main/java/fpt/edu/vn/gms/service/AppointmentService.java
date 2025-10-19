package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.dto.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.TimeSlotDto;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.Vehicle;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    List<TimeSlotDto> getTimeSlotsByDate(LocalDate date);
    AppointmentResponseDto createAppointment(AppointmentRequestDto dto);

    // Get all appointment
    List<AppointmentResponseDto> getAllAppointments();

    // Get appointment by id
    AppointmentResponseDto getAppointmentById(Long id);

    // Update appointment status
    AppointmentResponseDto updateStatus(Long id, AppointmentStatus status);

}
