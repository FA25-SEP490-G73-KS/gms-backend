package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.TimeSlotDto;
import fpt.edu.vn.gms.entity.Appointment;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    List<TimeSlotDto> getTimeSlotsByDate(LocalDate date);
    AppointmentResponseDto createAppointment(AppointmentRequestDto dto);
}
