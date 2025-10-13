package fpt.edu.vn.gms.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDto {
    private Long attendanceId;
    private Long employeeId;
    private LocalDate date;
    private Boolean isPresentAm;
    private Boolean isPresentPm;
    private String note;
    private Integer recordedBy;
    private LocalDateTime recordedAt;
}
