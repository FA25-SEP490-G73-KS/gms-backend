package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Invoice;
import fpt.edu.vn.gms.entity.Transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  Optional<Transaction> findByPaymentLinkId(String paymentLinkId);

  List<Transaction> findByInvoice(Invoice invoice);

  /**
   * Lấy tất cả transaction của một payment nhưng chỉ lấy transaction active
   */
  List<Transaction> findByInvoiceAndIsActiveTrue(Invoice invoice);

  List<Transaction> findAllByCustomerPhone(String phone);

  List<Transaction> findByInvoiceId(Long invoiceId);
}
