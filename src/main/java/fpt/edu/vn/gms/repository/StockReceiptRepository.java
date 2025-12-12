package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.PurchaseRequest;
import fpt.edu.vn.gms.entity.StockReceipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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
}
