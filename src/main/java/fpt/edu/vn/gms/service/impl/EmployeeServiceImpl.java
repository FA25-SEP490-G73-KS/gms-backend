package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.response.EmployeeInfoResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.mapper.EmployeeMapper;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.service.EmployeeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeServiceImpl implements EmployeeService {

    EmployeeRepository employeeRepository;
    EmployeeMapper employeeMapper;

    @Override
    public List<EmployeeDto> findAllEmployeeIsTechniciansActive() {

        return employeeRepository.findAllEmployeeIsTechniciansActive();
    }

    @Override
    public EmployeeInfoResponseDto findEmployeeInfoByPhone(String phone) {

        return employeeRepository.findEmployeeInfoByPhone(phone);
    }

    @Override
    public Page<EmployeeDto> findAll(int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return employeeRepository.findAll(pageable).map(employeeMapper::toDto);
    }
}
