package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.StockReceiptItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockReceiptItemHistoryRepository extends JpaRepository<StockReceiptItemHistory, Long> {

    List<StockReceiptItemHistory> findByStockReceiptItem_Id(Long itemId);
}

