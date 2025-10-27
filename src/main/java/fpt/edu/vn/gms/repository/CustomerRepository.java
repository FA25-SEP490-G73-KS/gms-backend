package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByPhone(String phone);

    @Query(value = "SELECT * FROM customer WHERE phone LIKE %:phonePart% LIMIT 10", nativeQuery = true)
    List<Customer> searchByPhoneContaining(@Param("phonePart") String phonePart);
}
