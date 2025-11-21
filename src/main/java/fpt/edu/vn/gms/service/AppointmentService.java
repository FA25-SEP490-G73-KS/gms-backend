package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.enums.AppointmentStatus;
import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.response.AppointmentBySlotResponse;
import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.response.TimeSlotDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    List<TimeSlotDto> getTimeSlotsByDate(LocalDate date);

    AppointmentResponseDto createAppointment(AppointmentRequestDto dto);

    Page<AppointmentResponseDto> getAllAppointments(int page, int size);

    Page<AppointmentResponseDto> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable);

    List<AppointmentBySlotResponse> getAppointmentsByDate(LocalDate date);

    AppointmentResponseDto getAppointmentById(Long id);

    AppointmentResponseDto updateArrivedStatus(Long id);

    long countAppointmentsByDate(LocalDate date);

    AppointmentResponseDto updateStatus(Long id, AppointmentStatus status);

    AppointmentResponseDto confirmAppointment(Long id);

}
