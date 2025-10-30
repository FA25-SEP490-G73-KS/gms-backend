package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public List<EmployeeDto> findAllEmployeeIsTechniciansActive() {

        return employeeRepository.findAllEmployeeIsTechniciansActive();
    }
}
