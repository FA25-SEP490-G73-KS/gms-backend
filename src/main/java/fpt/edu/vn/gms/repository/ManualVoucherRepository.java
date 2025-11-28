package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.LedgerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ManualVoucherRepository extends JpaRepository<LedgerVoucher, Long> {

    @Query("""
        SELECT COALESCE(SUM(m.amount), 0)
        FROM LedgerVoucher m
        WHERE m.relatedEmployeeId = :employeeId
          AND m.type = fpt.edu.vn.gms.common.enums.ManualVoucherType.ADVANCE_SALARY
          AND MONTH(m.createdAt) = :month
          AND YEAR(m.createdAt) = :year
          AND m.status = fpt.edu.vn.gms.common.enums.ManualVoucherStatus.APPROVED
    """)
    BigDecimal sumAdvanceSalary(@Param("employeeId") Long employeeId,
                                @Param("month") Integer month,
                                @Param("year") Integer year);

    List<LedgerVoucher> findByRelatedEmployeeId(Long employeeId);
}
