package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.common.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.CustomerType;
import fpt.edu.vn.gms.dto.ServiceTicketDto;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.entity.Vehicle;

/**
 * Lớp mapper để chuyển đổi qua lại giữa ServiceTicket entity và ServiceTicketDto.
 */
public class ServiceTicketMapper {

    /**
     * Chuyển đổi từ ServiceTicket entity sang ServiceTicketDto.
     * Phương thức này thực hiện:
     * - Copy các thông tin cơ bản từ ServiceTicket
     * - Lấy thông tin từ các entity liên quan (Customer, Vehicle)
     * - Chuyển đổi các trường enum một cách an toàn
     * @param serviceTicket ServiceTicket entity cần chuyển đổi
     * @return ServiceTicketDto chứa thông tin đã được chuyển đổi
     */
    public static ServiceTicketDto mapToServiceTicketDto(ServiceTicket serviceTicket) {
        if (serviceTicket == null) return null;
        Long appointmentId = serviceTicket.getAppointment() != null ? serviceTicket.getAppointment().getAppointmentId() : null;
        Long customerId = serviceTicket.getCustomer() != null ? serviceTicket.getCustomer().getCustomerId() : null;
        Long vehicleId = serviceTicket.getVehicle() != null ? serviceTicket.getVehicle().getVehicleId() : null;

        Customer customer = serviceTicket.getCustomer();
        Vehicle vehicle = serviceTicket.getVehicle();

        // Convert string fields in Customer to enums expected by DTO (null-safe and tolerant to invalid values)
        CustomerType customerType = null;
        CustomerLoyaltyLevel loyaltyLevel = null;
        if (customer != null) {
            if (customer.getCustomerType() != null) {
                try { customerType = CustomerType.valueOf(String.valueOf(customer.getCustomerType())); } catch (IllegalArgumentException ignored) {}
            }
            if (customer.getLoyaltyLevel() != null) {
                try { loyaltyLevel = CustomerLoyaltyLevel.valueOf(String.valueOf(customer.getLoyaltyLevel())); } catch (IllegalArgumentException ignored) {}
            }
        }
        return ServiceTicketDto.builder()
                .serviceTicketId(serviceTicket.getServiceTicketId())
                .appointmentId(appointmentId)
                .customerId(customerId)
                .vehicleId(vehicleId)
                .status(serviceTicket.getStatus())
                .notes(serviceTicket.getNotes())
                .createdAt(serviceTicket.getCreatedAt())
                .deliveryAt(serviceTicket.getDeliveryAt())
                .fullName(customer != null ? customer.getFullName() : null)
                .phone(customer != null ? customer.getPhone() : null)
                .zaloId(customer != null ? customer.getZaloId() : null)
                .address(customer != null ? customer.getAddress() : null)
                .customerType(customerType)
                .loyaltyLevel(loyaltyLevel)
                .licensePlate(vehicle != null ? vehicle.getLicensePlate() : null)
                .brand(vehicle != null ? vehicle.getBrand() : null)
                .model(vehicle != null ? vehicle.getModel() : null)
                .year(vehicle != null ? vehicle.getYear() : null)
                .vin(vehicle != null ? vehicle.getVin() : null)
                .build();
    }

    /**
     * Chuyển đổi từ ServiceTicketDto sang ServiceTicket entity.
     * Phương thức này thực hiện:
     * - Tạo các entity liên quan (Appointment, Customer, Vehicle) với ID tương ứng
     * - Copy các thông tin cơ bản từ DTO sang entity
     *
     * @param dto ServiceTicketDto cần chuyển đổi
     * @return ServiceTicket entity chứa thông tin đã được chuyển đổi
     */
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
