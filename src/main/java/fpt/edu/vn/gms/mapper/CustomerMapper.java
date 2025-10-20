package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.entity.Customer;

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
                .customerType(entity.getCustomerType())
                .loyaltyLevel(entity.getLoyaltyLevel())
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
                .customerType(dto.getCustomerType())
                .loyaltyLevel(dto.getLoyaltyLevel())
                .build();
    }
}
