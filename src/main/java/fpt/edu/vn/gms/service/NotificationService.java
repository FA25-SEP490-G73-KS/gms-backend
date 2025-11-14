package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.NotificationType;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.entity.Notification;

import java.util.List;

public interface NotificationService {

    NotificationResponseDto createNotification(Long receiver,
                                               String title,
                                               String message,
                                               NotificationType type,
                                               String referenceId,
                                               String actionPath);

    List<NotificationResponseDto> getNotificationsForUser(String recipientPhone);

    void markAsRead(Long notificationId, String recipientPhone);
}
