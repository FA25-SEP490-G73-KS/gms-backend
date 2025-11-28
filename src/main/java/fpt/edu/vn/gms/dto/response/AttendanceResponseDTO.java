package fpt.edu.vn.gms.dto.response;

import java.time.LocalDateTime;

import fpt.edu.vn.gms.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDTO {
  private Long employeeId;
  private String employeeName;
  private String employeePhone;
  private Role employeeRole;
  private Boolean isPresent;
  private String note;
  private Long recordedBy;
  private LocalDateTime recordedAt;
}
