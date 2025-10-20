package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.mapper.EmployeeMapper;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    /**
     * Lây danh sách nhân viên với phân trang
     * @param pageable
     * @return
     */
    @Override
    public Page<EmployeeDto> getAllEmployee(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(EmployeeMapper::mapToEmployeeDto);
    }
    /**
     * Lấy danh sách kỹ thuật viên đang hoạt động
     * @return
     */
    @Override
    public List<Employee> findAllEmployeeIsTechniciansActive(){
        return employeeRepository.findAllEmployeeIsTechniciansActive();
    }

    @Override
    public EmployeeDto getEmployeeByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new RuntimeException("Employee not found with id: " + employeeId));
        return EmployeeMapper.mapToEmployeeDto(employee);
    }

}
