package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.response.EmployeeInfoResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.mapper.EmployeeMapper;
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
    void findAll_ShouldUsePagingAndMapper() {
        Employee emp = Employee.builder()
                .employeeId(1L)
                .fullName("Emp 1")
                .phone("0909")
                .build();
        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Employee> page = new PageImpl<>(List.of(emp), pageable, 1);

        when(employeeRepository.findAll(pageable)).thenReturn(page);
        EmployeeDto dto = new EmployeeDto(1L, "Emp 1", "0909");
        when(employeeMapper.toDto(emp)).thenReturn(dto);

        Page<EmployeeDto> result = service.findAll(0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));
        verify(employeeRepository).findAll(pageable);
        verify(employeeMapper).toDto(emp);
    }
}


