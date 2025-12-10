package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.response.EmployeeInfoResponseDto;
import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE e.isActive = true and  e.account IS NULL")
    List<EmployeeDto> findAllEmployeeIsTechniciansActive();

    EmployeeInfoResponseDto findEmployeeInfoByPhone(String phone);

    Employee findByPhone(String phone);

    @Query("SELECT e FROM Employee e WHERE e.account.role = :role")
    List<Employee> findByRole(Role role);

    Optional<Employee> findByAccount(Account account);

    @Query("SELECT e FROM Employee e LEFT JOIN Attendance a ON e.employeeId = a.employee.employeeId WHERE a.isPresent IS NOT NULL AND a.date = :date")
    Set<Employee> findEmployeesWithAttendances(@Param("date") LocalDate date);
}
