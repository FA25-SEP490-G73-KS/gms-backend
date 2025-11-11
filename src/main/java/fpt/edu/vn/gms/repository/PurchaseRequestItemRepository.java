package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.PurchaseOrder;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRequestItemRepository extends JpaRepository<PurchaseRequestItem, Long> {

    List<PurchaseRequestItem> findByPurchaseRequest(PurchaseRequest purchaseRequest);
}
