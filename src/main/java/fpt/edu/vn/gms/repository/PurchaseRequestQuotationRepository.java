package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.PurchaseRequestQuotation;
import fpt.edu.vn.gms.entity.PurchaseRequestQuotationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestQuotationRepository extends JpaRepository<PurchaseRequestQuotation, PurchaseRequestQuotationId> {
}

