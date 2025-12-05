package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.LedgerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LedgerVoucherRepository extends JpaRepository<LedgerVoucher, Long>, JpaSpecificationExecutor<LedgerVoucher> {
}
