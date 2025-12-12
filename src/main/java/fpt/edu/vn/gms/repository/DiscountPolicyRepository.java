package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.entity.DiscountPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicy, Long> {

    Optional<DiscountPolicy> findByLoyaltyLevel(CustomerLoyaltyLevel loyaltyLevel);
}
