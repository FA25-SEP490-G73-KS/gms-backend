package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByEmployee_EmployeeId(Long employeeId);
    List<Payroll> findByMonthAndYear(Integer month, Integer year);
    boolean existsByMonthAndYear(Integer month, Integer year);
    @Query("SELECT p FROM Payroll p WHERE p.employee.employeeId = :employeeId AND p.month = :month AND p.year = :year")
    java.util.Optional<Payroll> findByEmployeeIdAndMonthAndYear(@Param("employeeId") Long employeeId, @Param("month") Integer month, @Param("year") Integer year);
}

