package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.OneTimeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OneTimeTokenRepository extends JpaRepository<OneTimeToken, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM OneTimeToken o WHERE o.token = :token")
    void deleteByToken(@Param("token") String token);
}
