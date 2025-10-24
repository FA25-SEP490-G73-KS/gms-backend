package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.request.VehicleRequestDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.entity.Vehicle;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ServiceTicketMapper {

    // ----------- ENTITY -> RESPONSE -----------
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "vehicle", source = "vehicle")
    @Mapping(target = "advisorId", source = "serviceAdvisor.employeeId")
    @Mapping(target = "assignedTechnicianIds", expression = "java(mapTechnicianIds(serviceTicket.getTechnicians()))")
    @Mapping(target = "receiveCondition", source = "receiveCondition")
    @Mapping(target = "note", source = "notes")
    @Mapping(target = "expectedDeliveryAt", source = "deliveryAt")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ServiceTicketResponseDto toResponseDto(ServiceTicket serviceTicket);

    // ----------- CUSTOMER & VEHICLE NESTED MAPPING -----------
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "customerType", source = "customerType")
    @Mapping(target = "loyaltyLevel", source = "loyaltyLevel")
    CustomerRequestDto toCustomerDto(Customer customer);

    @Mapping(target = "licensePlate", source = "licensePlate")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "model", source = "model")
    @Mapping(target = "year", source = "year")
    @Mapping(target = "vin", source = "vin")
    VehicleRequestDto toVehicleDto(Vehicle vehicle);

    // ----------- SUPPORT FUNCTION -----------
    default List<Long> mapTechnicianIds(List<Employee> technicians) {
        if (technicians == null) return null;
        return technicians.stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toList());
    }
}
