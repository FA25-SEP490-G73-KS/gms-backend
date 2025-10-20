package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByPhone(String phone);
}