package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.response.CustomerDetailResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerResponseDto;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = { VehicleMapper.class })
public interface CustomerMapper {

    @Mapping(target = "loyaltyLevel", source = "discountPolicy.loyaltyLevel")
    CustomerResponseDto toDto(Customer customer);

    List<CustomerResponseDto> toDtoList(List<Customer> customers);


    @Mapping(target = "loyaltyLevel", source = "discountPolicy.loyaltyLevel")
    @Mapping(target = "vehicles", source = "vehicles")
    CustomerDetailResponseDto toDetailDto(Customer customer);

    Customer toEntity(CustomerRequestDto dto);

    // MUST-HAVE: Default mapping method must be inside interface
    default List<String> mapLicensePlates(List<Vehicle> vehicles) {
        if (vehicles == null) return List.of();
        return vehicles.stream()
                .map(Vehicle::getLicensePlate)
                .toList();
    }
}
