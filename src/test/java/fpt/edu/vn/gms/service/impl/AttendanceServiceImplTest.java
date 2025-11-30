package fpt.edu.vn.gms.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.request.AttendanceRequestDTO;
import fpt.edu.vn.gms.dto.response.AttendanceResponseDTO;
import fpt.edu.vn.gms.dto.response.AttendanceSummaryDTO;
import fpt.edu.vn.gms.entity.Attendance;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.exception.EmployeeNotFoundException;
import fpt.edu.vn.gms.repository.AttendanceRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class AttendanceServiceImplTest extends BaseServiceTest {

  @Mock
  private AttendanceRepository attendanceRepository;

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private AttendanceServiceImpl attendanceServiceImpl;

  @Test
  void markMissingAttendances_WhenSomeEmployeesMissing_ShouldSaveAbsentAttendances() {
    Employee emp1 = getMockEmployee(Role.SERVICE_ADVISOR);
    Employee emp2 = getMockEmployee(Role.ACCOUNTANT);
    emp2.setEmployeeId(2L);

    Set<Employee> allEmployees = new HashSet<>(Arrays.asList(emp1, emp2));
    Set<Employee> employeesWithAttendance = new HashSet<>(Collections.singletonList(emp1));

    when(employeeRepository.findAll()).thenReturn(new ArrayList<>(allEmployees));
    when(employeeRepository.findEmployeesWithAttendances(any(LocalDate.class))).thenReturn(employeesWithAttendance);

    when(attendanceRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    attendanceServiceImpl.markMissingAttendances();

    ArgumentCaptor<List<Attendance>> captor = ArgumentCaptor.forClass(List.class);
    verify(attendanceRepository).saveAll(captor.capture());
    List<Attendance> saved = captor.getValue();
    assertEquals(1, saved.size());
    assertEquals(emp2.getEmployeeId(), saved.get(0).getEmployee().getEmployeeId());
    assertFalse(saved.get(0).getIsPresent());
  }

  @Test
  void markAttendances_WhenDateIsBeforeToday_ShouldThrowException() {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    List<AttendanceRequestDTO> requests = new ArrayList<>();
    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> attendanceServiceImpl.markAttendances(yesterday, requests, 1L));
    assertTrue(ex.getMessage().contains("Không được chỉnh sửa điểm danh của ngày hôm trước"));
  }

  @Test
  void markAttendances_WhenDateIsAfterToday_ShouldThrowException() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    List<AttendanceRequestDTO> requests = new ArrayList<>();
    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> attendanceServiceImpl.markAttendances(tomorrow, requests, 1L));
    assertTrue(ex.getMessage().contains("Không được chỉnh sửa điểm danh của ngày hôm sau"));
  }

  @Test
  void markAttendances_WhenValidRequests_ShouldSaveAttendances() {
    Employee emp = getMockEmployee(Role.SERVICE_ADVISOR);
    AttendanceRequestDTO req = AttendanceRequestDTO.builder()
        .employeeId(emp.getEmployeeId())
        .isPresent(true)
        .note("Có mặt")
        .build();
    List<AttendanceRequestDTO> requests = List.of(req);

    when(employeeRepository.findById(emp.getEmployeeId())).thenReturn(Optional.of(emp));
    when(attendanceRepository.findByEmployeeAndDate(eq(emp), any(LocalDate.class))).thenReturn(Optional.empty());
    when(attendanceRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    attendanceServiceImpl.markAttendances(LocalDate.now(), requests, 10L);

    ArgumentCaptor<List<Attendance>> captor = ArgumentCaptor.forClass(List.class);
    verify(attendanceRepository).saveAll(captor.capture());
    List<Attendance> saved = captor.getValue();
    assertEquals(1, saved.size());
    assertEquals(emp.getEmployeeId(), saved.get(0).getEmployee().getEmployeeId());
    assertTrue(saved.get(0).getIsPresent());
    assertEquals("Có mặt", saved.get(0).getNote());
    assertEquals(10L, saved.get(0).getRecordedBy());
  }

  @Test
  void markAttendances_WhenEmployeeNotFound_ShouldThrowEmployeeNotFoundException() {
    AttendanceRequestDTO req = AttendanceRequestDTO.builder()
        .employeeId(999L)
        .isPresent(false)
        .note("Vắng")
        .build();
    List<AttendanceRequestDTO> requests = List.of(req);

    when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(EmployeeNotFoundException.class,
        () -> attendanceServiceImpl.markAttendances(LocalDate.now(), requests, 1L));
  }

  @Test
  void getAttendancesByDate_WhenCalled_ShouldReturnAttendanceResponseDTOList() {
    LocalDate date = LocalDate.now();
    AttendanceResponseDTO dto = AttendanceResponseDTO.builder().employeeId(1L).build();
    when(attendanceRepository.findAllByDate(date)).thenReturn(List.of(dto));

    List<AttendanceResponseDTO> result = attendanceServiceImpl.getAttendancesByDate(date);

    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).getEmployeeId());
  }

  @Test
  void getAttendanceSummary_WhenCalled_ShouldReturnGroupedSummary() {
    Employee emp1 = getMockEmployee(Role.SERVICE_ADVISOR);
    emp1.setEmployeeId(1L);
    Attendance att1 = Attendance.builder()
        .employee(emp1)
        .date(LocalDate.now())
        .isPresent(true)
        .note("Có mặt")
        .build();

    Employee emp2 = getMockEmployee(Role.ACCOUNTANT);
    emp2.setEmployeeId(2L);
    Attendance att2 = Attendance.builder()
        .employee(emp2)
        .date(LocalDate.now())
        .isPresent(false)
        .note("Vắng")
        .build();

    when(attendanceRepository.findByDateBetween(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of(att1, att2));

    List<AttendanceSummaryDTO> result = attendanceServiceImpl.getAttendanceSummary(LocalDate.now(), LocalDate.now());

    assertEquals(2, result.size());
    AttendanceSummaryDTO dto1 = result.stream().filter(dto -> dto.getEmployeeId().equals(1L)).findFirst().orElse(null);
    AttendanceSummaryDTO dto2 = result.stream().filter(dto -> dto.getEmployeeId().equals(2L)).findFirst().orElse(null);

    assertNotNull(dto1);
    assertEquals("John Doe", dto1.getEmployeeName());
    assertEquals(1, dto1.getAttendanceData().size());
    assertTrue(dto1.getAttendanceData().get(0).getIsPresent());

    assertNotNull(dto2);
    assertEquals("John Doe", dto2.getEmployeeName());
    assertEquals(1, dto2.getAttendanceData().size());
    assertFalse(dto2.getAttendanceData().get(0).getIsPresent());
  }
}
