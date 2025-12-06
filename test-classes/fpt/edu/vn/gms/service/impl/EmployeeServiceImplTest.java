package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.response.EmployeeInfoResponseDto;
import fpt.edu.vn.gms.dto.response.EmployeeListResponse;
import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.entity.Attendance;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.mapper.EmployeeMapper;
import fpt.edu.vn.gms.repository.AttendanceRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    EmployeeMapper employeeMapper;
    @Mock
    AttendanceRepository attendanceRepository;

    @InjectMocks
    EmployeeServiceImpl service;

    @Test
    void findAllEmployeeIsTechniciansActive_ShouldDelegateToRepository() {
        List<EmployeeDto> list = List.of(new EmployeeDto(1L, "Tech 1", "0909"));
        when(employeeRepository.findAllEmployeeIsTechniciansActive()).thenReturn(list);

        List<EmployeeDto> result = service.findAllEmployeeIsTechniciansActive();

        assertSame(list, result);
        verify(employeeRepository).findAllEmployeeIsTechniciansActive();
    }

    @Test
    void findEmployeeInfoByPhone_ShouldDelegateToRepository() {
        EmployeeInfoResponseDto dto = EmployeeInfoResponseDto.builder()
                .id(1L)
                .fullName("Nguyen Van A")
                .phone("0909")
                .role("TECHNICIAN")
                .build();
        when(employeeRepository.findEmployeeInfoByPhone("0909")).thenReturn(dto);

        EmployeeInfoResponseDto result = service.findEmployeeInfoByPhone("0909");

        assertSame(dto, result);
        verify(employeeRepository).findEmployeeInfoByPhone("0909");
    }

    @Test
    void findAll_ShouldUsePagingAndComputeStatus() {
        Employee emp = Employee.builder()
                .employeeId(1L)
                .fullName("Emp 1")
                .phone("0909")
                .dailySalary(java.math.BigDecimal.valueOf(500000))
                .hireDate(java.time.LocalDateTime.now())
                .account(Account.builder()
                        .role(Role.SERVICE_ADVISOR)
                        .build())
                .build();
        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Employee> page = new PageImpl<>(List.of(emp), pageable, 1);

        when(employeeRepository.findAll(pageable)).thenReturn(page);
        when(attendanceRepository.findTodayAttendance(any(java.time.LocalDate.class), any(List.class)))
                .thenReturn(List.of());

        Page<EmployeeListResponse> result = service.findAll(0, 5, null);

        assertEquals(1, result.getTotalElements());
        EmployeeListResponse response = result.getContent().get(0);
        assertEquals(1L, response.getEmployeeId());
        assertEquals("Emp 1", response.getFullName());
        assertEquals("Nghỉ làm", response.getStatus()); // No attendance = "Nghỉ làm"
        verify(employeeRepository).findAll(pageable);
        verify(attendanceRepository).findTodayAttendance(any(java.time.LocalDate.class), any(List.class));
    }
}


