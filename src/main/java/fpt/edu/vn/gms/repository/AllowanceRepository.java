package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Allowance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AllowanceRepository extends JpaRepository<Allowance, Long> {

    @Query("""
        SELECT COALESCE(SUM(a.amount), 0)
        FROM Allowance a
        WHERE a.employee.employeeId = :employeeId
          AND a.month = :month
          AND a.year = :year
    """)
    BigDecimal sumForMonth(@Param("employeeId") Long employeeId,
                           @Param("month") Integer month,
                           @Param("year") Integer year);

    List<Allowance> findByEmployeeEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
}
