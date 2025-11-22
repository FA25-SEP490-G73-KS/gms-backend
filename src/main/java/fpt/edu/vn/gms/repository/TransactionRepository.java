package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Payment;
import fpt.edu.vn.gms.entity.Transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  Optional<Transaction> findByPaymentLinkId(String paymentLinkId);

  List<Transaction> findByPayment(Payment payment);

  /**
   * Lấy tất cả transaction của một payment nhưng chỉ lấy transaction active
   */
  List<Transaction> findByPaymentAndIsActiveTrue(Payment payment);
}
