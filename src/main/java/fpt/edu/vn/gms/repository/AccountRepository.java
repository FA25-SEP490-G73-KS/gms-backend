package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByPhone(String phone);

    List<Account> findByRole(Role role);
}
