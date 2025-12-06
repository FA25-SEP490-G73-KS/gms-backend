package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.DeductionType;
import fpt.edu.vn.gms.dto.request.DeductionRequestDto;
import fpt.edu.vn.gms.dto.request.DeductionDto;
import fpt.edu.vn.gms.entity.Deduction;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.repository.DeductionRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeductionServiceImplTest {

    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    DeductionRepository deductionRepository;

    @InjectMocks
    DeductionServiceImpl service;

    @Test
    void createDeduction_ShouldCreateAndReturnDto() {
        Employee employee = Employee.builder()
                .employeeId(1L)
                .fullName("Employee")
                .build();
        Employee creator = Employee.builder()
                .employeeId(2L)
                .fullName("Creator")
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(creator));

        DeductionRequestDto dto = DeductionRequestDto.builder()
                .employeeId(1L)
                .type(DeductionType.DAMAGE)
                .content("Hỏng do lỗi")
                .amount(new BigDecimal("500000"))
                .build();

        Deduction savedDeduction = Deduction.builder()
                .id(100L)
                .employee(employee)
                .type(DeductionType.DAMAGE)
                .reason("Hỏng do lỗi")
                .amount(new BigDecimal("500000"))
                .date(LocalDate.now())
                .createdBy("Creator")
                .build();
        when(deductionRepository.save(any(Deduction.class))).thenReturn(savedDeduction);

        DeductionDto result = service.createDeduction(dto, creator);

        assertNotNull(result);
        assertEquals("Hư hỏng", result.getType());
        assertEquals(new BigDecimal("500000"), result.getAmount());
        assertEquals(LocalDate.now(), result.getDate());
        assertEquals("Creator", result.getCreatedBy());

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).findById(2L);
        verify(deductionRepository).save(any(Deduction.class));
    }

    @Test
    void createDeduction_ShouldThrow_WhenEmployeeNotFound() {
        Employee creator = Employee.builder()
                .employeeId(2L)
                .fullName("Creator")
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        DeductionRequestDto dto = DeductionRequestDto.builder()
                .employeeId(1L)
                .type(DeductionType.DAMAGE)
                .content("Hỏng do lỗi")
                .amount(new BigDecimal("500000"))
                .build();

        assertThrows(RuntimeException.class,
                () -> service.createDeduction(dto, creator));
        verify(employeeRepository).findById(1L);
        verify(employeeRepository, never()).findById(2L);
        verify(deductionRepository, never()).save(any());
    }

    @Test
    void createDeduction_ShouldThrow_WhenCreatorNotFound() {
        Employee employee = Employee.builder()
                .employeeId(1L)
                .fullName("Employee")
                .build();
        Employee creator = Employee.builder()
                .employeeId(2L)
                .fullName("Creator")
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

        DeductionRequestDto dto = DeductionRequestDto.builder()
                .employeeId(1L)
                .type(DeductionType.DAMAGE)
                .content("Hỏng do lỗi")
                .amount(new BigDecimal("500000"))
                .build();

        assertThrows(RuntimeException.class,
                () -> service.createDeduction(dto, creator));
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).findById(2L);
        verify(deductionRepository, never()).save(any());
    }
}

