package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.AttendanceRequestDTO;
import fpt.edu.vn.gms.dto.response.AttendanceResponseDTO;
import fpt.edu.vn.gms.dto.response.AttendanceSummaryDTO;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
  void markAttendances(LocalDate date, List<AttendanceRequestDTO> requests, Long managerId);

  List<AttendanceResponseDTO> getAttendancesByDate(LocalDate date);

  List<AttendanceSummaryDTO> getAttendanceSummary(LocalDate startDate, LocalDate endDate);
}
