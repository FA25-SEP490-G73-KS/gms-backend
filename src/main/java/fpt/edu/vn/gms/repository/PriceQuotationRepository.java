package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.ExportStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceQuotationRepository extends JpaRepository<PriceQuotation, Long> {

    Page<PriceQuotation> findByStatus(PriceQuotationStatus status, Pageable pageable);

    Page<PriceQuotation> findByExportStatus(ExportStatus exportStatus, Pageable pageable);

    long countByStatus(PriceQuotationStatus status);
}
