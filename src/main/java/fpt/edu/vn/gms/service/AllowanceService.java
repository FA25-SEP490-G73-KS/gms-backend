package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.AllowanceRequestDto;
import fpt.edu.vn.gms.dto.response.AllowanceDto;
import fpt.edu.vn.gms.entity.Employee;

public interface AllowanceService {

    AllowanceDto createAllowance(AllowanceRequestDto dto, Employee accountance);

    void deleteAllowance(Long id);
}
