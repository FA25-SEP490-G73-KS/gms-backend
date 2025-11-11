package fpt.edu.vn.gms.mapper;


import fpt.edu.vn.gms.common.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.CustomerType;
import fpt.edu.vn.gms.dto.CustomerDto;

import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.response.CustomerDetailResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerResponseDto;

import fpt.edu.vn.gms.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


/**
 * Lớp mapper để chuyển đổi qua lại giữa Customer Entity và CustomerDto
 */
public class CustomerMapper {
    /**
     * Chuyển đổi từ Customer Entity sang CustomerDto
     * @param entity đối tượng Customer cần chuyển đổi
     * @return CustomerDto tương ứng
     */
    public static CustomerDto mapToCustomerDto(Customer entity) {
        if (entity == null) return null;
        return CustomerDto.builder()
                .customerId(entity.getCustomerId())
                .fullName(entity.getFullName())
                .phone(entity.getPhone())
                .zaloId(entity.getZaloId())
                .address(entity.getAddress())
                .customerType(String.valueOf(entity.getCustomerType()))
                .loyaltyLevel(String.valueOf(entity.getLoyaltyLevel()))
                .build();
    }

    /**
     * Chuyển đổi từ CustomerDto sang Customer Entity
     * @param dto đối tượng CustomerDto cần chuyển đổi
     * @return Customer entity tương ứng
     */
    public static Customer mapToCustomerEntity(CustomerDto dto) {
        if (dto == null) return null;
        return Customer.builder()
                .customerId(dto.getCustomerId())
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .zaloId(dto.getZaloId())
                .address(dto.getAddress())
                .customerType(CustomerType.valueOf(dto.getCustomerType()))
                .loyaltyLevel(CustomerLoyaltyLevel.valueOf(dto.getLoyaltyLevel()))
                .build();
    }

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
