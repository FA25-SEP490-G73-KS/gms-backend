package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.request.EmployeeCreateRequest;
import fpt.edu.vn.gms.dto.request.EmployeeUpdateRequest;
import fpt.edu.vn.gms.dto.response.EmployeeDetailResponse;
import fpt.edu.vn.gms.dto.response.EmployeeInfoResponseDto;
import fpt.edu.vn.gms.dto.response.EmployeeListResponse;
import fpt.edu.vn.gms.dto.response.EmployeeResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeService {

    List<EmployeeDto> findAllEmployeeIsTechniciansActive();

    EmployeeInfoResponseDto findEmployeeInfoByPhone(String phone);

    Page<EmployeeListResponse> findAll(int page, int size, String statusFilter);

    EmployeeResponse createEmployee(EmployeeCreateRequest request);

    EmployeeDetailResponse getEmployeeDetail(Long id);

    EmployeeDetailResponse updateEmployee(Long id, EmployeeUpdateRequest request);

    void updateEmployeeActiveStatus(Long id, boolean isActive);
}