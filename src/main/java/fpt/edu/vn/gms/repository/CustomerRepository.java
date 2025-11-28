package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.dto.response.CustomerDetailDto;
import fpt.edu.vn.gms.dto.response.CustomerDetailResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerListResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerServiceHistoryDto;
import fpt.edu.vn.gms.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);

    // Lấy top 10 số điện thoại khớp với ký tự
    List<Customer> findTop10ByPhoneContainingOrderByPhoneAsc(String phone);

    boolean existsByPhone(String phone);

    @Query("""
    SELECT new fpt.edu.vn.gms.dto.response.CustomerListResponseDto(
        c.customerId,
        c.fullName,
        c.phone,
        dp.loyaltyLevel,
        c.totalSpending,
        COUNT(st.serviceTicketId),
        COUNT(DISTINCT v.vehicleId),
        c.isActive
    )
    FROM Customer c
    LEFT JOIN c.discountPolicy dp
    LEFT JOIN c.vehicles v
    LEFT JOIN ServiceTicket st ON st.customer.customerId = c.customerId
    GROUP BY 
        c.customerId, c.fullName, c.phone,
        dp.loyaltyLevel, c.totalSpending, c.isActive
    """)
    Page<CustomerListResponseDto> getAllCustomers(Pageable pageable);

    @Query("""
    SELECT new fpt.edu.vn.gms.dto.response.CustomerDetailDto(
        c.customerId,
        c.fullName,
        c.phone,
        c.address,
        dp.loyaltyLevel,
        c.totalSpending,
        COUNT(DISTINCT v.vehicleId),
        COUNT(st.serviceTicketId),
        c.isActive,
        null,
        null
    )
    FROM Customer c
    LEFT JOIN c.discountPolicy dp
    LEFT JOIN c.vehicles v
    LEFT JOIN ServiceTicket st ON st.customer.customerId = c.customerId
    WHERE c.customerId = :customerId
    GROUP BY c.customerId, c.fullName, c.phone, c.address,
             dp.loyaltyLevel, c.totalSpending, c.isActive
    """)
    CustomerDetailDto getCustomerDetail(Long customerId);


}
