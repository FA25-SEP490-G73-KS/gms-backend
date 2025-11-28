package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.dto.response.PurchaseRequestItemDetailDto;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseRequestItemRepository extends JpaRepository<PurchaseRequestItem, Long> {

//    PurchaseRequestItem findByPurchaseRequestId(Long prId);

    @Query("""
    SELECT new fpt.edu.vn.gms.dto.response.PurchaseRequestItemDetailDto(
        pri.itemId,
        p.name,
        p.market.name,
        p.supplier.name,
        p.vehicleModel.name,
        p.quantityInStock,
        pri.quantity,
        p.purchasePrice,
        pri.estimatedPurchasePrice,
        pri.status,
        pri.reviewStatus,
        pri.note
    )
    FROM PurchaseRequestItem pri
    JOIN pri.part p
    JOIN p.market m
    JOIN p.supplier s
    JOIN p.vehicleModel vm
    WHERE pri.purchaseRequest.id = :purchaseRequestId
""")
    List<PurchaseRequestItemDetailDto> findItemsByPurchaseRequestId(Long purchaseRequestId);
}
