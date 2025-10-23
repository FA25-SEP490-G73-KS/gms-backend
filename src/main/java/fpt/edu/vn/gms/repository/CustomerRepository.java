package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
