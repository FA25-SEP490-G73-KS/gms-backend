package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDto {

    private Long id;
    private String type;
    private String title;
    private String message;
    private String status;
    private LocalDateTime createdAt;
    private String actionPath;
}
