package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.response.CustomerDetailResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerResponseDto;
import fpt.edu.vn.gms.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {VehicleMapper.class})
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(target = "vehicles", source = "vehicles")
    CustomerDetailResponseDto toDetailDto(Customer customer);

    // Map 1 entity sang DTO
    CustomerResponseDto toDto(Customer customer);

    // Map list entity sang list DTO
    List<CustomerResponseDto> toDtoList(List<Customer> customers);

    Customer toEntity(CustomerRequestDto dto);
}
