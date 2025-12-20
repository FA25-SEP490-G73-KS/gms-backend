package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByPhone(String phone);

    /**
     * Tìm account theo số điện thoại với TRIM để xử lý khoảng trắng trong database
     */
    @Query("SELECT a FROM Account a WHERE TRIM(a.phone) = TRIM(:phone)")
    Optional<Account> findByPhoneTrimmed(@Param("phone") String phone);

    List<Account> findByRole(Role role);
}
