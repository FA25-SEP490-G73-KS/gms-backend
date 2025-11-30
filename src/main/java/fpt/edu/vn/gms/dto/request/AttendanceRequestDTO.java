package fpt.edu.vn.gms.dto.request;

import com.google.firebase.database.annotations.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceRequestDTO {
  @NotNull
  private Long employeeId;

  @NotNull
  private Boolean isPresent;

  private String note;
}
