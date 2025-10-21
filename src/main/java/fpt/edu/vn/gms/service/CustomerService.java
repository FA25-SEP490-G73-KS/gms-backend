package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.CustomerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    /**
     * Lây danh sách khách hàng
     * @param page số trang
     * @param size kích thước trang
     * @return danh sách khách hàng phân trang
     */
    Page<CustomerDto> getAllCustumer(int page, int size);

    /**
     * Get a single customer by their ID
     * @param customerId the ID of the customer
     * @return the customer DTO
     */
    CustomerDto getCustumerByCustomerId(Long customerId);
}
