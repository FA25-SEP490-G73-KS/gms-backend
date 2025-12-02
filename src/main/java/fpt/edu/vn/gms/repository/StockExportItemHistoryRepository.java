package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.StockExportItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockExportItemHistoryRepository extends JpaRepository<StockExportItemHistory, Long> {

    List<StockExportItemHistory> findByStockExportItem_Id(Long exportItemId);
}

