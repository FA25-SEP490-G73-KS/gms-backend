package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.CustomerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    /**
     * Get all customers with pagination
     * @param pageable
     * @return
     */
    Page<CustomerDto> getAllCustumer(Pageable pageable);

    /**
     * Get a single customer by their ID
     * @param customerId the ID of the customer
     * @return the customer DTO
     */
    CustomerDto getCustumerByCustomerId(Long customerId);
}
