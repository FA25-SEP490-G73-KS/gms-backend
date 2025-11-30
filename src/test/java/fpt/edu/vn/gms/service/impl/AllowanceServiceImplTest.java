package fpt.edu.vn.gms.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.AllowanceType;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.request.AllowanceRequestDto;
import fpt.edu.vn.gms.dto.response.AllowanceDto;
import fpt.edu.vn.gms.entity.Allowance;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.AllowanceRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;

public class AllowanceServiceImplTest extends BaseServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private AllowanceRepository allowanceRepository;

  @InjectMocks
  private AllowanceServiceImpl allowanceServiceImpl;

  @Test
  void createAllowance_WhenEmployeeExists_ShouldSaveAndReturnAllowanceDto() {
    // Arrange
    Employee employee = getMockEmployee(Role.SERVICE_ADVISOR);
    Employee accountant = getMockEmployee(Role.ACCOUNTANT);

    AllowanceRequestDto requestDto = AllowanceRequestDto.builder()
        .employeeId(employee.getEmployeeId())
        .type(AllowanceType.OVERTIME)
        .amount(BigDecimal.valueOf(500000))
        .build();

    when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));

    // Mock AllowanceType.getVietnamese()
    AllowanceType type = AllowanceType.OVERTIME;

    // Act
    AllowanceDto result = allowanceServiceImpl.createAllowance(requestDto, accountant);

    // Assert
    verify(employeeRepository).findById(employee.getEmployeeId());
    verify(allowanceRepository).save(any(Allowance.class));
    assertNotNull(result);
    assertEquals(type.getVietnamese(), result.getType());
    assertEquals(requestDto.getAmount(), result.getAmount());
    assertNotNull(result.getCreatedAt());
  }

  @Test
  void createAllowance_WhenEmployeeNotFound_ShouldThrowResourceNotFoundException() {
    // Arrange
    AllowanceRequestDto requestDto = AllowanceRequestDto.builder()
        .employeeId(999L)
        .type(AllowanceType.OVERTIME)
        .amount(BigDecimal.valueOf(500000))
        .build();

    Employee accountant = getMockEmployee(Role.ACCOUNTANT);

    when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
        () -> allowanceServiceImpl.createAllowance(requestDto, accountant));
    assertTrue(ex.getMessage().contains("Không tìm thấy nhân viên"));
    verify(employeeRepository).findById(999L);
    verify(allowanceRepository, never()).save(any());
  }

  @Test
  void createAllowance_WhenCalled_ShouldSetCreatedByAndCreatedAtCorrectly() {
    // Arrange
    Employee employee = getMockEmployee(Role.SERVICE_ADVISOR);
    Employee accountant = getMockEmployee(Role.ACCOUNTANT);

    AllowanceRequestDto requestDto = AllowanceRequestDto.builder()
        .employeeId(employee.getEmployeeId())
        .type(AllowanceType.OVERTIME)
        .amount(BigDecimal.valueOf(100000))
        .build();

    when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));

    ArgumentCaptor<Allowance> allowanceCaptor = ArgumentCaptor.forClass(Allowance.class);

    // Act
    allowanceServiceImpl.createAllowance(requestDto, accountant);

    // Assert
    verify(allowanceRepository).save(allowanceCaptor.capture());
    Allowance savedAllowance = allowanceCaptor.getValue();
    assertEquals(accountant.getFullName(), savedAllowance.getCreatedBy());
    assertNotNull(savedAllowance.getCreatedAt());
    assertEquals(employee, savedAllowance.getEmployee());
    assertEquals(requestDto.getType(), savedAllowance.getType());
    assertEquals(requestDto.getAmount(), savedAllowance.getAmount());
  }
}
