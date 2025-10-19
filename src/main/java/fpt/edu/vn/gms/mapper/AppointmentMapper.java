package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.AppointmentResponseDto;
import fpt.edu.vn.gms.entity.Appointment;

public class AppointmentMapper {

    public static AppointmentResponseDto toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return AppointmentResponseDto.builder()
                .appointmentId(appointment.getAppointmentId())
                .customerName(appointment.getCustomer() != null ? appointment.getCustomer().getFullName() : null)
                .licensePlate(appointment.getVehicle() != null ? appointment.getVehicle().getLicensePlate() : null)
                .appointmentDate(appointment.getAppointmentDate())
                .timeSlotLabel(appointment.getTimeSlot() != null ? appointment.getTimeSlot().getLabel() : null)
                .serviceType(appointment.getServiceType())
                .status(appointment.getStatus())
                .note(appointment.getDescription())
                .createdAt(appointment.getCreatedAt()) // NEW
                .build();
    }
}
