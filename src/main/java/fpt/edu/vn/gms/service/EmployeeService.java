package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDto> findAllEmployeeIsTechniciansActive();
}
