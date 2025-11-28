package fpt.edu.vn.gms.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AttendanceSummaryDTO {
  private Long employeeId;
  private String employeeName;
  private String employeePhone;
  private String employeeRole;
  private List<AttendanceData> attendanceData;

  @Data
  public static class AttendanceData {
    private LocalDate date;
    private Boolean isPresent;
    private String note;
  }
}
