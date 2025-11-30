package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.response.EmployeeInfoResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.mapper.EmployeeMapper;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceImplTest extends BaseServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private EmployeeMapper employeeMapper;

  @InjectMocks
  private EmployeeServiceImpl employeeServiceImpl;

  @Test
  void findAllEmployeeIsTechniciansActive_WhenCalled_ShouldReturnTechnicianDtos() {
    EmployeeDto dto = new EmployeeDto(1L, "Tech", "0123456789");

    when(employeeRepository.findAllEmployeeIsTechniciansActive()).thenReturn(List.of(dto));

    List<EmployeeDto> result = employeeServiceImpl.findAllEmployeeIsTechniciansActive();

    assertEquals(1, result.size());
    assertEquals("Tech", result.get(0).fullName());
  }

  @Test
  void findEmployeeInfoByPhone_WhenPhoneExists_ShouldReturnEmployeeInfoResponseDto() {
    String phone = "0123456789";
    EmployeeInfoResponseDto dto = EmployeeInfoResponseDto.builder().id(1L).phone(phone).build();
    when(employeeRepository.findEmployeeInfoByPhone(phone)).thenReturn(dto);

    EmployeeInfoResponseDto result = employeeServiceImpl.findEmployeeInfoByPhone(phone);

    assertNotNull(result);
    assertEquals(phone, result.getPhone());
  }

  @Test
  void findAll_WhenCalled_ShouldReturnPagedEmployeeDtos() {
    Employee employee = getMockEmployee(Role.SERVICE_ADVISOR);
    EmployeeDto dto = new EmployeeDto(employee.getEmployeeId(), employee.getFullName(), employee.getPhone());
    Page<Employee> page = new PageImpl<>(List.of(employee));
    when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(employeeMapper.toDto(employee)).thenReturn(dto);

    Page<EmployeeDto> result = employeeServiceImpl.findAll(0, 10);

    assertEquals(1, result.getTotalElements());
    assertEquals(employee.getEmployeeId(), result.getContent().get(0).employeeId());
  }
}
