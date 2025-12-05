package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.StockReceiptItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockReceiptItemHistoryRepository extends JpaRepository<StockReceiptItemHistory, Long>, JpaSpecificationExecutor<StockReceiptItemHistory> {

    List<StockReceiptItemHistory> findByStockReceiptItem_Id(Long itemId);

    // Warehouse dashboard: daily import cost for a specific month
    @Query("""
        SELECT EXTRACT(DAY FROM h.receivedAt) AS day, COALESCE(SUM(h.totalPrice), 0)
        FROM StockReceiptItemHistory h
        WHERE YEAR(h.receivedAt) = :year AND MONTH(h.receivedAt) = :month
        GROUP BY EXTRACT(DAY FROM h.receivedAt)
        ORDER BY h.receivedAt
    """)
    List<Object[]> getDailyImportCost(@Param("year") int year, @Param("month") int month);

    // Warehouse dashboard: top imported parts in a given month
    @Query("""
        SELECT h.stockReceiptItem.purchaseRequestItem.part.partId,
               h.stockReceiptItem.purchaseRequestItem.part.name,
               h.stockReceiptItem.purchaseRequestItem.part.unit.name,
               SUM(h.quantity)
        FROM StockReceiptItemHistory h
        WHERE YEAR(h.receivedAt) = :year AND MONTH(h.receivedAt) = :month
        GROUP BY h.stockReceiptItem.purchaseRequestItem.part.partId,
                 h.stockReceiptItem.purchaseRequestItem.part.name,
                 h.stockReceiptItem.purchaseRequestItem.part.unit.name
        ORDER BY SUM(h.quantity) DESC
    """)
    List<Object[]> getTopImportedParts(@Param("year") int year, @Param("month") int month);

    // Warehouse dashboard: monthly import cost for all time (grouped by year & month)
    @Query("""
        SELECT YEAR(h.receivedAt) AS year,
               MONTH(h.receivedAt) AS month,
               COALESCE(SUM(h.totalPrice), 0)
        FROM StockReceiptItemHistory h
        GROUP BY YEAR(h.receivedAt), MONTH(h.receivedAt)
        ORDER BY YEAR(h.receivedAt), MONTH(h.receivedAt)
    """)
    List<Object[]> getMonthlyImportCostAllTime();

// Warehouse dashboard: top imported parts for all time
@Query("""
        SELECT h.stockReceiptItem.purchaseRequestItem.part.partId,
               h.stockReceiptItem.purchaseRequestItem.part.name,
               h.stockReceiptItem.purchaseRequestItem.part.unit.name,
               SUM(h.quantity)
        FROM StockReceiptItemHistory h
        GROUP BY h.stockReceiptItem.purchaseRequestItem.part.partId,
                 h.stockReceiptItem.purchaseRequestItem.part.name,
                 h.stockReceiptItem.purchaseRequestItem.part.unit.name
        ORDER BY SUM(h.quantity) DESC
    """)
List<Object[]> getTopImportedPartsAllTime();
}
