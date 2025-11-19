package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
    SELECT SUM(p.finalAmount) 
    FROM Payment p 
    WHERE p.serviceTicketId.serviceTicketId = :ticketId 
      AND p.paymentType = 'DEPOSIT'
    """)
    Optional<BigDecimal> sumDepositByTicket(@Param("ticketId") Long ticketId);

    @Query("""
    SELECT SUM(p.finalAmount) 
    FROM Payment p 
    WHERE p.quotationId.serviceTicket.customer.customerId = :customerId
      AND p.paymentMethod IS NULL 
    """)
    Optional<BigDecimal> sumUnpaidByCustomer(@Param("customerId") Long customerId);

}
