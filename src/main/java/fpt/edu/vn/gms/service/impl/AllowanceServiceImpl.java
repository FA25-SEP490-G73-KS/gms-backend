package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.AllowanceRequestDto;
import fpt.edu.vn.gms.dto.response.AllowanceDto;
import fpt.edu.vn.gms.entity.Allowance;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.AllowanceRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.service.AllowanceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AllowanceServiceImpl implements AllowanceService {

        EmployeeRepository employeeRepository;
        AllowanceRepository allowanceRepository;

        @Override
        public AllowanceDto createAllowance(AllowanceRequestDto dto, Employee accountance) {

                Employee employee = employeeRepository.findById(dto.getEmployeeId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

                Allowance allowance = Allowance.builder()
                                .employee(employee)
                                .type(dto.getType())
                                .amount(dto.getAmount())
                                .createdAt(LocalDateTime.now())
                                .month(dto.getMonth())
                                .year(dto.getYear())
                                .createdBy(accountance.getFullName())
                                .build();

                allowanceRepository.save(allowance);

                return AllowanceDto
                                .builder()
                                .type(allowance.getType().getVietnamese())
                                .amount(allowance.getAmount())
                                .createdAt(allowance.getCreatedAt())
                                .build();
        }

        @Override
        public void deleteAllowance(Long id) {
                Allowance allowance = allowanceRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phụ cấp"));
                allowanceRepository.delete(allowance);
        }

}
