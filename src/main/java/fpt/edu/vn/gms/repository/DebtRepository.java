package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {

    @Query("""
        SELECT COALESCE(SUM(d.amount - d.paidAmount), 0)
        FROM Debt d
        WHERE d.customer.customerId = :customerId
    """)
    BigDecimal getTotalDebt(Long customerId);

}
