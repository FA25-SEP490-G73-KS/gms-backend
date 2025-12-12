package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.StockExport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StockExportRepository extends JpaRepository<StockExport, Long> {

    @Query("SELECT s FROM StockExport s WHERE s.quotation.priceQuotationId = :quotationId")
    Optional<StockExport> findByQuotationId(Long quotationId);
}
