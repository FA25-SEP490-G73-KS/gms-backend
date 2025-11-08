package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDto {

    private String title;
    private String message;
    private String code;
    private NotificationType type;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
