package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.entity.Employee;

/**
 * Lớp EmployeeMapper chịu trách nhiệm chuyển đổi giữa thực thể Employee và đối tượng EmployeeDto.
 */
public class EmployeeMapper {
    /**
     * Chuyển đổi từ thực thể Employee sang đối tượng EmployeeDto tương ứng.
     * @param employee thực thể Employee cần chuyển đổi; có thể null
     * @return đối tượng EmployeeDto chứa dữ liệu đã được ánh xạ, hoặc null nếu đầu vào là null
     */
    public static EmployeeDto mapToEmployeeDto(Employee employee) {
        if (employee == null) return null;
        return EmployeeDto.builder()
                .employeeId(employee.getEmployeeId())
                .fullName(employee.getFullName())
                .position(employee.getPosition())
                .phone(employee.getPhone())
                .salaryBase(employee.getSalaryBase())
                .paidAmount(employee.getPaidAmount())
                .hireDate(employee.getHireDate())
                .status(employee.getStatus())
                .build();
    }

    /**
     * Chuyển đổi từ đối tượng EmployeeDto sang thực thể Employee tương ứng.
     * @param dto đối tượng EmployeeDto chứa dữ liệu cần chuyển đổi; có thể null
     * @return thực thể Employee được tạo từ EmployeeDto, hoặc null nếu đầu vào là null
     */
    public static Employee mapToEmployee(EmployeeDto dto) {
        if (dto == null) return null;
        return Employee.builder()
                .employeeId(dto.getEmployeeId())
                .fullName(dto.getFullName())
                .position(dto.getPosition())
                .phone(dto.getPhone())
                .salaryBase(dto.getSalaryBase())
                .paidAmount(dto.getPaidAmount())
                .hireDate(dto.getHireDate())
                .status(dto.getStatus())
                .build();
    }
}
