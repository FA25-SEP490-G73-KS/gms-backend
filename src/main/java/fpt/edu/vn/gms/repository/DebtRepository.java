package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.entity.Debt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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

        Optional<Debt> findByServiceTicket_ServiceTicketId(Long serviceTicketId);

        @Query(value = """
                    SELECT 
                        d.customer.customerId,
                        d.customer.fullName,
                        d.customer.phone,
                        SUM(d.amount),
                        SUM(d.paidAmount),
                        SUM(d.amount - d.paidAmount),
                        d.dueDate,
                        CASE WHEN SUM(d.amount) = SUM(d.paidAmount)
                             THEN 'PAID_IN_FULL'
                             ELSE 'OUTSTANDING'
                        END
                    FROM Debt d
                    WHERE (:status IS NULL OR d.status = :status)
                      AND (:fromDate IS NULL OR d.dueDate >= :fromDate)
                      AND (:toDate IS NULL OR d.dueDate <= :toDate)
                    GROUP BY d.customer.customerId, d.customer.fullName, d.customer.phone, d.dueDate
                """,
                countQuery = """
                    SELECT COUNT(DISTINCT d.customer.customerId)
                    FROM Debt d
                    WHERE (:status IS NULL OR d.status = :status)
                      AND (:fromDate IS NULL OR d.dueDate >= :fromDate)
                      AND (:toDate IS NULL OR d.dueDate <= :toDate)
                """)
        Page<Object[]> findTotalDebtGroupedByCustomer(@Param("status") DebtStatus status,
                                                      @Param("fromDate") LocalDate fromDate,
                                                      @Param("toDate") LocalDate toDate,
                                                      Pageable pageable);

        @Query("""
                        SELECT COALESCE(SUM(d.amount - d.paidAmount), 0)
                        FROM Debt d
                        WHERE d.status = fpt.edu.vn.gms.common.enums.DebtStatus.OUTSTANDING
                        """)
        BigDecimal sumOutstandingDebt();
}
