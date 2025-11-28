package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.dto.response.AttendanceResponseDTO;
import fpt.edu.vn.gms.entity.Attendance;
import fpt.edu.vn.gms.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
  @Query("""
      SELECT new fpt.edu.vn.gms.dto.response.AttendanceResponseDTO(
        e.employeeId,
        e.fullName,
        e.phone,
        e.account.role,
        a.isPresent,
        a.note,
        a.recordedBy,
        a.recordedAt
      )
      FROM Employee e
      LEFT JOIN Attendance a ON e.employeeId = a.employee.employeeId
      LEFT JOIN Account ac ON ac.accountId = e.account.accountId
      WHERE a.date = :date
      """)
  List<AttendanceResponseDTO> findAllByDate(@Param("date") LocalDate date);

  List<Attendance> findByEmployeeAndDateBetween(Employee employee, LocalDate start, LocalDate end);

  Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);

  List<Attendance> findByDateBetween(LocalDate start, LocalDate end);
}
