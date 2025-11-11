package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.response.EmployeeInfoResponseDto;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDto> findAllEmployeeIsTechniciansActive();

    EmployeeInfoResponseDto findEmployeeInfoByPhone(String phone);
}
