package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDto {
    private Long id;
    private String title;
    private String message;
    private Long serviceTicketId;
    private Long quotationId;
    private NotificationType type;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
