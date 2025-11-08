package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.response.EmployeeInfoResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    /**
     * Tìm tất cả nhân viên kỹ thuật viên đang hoạt động
     * @return Danh sách nhân viên kỹ thuật viên đang hoạt động
     */
    @Query("SELECT e FROM Employee e WHERE e.status = 'Active' and  e.position = 'TECHNICIAN'" )
    List<EmployeeDto> findAllEmployeeIsTechniciansActive();

    EmployeeInfoResponseDto findEmployeeInfoByPhone(String phone);

    Employee findByPhone(String phone);
}
