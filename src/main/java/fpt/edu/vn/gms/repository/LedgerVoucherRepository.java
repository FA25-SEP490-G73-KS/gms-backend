package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.LedgerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface LedgerVoucherRepository extends JpaRepository<LedgerVoucher, Long>, JpaSpecificationExecutor<LedgerVoucher> {
    
    /**
     * Tính tổng chi phí từ ledger voucher (không phải từ stock receipt)
     * @param year Năm (null nếu không filter)
     * @param month Tháng (null nếu không filter)
     * @return Tổng chi phí
     */
    @Query("""
            SELECT COALESCE(SUM(lv.amount), 0)
            FROM LedgerVoucher lv
            WHERE lv.receiptHistory IS NULL
              AND lv.status = fpt.edu.vn.gms.common.enums.LedgerVoucherStatus.APPROVED
              AND (:year IS NULL OR FUNCTION('year', COALESCE(lv.approvedAt, lv.createdAt)) = :year)
              AND (:month IS NULL OR FUNCTION('month', COALESCE(lv.approvedAt, lv.createdAt)) = :month)
            """)
    BigDecimal sumExpenseByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);
}
