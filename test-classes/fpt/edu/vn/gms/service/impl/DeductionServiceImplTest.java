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

    // TC043: Create deduction with DAMAGE type - Normal flow
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

    // TC050: Create deduction - Employee not found
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

    // TC051: Create deduction - Creator not found
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

    // TC041: Create deduction with PENALTY type
    @Test
    void createDeduction_ShouldCreateAndReturnDto_WhenPENALTYType() {
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
                .type(DeductionType.PENALTY)
                .content("Late 30 minutes")
                .amount(new BigDecimal("50000"))
                .build();

        Deduction savedDeduction = Deduction.builder()
                .id(100L)
                .employee(employee)
                .type(DeductionType.PENALTY)
                .reason("Late 30 minutes")
                .amount(new BigDecimal("50000"))
                .date(LocalDate.now())
                .createdBy("Creator")
                .build();
        when(deductionRepository.save(any(Deduction.class))).thenReturn(savedDeduction);

        DeductionDto result = service.createDeduction(dto, creator);

        assertNotNull(result);
        assertEquals("Phạt", result.getType());
        assertEquals(new BigDecimal("50000"), result.getAmount());
        verify(deductionRepository).save(any(Deduction.class));
    }

    // TC042: Create deduction with OTHER type
    @Test
    void createDeduction_ShouldCreateAndReturnDto_WhenOTHERType() {
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
                .type(DeductionType.OTHER)
                .content("Absent without leave")
                .amount(new BigDecimal("200000"))
                .build();

        Deduction savedDeduction = Deduction.builder()
                .id(100L)
                .employee(employee)
                .type(DeductionType.OTHER)
                .reason("Absent without leave")
                .amount(new BigDecimal("200000"))
                .date(LocalDate.now())
                .createdBy("Creator")
                .build();
        when(deductionRepository.save(any(Deduction.class))).thenReturn(savedDeduction);

        DeductionDto result = service.createDeduction(dto, creator);

        assertNotNull(result);
        assertEquals("Khác", result.getType());
        assertEquals(new BigDecimal("200000"), result.getAmount());
        verify(deductionRepository).save(any(Deduction.class));
    }

    // TC045: Create deduction with boundary amount (0)
    @Test
    void createDeduction_ShouldCreateAndReturnDto_WhenAmountIsZero() {
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
                .content("No damage cost")
                .amount(new BigDecimal("0"))
                .build();

        Deduction savedDeduction = Deduction.builder()
                .id(100L)
                .employee(employee)
                .type(DeductionType.DAMAGE)
                .reason("No damage cost")
                .amount(new BigDecimal("0"))
                .date(LocalDate.now())
                .createdBy("Creator")
                .build();
        when(deductionRepository.save(any(Deduction.class))).thenReturn(savedDeduction);

        DeductionDto result = service.createDeduction(dto, creator);

        assertNotNull(result);
        assertEquals(new BigDecimal("0"), result.getAmount());
        verify(deductionRepository).save(any(Deduction.class));
    }

    // TC047: Create deduction with maximum valid amount
    @Test
    void createDeduction_ShouldHandleMaximumValidAmount() {
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
                .content("Maximum deduction")
                .amount(new BigDecimal("999999999"))
                .build();

        Deduction savedDeduction = Deduction.builder()
                .id(100L)
                .employee(employee)
                .type(DeductionType.DAMAGE)
                .reason("Maximum deduction")
                .amount(new BigDecimal("999999999"))
                .date(LocalDate.now())
                .createdBy("Creator")
                .build();
        when(deductionRepository.save(any(Deduction.class))).thenReturn(savedDeduction);

        DeductionDto result = service.createDeduction(dto, creator);

        assertNotNull(result);
        assertEquals(new BigDecimal("999999999"), result.getAmount());
        verify(deductionRepository).save(any(Deduction.class));
    }

    // TC048: Create deduction with minimum valid amount
    @Test
    void createDeduction_ShouldHandleMinimumValidAmount() {
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
                .content("Minimum deduction")
                .amount(new BigDecimal("1"))
                .build();

        Deduction savedDeduction = Deduction.builder()
                .id(100L)
                .employee(employee)
                .type(DeductionType.DAMAGE)
                .reason("Minimum deduction")
                .amount(new BigDecimal("1"))
                .date(LocalDate.now())
                .createdBy("Creator")
                .build();
        when(deductionRepository.save(any(Deduction.class))).thenReturn(savedDeduction);

        DeductionDto result = service.createDeduction(dto, creator);

        assertNotNull(result);
        assertEquals(new BigDecimal("1"), result.getAmount());
        verify(deductionRepository).save(any(Deduction.class));
    }

    // TC049: Create deduction with long content
    @Test
    void createDeduction_ShouldHandleLongContent() {
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

        String longContent = "A".repeat(500); // Long content
        DeductionRequestDto dto = DeductionRequestDto.builder()
                .employeeId(1L)
                .type(DeductionType.DAMAGE)
                .content(longContent)
                .amount(new BigDecimal("500000"))
                .build();

        Deduction savedDeduction = Deduction.builder()
                .id(100L)
                .employee(employee)
                .type(DeductionType.DAMAGE)
                .reason(longContent)
                .amount(new BigDecimal("500000"))
                .date(LocalDate.now())
                .createdBy("Creator")
                .build();
        when(deductionRepository.save(any(Deduction.class))).thenReturn(savedDeduction);

        DeductionDto result = service.createDeduction(dto, creator);

        assertNotNull(result);
        assertEquals(DeductionType.DAMAGE.getVietnamese(), result.getType());
        assertEquals(new BigDecimal("500000"), result.getAmount());
        // Note: DeductionDto does not have content field, content is stored in entity.reason
        verify(deductionRepository).save(any(Deduction.class));
    }
}

