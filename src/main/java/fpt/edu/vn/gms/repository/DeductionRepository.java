package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {

    @Query("""
        SELECT COALESCE(SUM(d.amount), 0)
        FROM Deduction d
        WHERE d.employee.employeeId = :employeeId
          AND MONTH(d.date) = :month
          AND YEAR(d.date) = :year
    """)
    BigDecimal sumForMonth(@Param("employeeId") Long employeeId,
                           @Param("month") Integer month,
                           @Param("year") Integer year);

    List<Deduction> findByEmployeeEmployeeIdAndDateBetween(Long employeeId,
                                                           LocalDate start,
                                                           LocalDate end);
}
