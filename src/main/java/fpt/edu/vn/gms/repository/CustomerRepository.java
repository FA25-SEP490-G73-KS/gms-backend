package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.dto.response.CustomerDetailResponseDto;
import fpt.edu.vn.gms.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);

    // Lấy top 10 số điện thoại khớp với ký tự
    List<Customer> findTop10ByPhoneContainingOrderByPhoneAsc(String phone);

    boolean existsByPhone(String phone);
}
