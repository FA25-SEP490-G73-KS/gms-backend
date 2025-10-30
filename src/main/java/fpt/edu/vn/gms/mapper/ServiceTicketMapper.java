package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.CustomerResponseDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.dto.response.VehicleResponseDto;
import fpt.edu.vn.gms.entity.*;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ServiceTicketMapper {

    // ----------- ENTITY -> RESPONSE -----------
    @Mapping(target = "id", source = "serviceTicketId")
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "vehicle", source = "vehicle")
    @Mapping(target = "serviceType", expression = "java(mapServiceTypeNames(serviceTicket.getServiceTypes()))")
    @Mapping(target = "serviceAdvisor", source = "serviceAdvisor.fullName")
    @Mapping(target = "technicians", expression = "java(mapTechnicianNames(serviceTicket.getTechnicians()))") // danh sách tên
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "deliveryAt", source = "deliveryAt")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "status", source = "status")
    ServiceTicketResponseDto toResponseDto(ServiceTicket serviceTicket);

    // ----------- CUSTOMER & VEHICLE NESTED MAPPING -----------
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "customerType", source = "customerType")
    @Mapping(target = "loyaltyLevel", source = "loyaltyLevel")
    CustomerResponseDto toCustomerDto(Customer customer);

    @Mapping(target = "vehicleId", source = "vehicleId")
    @Mapping(target = "licensePlate", source = "licensePlate")
    @Mapping(target = "vin", source = "vin")
    @Mapping(target = "year", source = "year")
    @Mapping(target = "customerId", source = "customer.customerId")
    @Mapping(target = "vehicleModelId", source = "vehicleModel.vehicleModelId")
    @Mapping(target = "vehicleModelName", source = "vehicleModel.name")
    @Mapping(target = "brandName", source = "vehicleModel.brand.name")
    VehicleResponseDto toVehicleDto(Vehicle vehicle);

    // ----------- SUPPORT FUNCTION -----------
    default List<String> mapTechnicianNames(List<Employee> technicians) {
        if (technicians == null) return null;
        return technicians.stream()
                .map(Employee::getFullName)
                .collect(Collectors.toList());
    }

    default List<String> mapServiceTypeNames(List<ServiceType> serviceTypes) {
        if (serviceTypes == null) return new ArrayList<>();
        return serviceTypes.stream()
                .map(ServiceType::getName)
                .toList();
    }
}
