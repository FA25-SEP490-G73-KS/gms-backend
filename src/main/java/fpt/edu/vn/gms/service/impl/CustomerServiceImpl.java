package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.response.CustomerDetailResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerResponseDto;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.CustomerMapper;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDto> searchCustomersByPhone(String query) {
        if (query == null || query.isEmpty()) {
            return List.of();
        }
        return customerRepository.findTop10ByPhoneContainingOrderByPhoneAsc(query)
                .stream()
                .map(c -> new CustomerDto(c.getCustomerId(), c.getFullName(), c.getPhone()))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDetailResponseDto getCustomerDetailById(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));

        return customerMapper.toDetailDto(customer);
    }

    @Override
    public Page<CustomerResponseDto> getAllCustomers(int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return customerRepository.findAll(pageable).map(customerMapper::toDto);
    }

    @Override
    public CustomerDetailResponseDto getByPhone(String phone) {

        return customerMapper.toDetailDto(customerRepository.findByPhone(phone).
                orElseThrow(() -> new ResourceNotFoundException("Không có khách hàng!!!")));
    }

    @Override
    public CustomerResponseDto createCustomer(CustomerRequestDto customerDto) {

        if (customerRepository.existsByPhone(customerDto.getPhone())) {
            throw new RuntimeException("Số điện thoại đã tồn tại trong hệ thống!");
        }

        Customer customer = customerMapper.toEntity(customerDto);
        Customer saved = customerRepository.save(customer);

        return customerMapper.toDto(saved);
    }

    @Override
    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto) {

        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + id));

        if (!existing.getPhone().equals(dto.getPhone()) && customerRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Số điện thoại đã tồn tại trong hệ thống!");
        }

        existing.setFullName(dto.getFullName());
        existing.setPhone(dto.getPhone());
        existing.setAddress(dto.getAddress());
        existing.setCustomerType(dto.getCustomerType());
        existing.setLoyaltyLevel(dto.getLoyaltyLevel());

        Customer updated = customerRepository.save(existing);

        return customerMapper.toDto(updated);
    }
}
