package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.DeductionRequestDto;
import fpt.edu.vn.gms.dto.request.DeductionDto;
import fpt.edu.vn.gms.entity.Employee;

public interface DeductionService {

    DeductionDto createDeduction(DeductionRequestDto dto, Employee employee);
}
