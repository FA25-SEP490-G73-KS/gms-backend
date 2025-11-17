package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import fpt.edu.vn.gms.common.enums.NotificationType;

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
