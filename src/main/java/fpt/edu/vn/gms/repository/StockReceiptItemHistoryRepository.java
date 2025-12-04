package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.StockReceiptItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface StockReceiptItemHistoryRepository extends JpaRepository<StockReceiptItemHistory, Long>, JpaSpecificationExecutor<StockReceiptItemHistory> {

    List<StockReceiptItemHistory> findByStockReceiptItem_Id(Long itemId);
}
