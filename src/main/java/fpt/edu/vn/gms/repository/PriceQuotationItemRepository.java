package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PriceQuotationItemRepository extends JpaRepository<PriceQuotationItem, Long> {

    List<PriceQuotationItem> findAllByPriceQuotation(PriceQuotation quotation);

    @Query("""
        SELECT i FROM PriceQuotationItem i
        LEFT JOIN FETCH i.priceQuotation pq
        LEFT JOIN FETCH i.part p
        LEFT JOIN FETCH p.category
        LEFT JOIN FETCH p.vehicleModel vm
        LEFT JOIN FETCH p.market
        LEFT JOIN FETCH p.unit
        LEFT JOIN FETCH p.supplier
        WHERE i.priceQuotationItemId = :itemId
    """)
    PriceQuotationItem getDetailById(@Param("itemId") Long itemId);
}
