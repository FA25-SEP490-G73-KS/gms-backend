package fpt.edu.vn.gms.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDto {
    private Long assignmentId;
    private Long serviceTicketId;
    private Long employeeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String note;
}
