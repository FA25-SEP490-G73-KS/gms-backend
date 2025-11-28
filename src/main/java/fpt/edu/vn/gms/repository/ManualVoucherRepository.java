package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.ManualVoucher;
import fpt.edu.vn.gms.entity.StockReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManualVoucherRepository extends JpaRepository<ManualVoucher, Long> {

}
