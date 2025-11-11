package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi notification realtime tới advisor theo số điện thoại
     */
    public void sendToAdvisor(String advisorPhone, NotificationResponseDto notification) {
        // Topic riêng cho từng advisor
        messagingTemplate.convertAndSend("/queue/notifications-" + advisorPhone, notification);
    }
}
