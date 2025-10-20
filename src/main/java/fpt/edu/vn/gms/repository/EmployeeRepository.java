package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE e.status = 'Active' and  e.position = 'Kỹ Thuật Viên'" )
    List<Employee> findAllEmployeeIsTechniciansActive();
}
