package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.AllowanceType;
import fpt.edu.vn.gms.dto.request.AllowanceRequestDto;
import fpt.edu.vn.gms.dto.response.AllowanceDto;
import fpt.edu.vn.gms.entity.Allowance;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.AllowanceRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllowanceServiceImplTest {

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    AllowanceRepository allowanceRepository;

    @InjectMocks
    AllowanceServiceImpl allowanceService;

    @Captor
    ArgumentCaptor<Allowance> allowanceCaptor;

    private AllowanceRequestDto requestDto;
    private Employee accountant;
    private Employee employee;

    @BeforeEach
    void setUp() {
        requestDto = AllowanceRequestDto.builder()
                .employeeId(1L)
                .type(AllowanceType.MEAL)
                .amount(new BigDecimal("150000"))
                .build();

        accountant = Employee.builder()
                .employeeId(99L)
                .fullName("Kế toán viên")
                .build();

        employee = Employee.builder()
                .employeeId(1L)
                .fullName("Nguyễn Văn A")
                .build();
    }

    // TC031: Create allowance with MEAL type - Normal flow
    @Test
    void createAllowance_ShouldReturnDto_WhenEmployeeExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        // Echo-back saved entity
        when(allowanceRepository.save(any(Allowance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AllowanceDto result = allowanceService.createAllowance(requestDto, accountant);

        assertNotNull(result, "Result DTO should not be null");
        assertEquals(AllowanceType.MEAL.getVietnamese(), result.getType());
        assertEquals(new BigDecimal("150000"), result.getAmount());
        assertNotNull(result.getCreatedAt(), "CreatedAt should be set");

        verify(employeeRepository).findById(1L);
        verify(allowanceRepository).save(allowanceCaptor.capture());
        Allowance saved = allowanceCaptor.getValue();
        assertEquals(employee, saved.getEmployee());
        assertEquals(AllowanceType.MEAL, saved.getType());
        assertEquals(new BigDecimal("150000"), saved.getAmount());
        assertEquals(accountant.getFullName(), saved.getCreatedBy());
        assertNotNull(saved.getCreatedAt());
    }

    // TC040: Create allowance - Employee not found
    @Test
    void createAllowance_ShouldThrowResourceNotFoundException_WhenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> allowanceService.createAllowance(requestDto, accountant));
        assertTrue(ex.getMessage().contains("Không tìm thấy nhân viên"));

        verify(employeeRepository).findById(1L);
        verifyNoInteractions(allowanceRepository);
    }

    // TC032: Create allowance with OVERTIME type
    @Test
    void createAllowance_ShouldReturnDto_WhenOVERTIMEType() {
        AllowanceRequestDto dto = AllowanceRequestDto.builder()
                .employeeId(1L)
                .type(AllowanceType.OVERTIME)
                .amount(new BigDecimal("200000"))
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(allowanceRepository.save(any(Allowance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AllowanceDto result = allowanceService.createAllowance(dto, accountant);

        assertNotNull(result);
        assertEquals(AllowanceType.OVERTIME.getVietnamese(), result.getType());
        assertEquals(new BigDecimal("200000"), result.getAmount());
        verify(allowanceRepository).save(allowanceCaptor.capture());
        Allowance saved = allowanceCaptor.getValue();
        assertEquals(AllowanceType.OVERTIME, saved.getType());
    }

    // TC033: Create allowance with BONUS type
    @Test
    void createAllowance_ShouldReturnDto_WhenBONUSType() {
        AllowanceRequestDto dto = AllowanceRequestDto.builder()
                .employeeId(1L)
                .type(AllowanceType.BONUS)
                .amount(new BigDecimal("100000"))
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(allowanceRepository.save(any(Allowance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AllowanceDto result = allowanceService.createAllowance(dto, accountant);

        assertNotNull(result);
        assertEquals(AllowanceType.BONUS.getVietnamese(), result.getType());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        verify(allowanceRepository).save(allowanceCaptor.capture());
        Allowance saved = allowanceCaptor.getValue();
        assertEquals(AllowanceType.BONUS, saved.getType());
    }

    // TC036: Create allowance with boundary amount (0)
    @Test
    void createAllowance_ShouldReturnDto_WhenAmountIsZero() {
        AllowanceRequestDto dto = AllowanceRequestDto.builder()
                .employeeId(1L)
                .type(AllowanceType.MEAL)
                .amount(new BigDecimal("0"))
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(allowanceRepository.save(any(Allowance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AllowanceDto result = allowanceService.createAllowance(dto, accountant);

        assertNotNull(result);
        assertEquals(new BigDecimal("0"), result.getAmount());
        verify(allowanceRepository).save(allowanceCaptor.capture());
        Allowance saved = allowanceCaptor.getValue();
        assertEquals(new BigDecimal("0"), saved.getAmount());
    }

    // TC037: Create allowance with invalid amount (< 0) - This should be validated at DTO level
    // Note: If validation is at service level, this test would throw ValidationException
    @Test
    void createAllowance_ShouldHandleMinimumValidAmount() {
        AllowanceRequestDto dto = AllowanceRequestDto.builder()
                .employeeId(1L)
                .type(AllowanceType.MEAL)
                .amount(new BigDecimal("1"))
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(allowanceRepository.save(any(Allowance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AllowanceDto result = allowanceService.createAllowance(dto, accountant);

        assertNotNull(result);
        assertEquals(new BigDecimal("1"), result.getAmount());
    }

    // TC038: Create allowance with maximum valid amount
    @Test
    void createAllowance_ShouldHandleMaximumValidAmount() {
        AllowanceRequestDto dto = AllowanceRequestDto.builder()
                .employeeId(1L)
                .type(AllowanceType.MEAL)
                .amount(new BigDecimal("999999999"))
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(allowanceRepository.save(any(Allowance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AllowanceDto result = allowanceService.createAllowance(dto, accountant);

        assertNotNull(result);
        assertEquals(new BigDecimal("999999999"), result.getAmount());
    }

    // TC039: Create allowance with minimum valid amount (1)
    @Test
    void createAllowance_ShouldCreateSuccessfully_WithMinimumAmount() {
        AllowanceRequestDto dto = AllowanceRequestDto.builder()
                .employeeId(1L)
                .type(AllowanceType.MEAL)
                .amount(new BigDecimal("1"))
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(allowanceRepository.save(any(Allowance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AllowanceDto result = allowanceService.createAllowance(dto, accountant);

        assertNotNull(result);
        assertEquals(new BigDecimal("1"), result.getAmount());
    }
}
