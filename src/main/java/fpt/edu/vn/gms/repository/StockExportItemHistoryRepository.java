package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.StockExportItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockExportItemHistoryRepository extends JpaRepository<StockExportItemHistory, Long> {

    List<StockExportItemHistory> findByStockExportItem_Id(Long exportItemId);

    // Warehouse dashboard: monthly export quantity for all time (grouped by year & month)
    // Note: StockExportItemHistory does not have a totalPrice column, only quantity and timestamps.
    // If you need export COST, compute it in service using quantity * part price via joins there.
    @Query("""
        SELECT YEAR(h.exportedAt) AS year,
               MONTH(h.exportedAt) AS month,
               COALESCE(SUM(h.quantity), 0)
        FROM StockExportItemHistory h
        GROUP BY YEAR(h.exportedAt), MONTH(h.exportedAt)
        ORDER BY YEAR(h.exportedAt), MONTH(h.exportedAt)
    """)
    List<Object[]> getMonthlyExportCostAllTime();
}