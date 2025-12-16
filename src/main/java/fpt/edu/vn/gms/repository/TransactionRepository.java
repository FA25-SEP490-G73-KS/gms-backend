package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Invoice;
import fpt.edu.vn.gms.entity.Transaction;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  Optional<Transaction> findByPaymentLinkId(String paymentLinkId);

  List<Transaction> findByInvoice(Invoice invoice);

  /**
   * Lấy tất cả transaction của một payment nhưng chỉ lấy transaction active
   */
  List<Transaction> findByInvoiceAndIsActiveTrue(Invoice invoice);

  List<Transaction> findAllByCustomerPhone(String phone);

  // Thêm method: tìm transaction theo customerPhone và debtId
  List<Transaction> findAllByCustomerPhoneAndDebt_Id(String phone, Long debtId);

  List<Transaction> findByInvoiceId(Long invoiceId);

  @Query("""
          SELECT COALESCE(SUM(t.amount), 0)
          FROM Transaction t
          WHERE t.isActive = TRUE
            AND t.type = fpt.edu.vn.gms.common.enums.PaymentTransactionType.PAYMENT
            AND (:year IS NULL OR FUNCTION('year', t.createdAt) = :year)
          """)
  BigDecimal sumRevenueByYear(@Param("year") Integer year);

  @Query("""
          SELECT FUNCTION('month', t.createdAt) AS month,
                 COALESCE(SUM(t.amount), 0) AS total
          FROM Transaction t
          WHERE t.isActive = TRUE
            AND t.type = fpt.edu.vn.gms.common.enums.PaymentTransactionType.PAYMENT
            AND (:year IS NULL OR FUNCTION('year', t.createdAt) = :year)
          GROUP BY FUNCTION('month', t.createdAt)
          ORDER BY month
          """)
  List<Object[]> sumRevenueByMonth(@Param("year") Integer year);
}
