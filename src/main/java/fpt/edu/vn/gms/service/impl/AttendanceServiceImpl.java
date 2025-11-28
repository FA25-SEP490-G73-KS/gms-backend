package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.AttendanceRequestDTO;
import fpt.edu.vn.gms.dto.response.AttendanceResponseDTO;
import fpt.edu.vn.gms.dto.response.AttendanceSummaryDTO;
import fpt.edu.vn.gms.entity.Attendance;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.exception.EmployeeNotFoundException;
import fpt.edu.vn.gms.repository.AttendanceRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.service.AttendanceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {
  AttendanceRepository attendanceRepository;
  EmployeeRepository employeeRepository;

  @Scheduled(cron = "0 59 23 * * *", zone = "Asia/Ho_Chi_Minh")
  public void markMissingAttendances() {
    Set<Employee> employees = new HashSet<>(employeeRepository.findAll());
    Set<Employee> employeesWithAttendance = employeeRepository.findEmployeesWithAttendances(LocalDate.now());
    employees.removeAll(employeesWithAttendance);

    LocalDateTime currentDateTime = LocalDateTime.now();
    attendanceRepository.saveAll(employees.stream()
        .map(emp -> Attendance.builder().employee(emp).isPresent(false).date(LocalDate.now())
            .recordedAt(currentDateTime).build())
        .collect(Collectors.toList()));
    log.info("Đã tự động điểm danh vắng mặt cho {} nhân viên vào lúc {}", employees.size(),
        currentDateTime);
  }

  @Override
  @Transactional
  public void markAttendances(List<AttendanceRequestDTO> requests, Long managerId) {
    Map<Long, AttendanceRequestDTO> uniqueRequests = requests.stream()
        .collect(Collectors.toMap(
            AttendanceRequestDTO::getEmployeeId,
            request -> request,
            (oldValue, newValue) -> newValue));

    List<AttendanceRequestDTO> cleanRequests = new ArrayList<>(uniqueRequests.values());

    attendanceRepository.saveAll(cleanRequests.stream().map(req -> {
      Employee emp = employeeRepository.findById(req.getEmployeeId()).orElseThrow(EmployeeNotFoundException::new);
      Attendance att = attendanceRepository.findByEmployeeAndDate(emp, LocalDate.now()).orElse(new Attendance());

      att.setEmployee(emp);
      att.setIsPresent(req.getIsPresent());
      att.setDate(LocalDate.now());
      att.setNote(req.getNote());
      att.setRecordedBy(managerId);
      att.setRecordedAt(LocalDateTime.now());
      return att;
    }).collect(Collectors.toList()));
  }

  @Override
  public List<AttendanceResponseDTO> getAttendancesByDate(LocalDate date) {
    return attendanceRepository.findAllByDate(date);
  }

  @Override
  public List<AttendanceSummaryDTO> getAttendanceSummary(LocalDate startDate, LocalDate endDate) {
    List<Attendance> flatList = attendanceRepository.findByDateBetween(startDate, endDate);
    // Group by employeeId
    Map<Long, AttendanceSummaryDTO> grouped = new LinkedHashMap<>();
    for (Attendance flat : flatList) {
      Long empId = flat.getEmployee().getEmployeeId();
      AttendanceSummaryDTO.AttendanceData data = new AttendanceSummaryDTO.AttendanceData();
      data.setDate(flat.getDate());
      data.setIsPresent(flat.getIsPresent());
      data.setNote(flat.getNote());
      if (!grouped.containsKey(empId)) {
        AttendanceSummaryDTO dto = new AttendanceSummaryDTO();
        dto.setEmployeeId(empId);
        dto.setEmployeeName(flat.getEmployee().getFullName());
        dto.setAttendanceData(new ArrayList<>());
        grouped.put(empId, dto);
      }
      grouped.get(empId).getAttendanceData().add(data);
    }
    return new ArrayList<>(grouped.values());
  }
}
