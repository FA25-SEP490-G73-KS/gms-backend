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

        // Lấy các thực thể liên quan
        Customer customer = serviceTicket.getCustomer();
        Vehicle vehicle = serviceTicket.getVehicle();
        Appointment appointment = serviceTicket.getAppointment();

        // Lấy các ID
        Long appointmentId = (appointment != null) ? appointment.getAppointmentId() : null;
        Long customerId = (customer != null) ? customer.getCustomerId() : null;
        Long vehicleId = (vehicle != null) ? vehicle.getVehicleId() : null;

        // Lấy thông tin khách hàng
        String fullName = (customer != null) ? customer.getFullName() : null;
        String phone = (customer != null) ? customer.getPhone() : null;
        String zaloId = (customer != null) ? customer.getZaloId() : null;
        String address = (customer != null) ? customer.getAddress() : null;
        CustomerType customerType = (customer != null) ? customer.getCustomerType() : null;
        CustomerLoyaltyLevel loyaltyLevel = (customer != null) ? customer.getLoyaltyLevel() : null;

        // Lấy thông tin xe
        String licensePlate = (vehicle != null) ? vehicle.getLicensePlate() : null;
        String brand = (vehicle != null) ? vehicle.getBrand() : null;
        String model = (vehicle != null) ? vehicle.getModel() : null;
        Integer year = (vehicle != null) ? vehicle.getYear() : null;
        String vin = (vehicle != null) ? vehicle.getVin() : null;

        // Trả về DTO
        return ServiceTicketDto.builder()
                .serviceTicketId(serviceTicket.getServiceTicketId())
                .appointmentId(appointmentId)
                .customerId(customerId)
                .vehicleId(vehicleId)
                .status(serviceTicket.getStatus())
                .notes(serviceTicket.getNotes())
                .createdAt(serviceTicket.getCreatedAt())
                .deliveryAt(serviceTicket.getDeliveryAt())
                .fullName(fullName)
                .phone(phone)
                .zaloId(zaloId)
                .address(address)
                .customerType(customerType)
                .loyaltyLevel(loyaltyLevel)
                .licensePlate(licensePlate)
                .brand(brand)
                .model(model)
                .year(year)
                .vin(vin)
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

        // Lấy các ID từ DTO
        Long appointmentId = (dto.getAppointmentId() != null) ? dto.getAppointmentId() : null;
        Long customerId = (dto.getCustomerId() != null) ? dto.getCustomerId() : null;
        Long vehicleId = (dto.getVehicleId() != null) ? dto.getVehicleId() : null;

        // Khởi tạo entity liên quan
        Appointment appointment = (appointmentId != null) ? new Appointment() : null;
        if (appointment != null) appointment.setAppointmentId(appointmentId);

        Customer customer = (customerId != null) ? new Customer() : null;
        if (customer != null) customer.setCustomerId(customerId);

        Vehicle vehicle = (vehicleId != null) ? new Vehicle() : null;
        if (vehicle != null) vehicle.setVehicleId(vehicleId);

        // Trả về ServiceTicket entity
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
