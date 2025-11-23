package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.StockReceipt;
import fpt.edu.vn.gms.entity.StockReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockReceiptItemRepository extends JpaRepository<StockReceiptItem, Long> {

    List<StockReceiptItem> findByStockReceipt(StockReceipt stockReceipt);
}
