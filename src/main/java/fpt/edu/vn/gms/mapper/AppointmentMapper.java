package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.ServiceType;

import java.util.List;

public class AppointmentMapper {

    public static AppointmentResponseDto toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        // Lấy danh sách tên service type
        List<String> serviceTypeNames = appointment.getServiceTypes() != null
                ? appointment.getServiceTypes().stream()
                .map(ServiceType::getName)
                .toList()
                : List.of();

        return AppointmentResponseDto.builder()
                .appointmentId(appointment.getAppointmentId())
                .customerId(appointment.getCustomer() != null ? appointment.getCustomer().getCustomerId() : null)
                .customerName(appointment.getCustomer() != null ? appointment.getCustomer().getFullName() : null)
                .customerPhone(appointment.getCustomer() != null ? appointment.getCustomer().getPhone() : null)
                .vehicleId(appointment.getVehicle() != null ? appointment.getVehicle().getVehicleId() : null)
                .licensePlate(appointment.getVehicle() != null ? appointment.getVehicle().getLicensePlate() : null)
                .appointmentDate(appointment.getAppointmentDate())
                .timeSlotLabel(appointment.getTimeSlot() != null ? appointment.getTimeSlot().getLabel() : null)
                .serviceType(serviceTypeNames)
                .status(appointment.getStatus())
                .note(appointment.getDescription())
                .createdAt(appointment.getCreatedAt()) // NEW
                .build();
    }
}
