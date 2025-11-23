package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.ExpenseVoucher;
import fpt.edu.vn.gms.entity.StockReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseVoucherRepository extends JpaRepository<ExpenseVoucher, Long> {

    boolean existsByStockReceiptItem(StockReceiptItem item);
}
