package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.DeductionType;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.request.DeductionRequestDto;
import fpt.edu.vn.gms.dto.response.DeductionDto;
import fpt.edu.vn.gms.entity.Deduction;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.repository.DeductionRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeductionServiceImplTest extends BaseServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private DeductionRepository deductionRepository;

  @InjectMocks
  private DeductionServiceImpl deductionServiceImpl;

  @Test
  void createDeduction_WhenEmployeeAndCreatorExist_ShouldSaveAndReturnDeductionDto() {
    Employee employee = getMockEmployee(Role.SERVICE_ADVISOR);
    Employee creator = getMockEmployee(Role.ACCOUNTANT);
    creator.setEmployeeId(2L);
    creator.setFullName("Creator Name");

    DeductionRequestDto dto = DeductionRequestDto.builder()
        .employeeId(employee.getEmployeeId())
        .type(DeductionType.PENALTY)
        .content("Đi trễ")
        .amount(BigDecimal.valueOf(100000))
        .build();

    when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
    when(employeeRepository.findById(creator.getEmployeeId())).thenReturn(Optional.of(creator));
    when(deductionRepository.save(any(Deduction.class))).thenAnswer(invocation -> invocation.getArgument(0));

    DeductionDto result = deductionServiceImpl.createDeduction(dto, creator);

    assertNotNull(result);
    assertEquals(DeductionType.PENALTY.getVietnamese(), result.getType());
    assertEquals(BigDecimal.valueOf(100000), result.getAmount());
    assertEquals(LocalDate.now(), result.getDate());
    assertEquals("Creator Name", result.getCreatedBy());
    verify(deductionRepository).save(any(Deduction.class));
  }

  @Test
  void createDeduction_WhenEmployeeNotFound_ShouldThrowRuntimeException() {
    Employee creator = getMockEmployee(Role.ACCOUNTANT);
    DeductionRequestDto dto = DeductionRequestDto.builder()
        .employeeId(99L)
        .type(DeductionType.PENALTY)
        .content("Đi trễ")
        .amount(BigDecimal.valueOf(50000))
        .build();

    when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> deductionServiceImpl.createDeduction(dto, creator));
    assertTrue(ex.getMessage().contains("Không tìm thấy nhân viên"));
    verify(deductionRepository, never()).save(any());
  }

  @Test
  void createDeduction_WhenCreatorNotFound_ShouldThrowRuntimeException() {
    Employee employee = getMockEmployee(Role.ACCOUNTANT);
    Employee creator = getMockEmployee(Role.ACCOUNTANT);
    creator.setEmployeeId(2L);

    DeductionRequestDto dto = DeductionRequestDto.builder()
        .employeeId(employee.getEmployeeId())
        .type(DeductionType.PENALTY)
        .content("Đi trễ")
        .amount(BigDecimal.valueOf(50000))
        .build();

    when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
    when(employeeRepository.findById(creator.getEmployeeId())).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> deductionServiceImpl.createDeduction(dto, creator));
    assertTrue(ex.getMessage().contains("Không tìm thấy người tạo"));
    verify(deductionRepository, never()).save(any());
  }
}
