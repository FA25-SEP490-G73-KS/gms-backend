package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.DiscountPolicy;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.CustomerMapper;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.repository.DiscountPolicyRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.repository.VehicleRepository;
import fpt.edu.vn.gms.service.CustomerService;
import fpt.edu.vn.gms.utils.PhoneUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    CustomerRepository customerRepository;
    DiscountPolicyRepository discountPolicyRepository;
    VehicleRepository vehicleRepository;
    CustomerMapper customerMapper;
    ServiceTicketRepository serviceTicketRepository;

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

        return customerMapper.toDetailDto(customerRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Không có khách hàng!!!")));
    }

    @Override
    public CustomerResponseDto createCustomer(CustomerRequestDto customerDto) {

        customerDto.setPhone(PhoneUtils.normalize(customerDto.getPhone()));

        if (customerRepository.existsByPhone(customerDto.getPhone())) {

            Customer oldCustomer = customerRepository.findByPhone(customerDto.getPhone())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng!"));

            oldCustomer.setIsActive(false);
            customerRepository.save(oldCustomer);
        }

        Customer customer = customerMapper.toEntity(customerDto);

        Customer saved = customerRepository.save(customer);

        return customerMapper.toDto(saved);
    }

    @Override
    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto) {

        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + id));

        dto.setPhone(PhoneUtils.normalize(dto.getPhone()));

        if (!existing.getPhone().equals(dto.getPhone()) && customerRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Số điện thoại đã tồn tại trong hệ thống!");
        }

        DiscountPolicy discountPolicy = discountPolicyRepository.findById(dto.getDiscountPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy chính sách giảm giá với ID: " + dto.getDiscountPolicyId()));

        existing.setFullName(dto.getFullName());
        existing.setPhone(dto.getPhone());
        existing.setAddress(dto.getAddress());
        existing.setCustomerType(dto.getCustomerType());
        existing.setDiscountPolicy(discountPolicy);

        Customer updated = customerRepository.save(existing);

        return customerMapper.toDto(updated);
    }

    @Override
    public CustomerDetailDto getCustomerServiceHistoryByPhone(String phone) {

        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Không có khách hàng!!!"));

        Long customerId = customer.getCustomerId();

        CustomerDetailDto dto = customerRepository.getCustomerDetail(customerId);

        dto.setVehicles(vehicleRepository.getCustomerVehicles(customerId));
        dto.setHistory(serviceTicketRepository.getCustomerServiceHistory(customerId));

        return dto;
    }

    @Override
    public void updateTotalSpending(Long customerId, BigDecimal additionalSpending) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Update totalSpending
        customer.setTotalSpending(customer.getTotalSpending().add(additionalSpending));

        // Find the best discount policy
        DiscountPolicy bestPolicy = discountPolicyRepository.findAll().stream()
                .filter(policy -> customer.getTotalSpending().compareTo(policy.getRequiredSpending()) >= 0)
                .max(Comparator.comparing(DiscountPolicy::getRequiredSpending))
                .orElse(null);

        customer.setDiscountPolicy(bestPolicy);

        customerRepository.save(customer);
    }

    @Override
    public Page<CustomerDetailDto> getCustomers(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return customerRepository.getAllCustomers(pageable);
    }

    @Override
    public CustomerDetailDto getCustomerDetail(Long customerId) {

        CustomerDetailDto dto = customerRepository.getCustomerDetail(customerId);

        dto.setVehicles(vehicleRepository.getCustomerVehicles(customerId));
        dto.setHistory(null);

        return dto;
    }

    @Override
    public CustomerDetailDto getServiceHistory(Long customerId) {
        CustomerDetailDto dto = customerRepository.getCustomerDetail(customerId);

        dto.setHistory(serviceTicketRepository.getCustomerServiceHistory(customerId));
        dto.setVehicles(null);

        return dto;
    }

    @Override
    @Transactional
    public CustomerResponseDto handleNotMe(String phone) {

        Customer old = customerRepository.findByPhone(phone)
                .orElse(null);

        if (old != null) {
            old.setIsActive(false);
            customerRepository.save(old);

            customerRepository.flush();
        }

        Customer newCus = new Customer();
        newCus.setPhone(phone);
        newCus.setIsActive(true);
        customerRepository.save(newCus);

        return customerMapper.toDto(newCus);
    }

    @Override
    @Transactional
    public CustomerResponseDto toggleActive(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));

        Boolean current = customer.getIsActive();
        boolean newStatus = current != null && !current;
        customer.setIsActive(newStatus);

        Customer saved = customerRepository.save(customer);
        return customerMapper.toDto(saved);
    }
}
