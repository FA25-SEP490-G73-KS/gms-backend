package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
    SELECT COALESCE(SUM(p.finalAmount), 0)
    FROM Payment p
    WHERE p.quotation.serviceTicket.customer.customerId = :customerId
      AND p.paymentMethod IS NULL
""")
    Optional<BigDecimal> sumUnpaidByCustomer(@Param("customerId") Long customerId);


}
