package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    /**
     * Lây danh sách nhân viên với phân trang
     * @param pageable
     * @return
     */
    Page<EmployeeDto> getAllEmployee(Pageable pageable);

    /**
     * Lấy danh sách kỹ thuật viên đang hoạt động
     * @return
     */
    List<Employee> findAllEmployeeIsTechniciansActive();

    /**
     * Get an employee by their ID
     * @param employeeId the ID of the employee
     * @return employee DTO
     */
    EmployeeDto getEmployeeByEmployeeId(Long employeeId);
}
