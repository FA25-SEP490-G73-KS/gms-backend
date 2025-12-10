package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.response.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    List<CustomerDto> searchCustomersByPhone(String query);

    CustomerDetailResponseDto getCustomerDetailById(Long id);

    Page<CustomerResponseDto> getAllCustomers(int page, int size);

    Page<CustomerDetailDto> getCustomers(int page, int size);

    CustomerDetailResponseDto getByPhone(String phone);

    CustomerResponseDto createCustomer(CustomerRequestDto customerDto);

    CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto);

    CustomerDetailDto getCustomerServiceHistoryByPhone(String phone);

    void updateTotalSpending(Long customerId, BigDecimal addtionalSpending);

    CustomerDetailDto getCustomerDetail(Long customerId);

    CustomerDetailDto getServiceHistory(Long customerId);

    CustomerResponseDto handleNotMe(String phone);

    CustomerResponseDto toggleActive(Long customerId);

    /**
     * Lấy thông tin khách hàng theo số điện thoại và chỉ trả về khi khách đã từng
     * sử dụng dịch vụ.
     * Nếu không tồn tại hoặc chưa có lịch sử dịch vụ sẽ bắn
     * ResourceNotFoundException.
     */
    CustomerDetailDto getCustomerIfHasServiceHistory(String phone);
}
