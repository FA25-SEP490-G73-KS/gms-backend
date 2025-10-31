package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Integer> {
}
