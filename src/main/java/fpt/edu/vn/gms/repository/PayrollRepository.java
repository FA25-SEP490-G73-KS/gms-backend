package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    @Query("""
        SELECT p
        FROM Payroll p
        WHERE p.employee.employeeId = :employeeId
          AND p.month = :month
          AND p.year = :year
    """)
    Optional<Payroll> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);

    boolean existsByMonthAndYear(Integer month, Integer year);

    @Query("""
        SELECT p
        FROM Payroll p
        WHERE p.month = :month AND p.year = :year
    """)
    List<Payroll> findAllByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);
}
