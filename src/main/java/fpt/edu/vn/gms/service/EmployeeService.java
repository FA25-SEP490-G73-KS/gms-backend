package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.response.EmployeeInfoResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDto> findAllEmployeeIsTechniciansActive();

    EmployeeInfoResponseDto findEmployeeInfoByPhone(String phone);

    Page<EmployeeDto> findAll(int page, int size);
}
