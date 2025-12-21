package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.entity.PriceQuotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PriceQuotationRepository extends JpaRepository<PriceQuotation, Long> {

    Page<PriceQuotation> findByStatus(PriceQuotationStatus status, Pageable pageable);

    long countByStatus(PriceQuotationStatus status);

    long countByStatusIn(Iterable<PriceQuotationStatus> statuses);

    java.util.Optional<PriceQuotation> findByCode(String code);

    @Query("""
                SELECT DISTINCT q FROM PriceQuotation q
                JOIN q.items item
                WHERE q.status = :status
                AND item.inventoryStatus = :outOfStockStatus
                AND (:keyword IS NULL OR :keyword = '' OR LOWER(q.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
                     OR LOWER(q.serviceTicket.customerName) LIKE LOWER(CONCAT('%', :keyword, '%')))
                AND (:fromDate IS NULL OR q.createdAt >= :fromDate)
                AND (:toDate IS NULL OR q.createdAt <= :toDate)
                ORDER BY q.createdAt DESC
            """)
    Page<PriceQuotation> findAvailableForPurchaseRequest(
            @Param("status") PriceQuotationStatus status,
            @Param("outOfStockStatus") PriceQuotationItemStatus outOfStockStatus,
            @Param("keyword") String keyword,
            @Param("fromDate") java.time.LocalDateTime fromDate,
            @Param("toDate") java.time.LocalDateTime toDate,
            Pageable pageable);
}