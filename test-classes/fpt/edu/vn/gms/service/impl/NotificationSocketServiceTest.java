package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.entity.Notification;
import fpt.edu.vn.gms.mapper.NotificationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationSocketServiceTest {

    @Mock
    SimpMessagingTemplate template;
    @Mock
    NotificationMapper notificationMapper;

    @InjectMocks
    NotificationSocketService service;

    @Test
    void pushToUser_ShouldSendNotificationViaWebSocket() {
        Notification notification = Notification.builder()
                .id(1L)
                .title("Test Title")
                .build();

        NotificationResponseDto dto = NotificationResponseDto.builder()
                .id(1L)
                .title("Test Title")
                .build();
        when(notificationMapper.toResponseDto(notification)).thenReturn(dto);

        service.pushToUser(100L, notification);

        verify(notificationMapper).toResponseDto(notification);
        verify(template).convertAndSend("/topic/noti/100", dto);
    }
}

