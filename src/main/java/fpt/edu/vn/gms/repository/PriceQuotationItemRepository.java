package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceQuotationItemRepository extends JpaRepository<PriceQuotationItem, Long> {

    List<PriceQuotationItem> findAllByPriceQuotation(PriceQuotation quotation);
}
