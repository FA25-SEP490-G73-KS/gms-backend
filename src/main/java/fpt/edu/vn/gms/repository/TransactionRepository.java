package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Transaction;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  Optional<Transaction> findByPaymentLinkId(String paymentLinkId);
}
