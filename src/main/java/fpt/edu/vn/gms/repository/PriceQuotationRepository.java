package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.entity.PriceQuotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceQuotationRepository extends JpaRepository<PriceQuotation, Long> {

    Page<PriceQuotation> findByStatus(PriceQuotationStatus status, Pageable pageable);

    long countByStatus(PriceQuotationStatus status);

    long countByStatusIn(Iterable<PriceQuotationStatus> statuses);
}
