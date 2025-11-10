package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.NotificationType;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.entity.Notification;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.mapper.NotificationMapper;
import fpt.edu.vn.gms.repository.NotificationRepository;
import fpt.edu.vn.gms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;


    public NotificationResponseDto createNotification(String recipientPhone,
                                                      String title,
                                                      String message,
                                                      NotificationType type) {

        Notification notification = Notification.builder()
                .recipientPhone(recipientPhone)
                .title(title)
                .message(message)
                .type(type)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        // map entity -> DTO
        return notificationMapper.toResponseDto(saved);
    }

    @Override
    public List<NotificationResponseDto> getNotificationsForUser(String recipientPhone) {

        return notificationRepository.findByRecipientPhoneOrderByCreatedAtDesc(recipientPhone)
                .stream()
                .map(notificationMapper::toResponseDto)
                .toList();
    }
}
