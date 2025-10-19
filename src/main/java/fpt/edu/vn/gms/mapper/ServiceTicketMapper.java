package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.ServiceTicketDto;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.entity.Vehicle;

/**
 * Chuyển đổi giữa Entity ServiceTicket và DTO tương ứng.
 */
public class ServiceTicketMapper {

    public static ServiceTicketDto mapToServiceTicketDto(ServiceTicket serviceTicket) {
        if (serviceTicket == null) return null;
        Long appointmentId = serviceTicket.getAppointment() != null ? serviceTicket.getAppointment().getAppointmentId() : null;
        Long customerId = serviceTicket.getCustomer() != null ? serviceTicket.getCustomer().getCustomerId() : null;
        Long vehicleId = serviceTicket.getVehicle() != null ? serviceTicket.getVehicle().getVehicleId() : null;

        return ServiceTicketDto.builder()
                .serviceTicketId(serviceTicket.getServiceTicketId())
                .appointmentId(appointmentId)
                // Entity has no employee field; keep DTO's employeeId as null
                .employeeId(null)
                .customerId(customerId)
                .vehicleId(vehicleId)
                .status(serviceTicket.getStatus())
                .notes(serviceTicket.getNotes())
                .createdAt(serviceTicket.getCreatedAt())
                .deliveryAt(serviceTicket.getDeliveryAt())
                .build();
    }

    public static ServiceTicket mapToServiceTicket(ServiceTicketDto dto) {
        if (dto == null) return null;

        Appointment appointment = null;
        if (dto.getAppointmentId() != null) {
            appointment = new Appointment();
            appointment.setAppointmentId(dto.getAppointmentId());
        }

        Customer customer = null;
        if (dto.getCustomerId() != null) {
            customer = new Customer();
            customer.setCustomerId(dto.getCustomerId());
        }

        Vehicle vehicle = null;
        if (dto.getVehicleId() != null) {
            vehicle = new Vehicle();
            vehicle.setVehicleId(dto.getVehicleId());
        }

        return ServiceTicket.builder()
                .serviceTicketId(dto.getServiceTicketId())
                .appointment(appointment)
                .customer(customer)
                .vehicle(vehicle)
                .status(dto.getStatus())
                .notes(dto.getNotes())
                .createdAt(dto.getCreatedAt())
                .deliveryAt(dto.getDeliveryAt())
                .build();
    }
}
