package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.DeductionRequestDto;
import fpt.edu.vn.gms.dto.response.DeductionDto;
import fpt.edu.vn.gms.entity.Deduction;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.repository.DeductionRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.service.DeductionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeductionServiceImpl implements DeductionService {

    EmployeeRepository employeeRepository;
    DeductionRepository deductionRepository;

    @Override
    public DeductionDto createDeduction(DeductionRequestDto dto, Employee createdBy) {

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        Employee creator = employeeRepository.findById(createdBy.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người tạo"));

        Deduction deduction = Deduction.builder()
                .employee(employee)
                .type(dto.getType())
                .reason(dto.getContent())
                .amount(dto.getAmount())
                .date(LocalDate.now())
                .createdBy(createdBy.getFullName())
                .build();

        deductionRepository.save(deduction);

        return DeductionDto.builder()
                .type(deduction.getType().getVietnamese())
                .amount(deduction.getAmount())
                .date(deduction.getDate())
                .createdBy(creator.getFullName())
                .build();
    }
}
