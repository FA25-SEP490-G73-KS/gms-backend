package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.response.CustomerDetailResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

//    Page<CustomerDto> getAllCustumer(int page, int size);

//    CustomerDto getCustumerByCustomerId(Long customerId);

    List<CustomerDto> searchCustomersByPhone(String query);

    CustomerDetailResponseDto getCustomerDetailById(Long id);
}
