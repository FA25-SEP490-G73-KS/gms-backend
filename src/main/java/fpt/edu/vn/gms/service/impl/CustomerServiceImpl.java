package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.response.CustomerResponseDto;
import fpt.edu.vn.gms.entity.Customer;
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

//    @Override
//    public Page<CustomerDto> getAllCustumer(int page, int size) {
//        Pageable pageable = Pageable.ofSize(size).withPage(page);
//        return customerRepository.findAll(pageable).map(CustomerMapper::mapToCustomerDto);
//    }
//
//    @Override
//    public CustomerDto getCustumerByCustomerId(Long customerId) {
//        return customerRepository.findById(Math.toIntExact(customerId))
//                .map(CustomerMapper::mapToCustomerDto)
//                .orElse(null);
//    }


    @Override
    public List<CustomerResponseDto> searchByPhone(String phonePart) {

        List<Customer> customers = customerRepository.searchByPhoneContaining((phonePart));

        return customerMapper.toDtoList(customers);
    }
}
