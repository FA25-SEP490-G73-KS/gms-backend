package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.entity.Notification;
import fpt.edu.vn.gms.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSocketService {

    private final SimpMessagingTemplate template;
    private final NotificationMapper notificationMapper;
    /**
     * Gửi notification realtime tới advisor theo số điện thoại
     */
    public void pushToUser(Long employeeId, Notification n) {
        template.convertAndSend("/topic/noti/" + employeeId, notificationMapper.toResponseDto(n));
    }
}
