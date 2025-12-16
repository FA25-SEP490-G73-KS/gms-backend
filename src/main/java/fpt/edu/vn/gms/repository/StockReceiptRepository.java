package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.PurchaseRequest;
import fpt.edu.vn.gms.entity.StockReceipt;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface StockReceiptRepository extends JpaRepository<StockReceipt, Long> {

    Optional<StockReceipt> findByPurchaseRequest(PurchaseRequest pr);

    @Query("""
             SELECT r FROM StockReceipt r
             LEFT JOIN r.purchaseRequest pr
             LEFT JOIN pr.relatedQuotation q
             LEFT JOIN q.serviceTicket t
             LEFT JOIN t.vehicle v
             WHERE (:search IS NULL OR :search = ''
                    OR r.code LIKE %:search%
                    OR v.licensePlate LIKE %:search%)
            """)
    Page<StockReceipt> searchForAccounting(@Param("search") String search, Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(r.totalAmount), 0)
            FROM StockReceipt r
            WHERE r.status IN (fpt.edu.vn.gms.common.enums.StockReceiptStatus.RECEIVED,
                               fpt.edu.vn.gms.common.enums.StockReceiptStatus.PARTIAL_RECEIVED)
              AND (:year IS NULL OR FUNCTION('year', COALESCE(r.receivedAt, r.createdAt)) = :year)
            """)
    BigDecimal sumExpenseByYear(@Param("year") Integer year);

    @Query("""
            SELECT FUNCTION('month', COALESCE(r.receivedAt, r.createdAt)) AS month,
                   COALESCE(SUM(r.totalAmount), 0) AS total
            FROM StockReceipt r
            WHERE r.status IN (fpt.edu.vn.gms.common.enums.StockReceiptStatus.RECEIVED,
                               fpt.edu.vn.gms.common.enums.StockReceiptStatus.PARTIAL_RECEIVED)
              AND (:year IS NULL OR FUNCTION('year', COALESCE(r.receivedAt, r.createdAt)) = :year)
            GROUP BY FUNCTION('month', COALESCE(r.receivedAt, r.createdAt))
            ORDER BY month
            """)
    List<Object[]> sumExpenseByMonth(@Param("year") Integer year);
}
