package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.PurchaseRequest;
import fpt.edu.vn.gms.entity.StockReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockReceiptRepository extends JpaRepository<StockReceipt, Integer> {

    Optional<StockReceipt> findByPurchaseRequest(PurchaseRequest pr);
}
