package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.AttendanceRequestDTO;
import fpt.edu.vn.gms.dto.response.AttendanceResponseDTO;
import fpt.edu.vn.gms.dto.response.AttendanceSummaryDTO;
import fpt.edu.vn.gms.entity.Attendance;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.exception.EmployeeNotFoundException;
import fpt.edu.vn.gms.repository.AttendanceRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    AttendanceRepository attendanceRepository;
    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    AttendanceServiceImpl service;

    @Test
    void markMissingAttendances_ShouldCreateAbsencesForEmployeesWithoutAttendanceToday() {
        LocalDate today = LocalDate.now();

        Employee e1 = Employee.builder().employeeId(1L).fullName("Emp 1").build();
        Employee e2 = Employee.builder().employeeId(2L).fullName("Emp 2").build();

        when(employeeRepository.findAll()).thenReturn(List.of(e1, e2));
        // e2 đã có attendance, chỉ e1 bị vắng
        when(employeeRepository.findEmployeesWithAttendances(today))
                .thenReturn(Set.of(e2));

        service.markMissingAttendances();

        verify(employeeRepository).findAll();
        verify(employeeRepository).findEmployeesWithAttendances(today);
        verify(attendanceRepository).saveAll(argThat(iterable -> {
            @SuppressWarnings("unchecked")
            java.util.List<Attendance> list = (java.util.List<Attendance>) iterable;
            assertEquals(1, list.size());
            Attendance a = list.get(0);
            assertEquals(e1, a.getEmployee());
            assertEquals(Boolean.FALSE, a.getIsPresent());
            assertEquals(today, a.getDate());
            assertNotNull(a.getRecordedAt());
            return true;
        }));
    }

    @Test
    void markAttendances_ShouldThrow_WhenDateBeforeToday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<AttendanceRequestDTO> requests = List.of(
                AttendanceRequestDTO.builder().employeeId(1L).isPresent(true).build()
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.markAttendances(yesterday, requests, 100L));
        verifyNoInteractions(attendanceRepository);
    }

    @Test
    void markAttendances_ShouldThrow_WhenDateAfterToday() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<AttendanceRequestDTO> requests = List.of(
                AttendanceRequestDTO.builder().employeeId(1L).isPresent(true).build()
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.markAttendances(tomorrow, requests, 100L));
        verifyNoInteractions(attendanceRepository);
    }

    @Test
    void markAttendances_ShouldUpsertAttendancesForToday() {
        LocalDate today = LocalDate.now();
        Long managerId = 999L;

        AttendanceRequestDTO r1 = AttendanceRequestDTO.builder()
                .employeeId(1L).isPresent(true).note("On time").build();
        // duplicate id -> should be overridden by last
        AttendanceRequestDTO r1b = AttendanceRequestDTO.builder()
                .employeeId(1L).isPresent(false).note("Late").build();
        AttendanceRequestDTO r2 = AttendanceRequestDTO.builder()
                .employeeId(2L).isPresent(true).note("OK").build();

        List<AttendanceRequestDTO> requests = List.of(r1, r1b, r2);

        Employee e1 = Employee.builder().employeeId(1L).fullName("Emp 1").build();
        Employee e2 = Employee.builder().employeeId(2L).fullName("Emp 2").build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(e1));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(e2));

        // existing attendance for e1, none for e2
        Attendance existing = Attendance.builder()
                .attendanceId(10L)
                .employee(e1)
                .date(today)
                .isPresent(true)
                .note("Old")
                .build();
        when(attendanceRepository.findByEmployeeAndDate(e1, today))
                .thenReturn(Optional.of(existing));
        when(attendanceRepository.findByEmployeeAndDate(e2, today))
                .thenReturn(Optional.empty());

        service.markAttendances(today, requests, managerId);

        verify(attendanceRepository).saveAll(argThat(iterable -> {
            @SuppressWarnings("unchecked")
            java.util.List<Attendance> list = (java.util.List<Attendance>) iterable;
            assertEquals(2, list.size());

            Attendance a1 = list.stream()
                    .filter(a -> a.getEmployee().getEmployeeId().equals(1L))
                    .findFirst()
                    .orElseThrow();
            Attendance a2 = list.stream()
                    .filter(a -> a.getEmployee().getEmployeeId().equals(2L))
                    .findFirst()
                    .orElseThrow();

            // e1 should use last request (r1b)
            assertEquals(Boolean.FALSE, a1.getIsPresent());
            assertEquals("Late", a1.getNote());
            assertEquals(today, a1.getDate());
            assertEquals(managerId, a1.getRecordedBy());
            assertNotNull(a1.getRecordedAt());

            // e2 from r2
            assertEquals(Boolean.TRUE, a2.getIsPresent());
            assertEquals("OK", a2.getNote());
            assertEquals(today, a2.getDate());
            assertEquals(managerId, a2.getRecordedBy());
            assertNotNull(a2.getRecordedAt());

            return true;
        }));
    }

    @Test
    void markAttendances_ShouldThrowEmployeeNotFound_WhenEmployeeMissing() {
        LocalDate today = LocalDate.now();
        AttendanceRequestDTO r1 = AttendanceRequestDTO.builder()
                .employeeId(1L).isPresent(true).build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> service.markAttendances(today, List.of(r1), 1L));
    }

    @Test
    void getAttendancesByDate_ShouldDelegateToRepository() {
        LocalDate date = LocalDate.now();
        List<AttendanceResponseDTO> list = List.of(
                AttendanceResponseDTO.builder().employeeId(1L).employeeName("Emp 1").build()
        );

        when(attendanceRepository.findAllByDate(date)).thenReturn(list);

        List<AttendanceResponseDTO> result = service.getAttendancesByDate(date);

        assertSame(list, result);
        verify(attendanceRepository).findAllByDate(date);
    }

    @Test
    void getAttendanceSummary_ShouldGroupByEmployee() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();

        Employee e1 = Employee.builder().employeeId(1L).fullName("Emp 1").build();
        Employee e2 = Employee.builder().employeeId(2L).fullName("Emp 2").build();

        Attendance a1 = Attendance.builder()
                .employee(e1)
                .date(start)
                .isPresent(true)
                .note("OK")
                .build();
        Attendance a2 = Attendance.builder()
                .employee(e1)
                .date(end)
                .isPresent(false)
                .note("Off")
                .build();
        Attendance a3 = Attendance.builder()
                .employee(e2)
                .date(end)
                .isPresent(true)
                .note("OK")
                .build();

        when(attendanceRepository.findByDateBetween(start, end))
                .thenReturn(List.of(a1, a2, a3));

        List<AttendanceSummaryDTO> result = service.getAttendanceSummary(start, end);

        assertEquals(2, result.size());

        AttendanceSummaryDTO e1Summary = result.stream()
                .filter(r -> r.getEmployeeId().equals(1L))
                .findFirst()
                .orElseThrow();
        assertEquals("Emp 1", e1Summary.getEmployeeName());
        assertEquals(2, e1Summary.getAttendanceData().size());

        AttendanceSummaryDTO e2Summary = result.stream()
                .filter(r -> r.getEmployeeId().equals(2L))
                .findFirst()
                .orElseThrow();
        assertEquals("Emp 2", e2Summary.getEmployeeName());
        assertEquals(1, e2Summary.getAttendanceData().size());

        verify(attendanceRepository).findByDateBetween(start, end);
    }
}


