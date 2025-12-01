package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.dto.response.CustomerDebtSummaryDto;
import fpt.edu.vn.gms.entity.Debt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {

        @Query("""
                            SELECT COALESCE(SUM(d.amount - d.paidAmount), 0)
                            FROM Debt d
                            WHERE d.customer.customerId = :customerId
                        """)
        BigDecimal getTotalDebt(Long customerId);

        @Query("""
                            SELECT d
                            FROM Debt d
                            WHERE d.customer.customerId = :customerId
                              AND (:status IS NULL OR d.status = :status)
                              AND (:keyword IS NULL OR LOWER(d.serviceTicket.serviceTicketCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
                        """)
        Page<Debt> findByCustomerAndFilter(
                        @Param("customerId") Long customerId,
                        @Param("status") DebtStatus status,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        Optional<Debt> findByIdAndCustomerCustomerId(Long debtId, Long customerId);

        @Query(value = """
                    SELECT new fpt.edu.vn.gms.dto.response.CustomerDebtSummaryDto(
                        d.customer.customerId,
                        d.customer.fullName,
                        d.customer.phone,
                        COALESCE(SUM(d.amount), CAST(0 AS bigdecimal)),
                        COALESCE(SUM(d.paidAmount), CAST(0 AS bigdecimal)),
                        COALESCE(SUM(d.amount - d.paidAmount), CAST(0 AS bigdecimal)),
                        CASE WHEN SUM(d.amount) = SUM(d.paidAmount)
                             THEN 'PAID_IN_FULL'
                             ELSE 'OUTSTANDING'
                        END
                    )
                    FROM Debt d
                    GROUP BY d.customer.customerId, d.customer.fullName, d.customer.phone
                """,
                countQuery = "SELECT COUNT(DISTINCT d.customer.customerId) FROM Debt d")
        Page<CustomerDebtSummaryDto> findTotalDebtGroupedByCustomer(Pageable pageable);
}
