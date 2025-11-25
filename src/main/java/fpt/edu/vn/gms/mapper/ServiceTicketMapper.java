package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.entity.*;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { PriceQuotationMapper.class, CustomerMapper.class, VehicleMapper.class })
public interface ServiceTicketMapper {

    // ----------- ENTITY -> RESPONSE -----------
    @Named("toServiceTicketResponseDto")
    @Mapping(target = "serviceTicketCode", source = "serviceTicketCode")
    @Mapping(target = "serviceType", expression = "java(mapServiceTypeNames(serviceTicket.getServiceTypes()))")
    @Mapping(target = "createdBy", source = "createdBy.fullName")
    @Mapping(target = "technicians", expression = "java(mapTechnicianNames(serviceTicket.getTechnicians()))") // danh
                                                                                                              // sách
                                                                                                              // tên
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "vehicle", source = "vehicle")
    ServiceTicketResponseDto toResponseDto(ServiceTicket serviceTicket);

    // ----------- SUPPORT FUNCTION -----------
    default List<String> mapTechnicianNames(List<Employee> technicians) {
        if (technicians == null)
            return null;
        return technicians.stream()
                .map(Employee::getFullName)
                .collect(Collectors.toList());
    }

    default List<String> mapServiceTypeNames(List<ServiceType> serviceTypes) {
        if (serviceTypes == null)
            return new ArrayList<>();
        return serviceTypes.stream()
                .map(ServiceType::getName)
                .toList();
    }
}
