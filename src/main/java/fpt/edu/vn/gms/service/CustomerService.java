package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.response.CustomerDetailResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerServiceHistoryResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {

    List<CustomerDto> searchCustomersByPhone(String query);

    CustomerDetailResponseDto getCustomerDetailById(Long id);

    Page<CustomerResponseDto>  getAllCustomers(int page, int size);

    CustomerDetailResponseDto getByPhone(String phone);

    CustomerResponseDto createCustomer(CustomerRequestDto customerDto);

    CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto);

    CustomerServiceHistoryResponseDto getCustomerServiceHistoryByPhone(String phone);

}
