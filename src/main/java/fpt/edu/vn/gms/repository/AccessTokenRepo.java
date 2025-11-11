package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {
    AccessToken findTopByOrderByIdDesc();
}
