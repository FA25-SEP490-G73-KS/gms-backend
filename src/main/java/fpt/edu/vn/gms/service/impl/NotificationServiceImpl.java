package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.NotificationStatus;
import fpt.edu.vn.gms.common.enums.NotificationType;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.Notification;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.NotificationMapper;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.repository.NotificationRepository;
import fpt.edu.vn.gms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationSocketService socketService;

    /**
     * Tạo notification và gửi realtime
     */
    @Override
    public NotificationResponseDto createNotification(
            Long receiverId,
            String title,
            String message,
            NotificationType type,
            String referenceId,
            String actionPath) {

        Employee receiver = employeeRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Notification notification = Notification.builder()
                .receiver(receiver)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId != null ? Long.valueOf(referenceId) : null)
                .referenceType(null) // Bạn có thể map type nếu cần
                .actionPath(actionPath)
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        NotificationResponseDto dto = notificationMapper.toResponseDto(notification);

        // Push realtime qua WebSocket
        socketService.pushToUser(receiverId, notification);

        return dto;
    }

    @Override
    public List<NotificationResponseDto> getNotificationsForUser(String recipientPhone) {
        List<Notification> notifications = notificationRepository
                .findByReceiver_PhoneOrderByCreatedAtDesc(recipientPhone);

        return notifications.stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId, String recipientPhone) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getReceiver().getPhone().equals(recipientPhone)) {
            throw new RuntimeException("Notification does not belong to this user");
        }

        notification.setStatus(NotificationStatus.READ);
        notificationRepository.save(notification);
    }

    @Override
    public NotificationResponseDto getNotificationById(Long id, String recipientPhone) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getReceiver().getPhone().equals(recipientPhone)) {
            throw new RuntimeException("Notification does not belong to this user");
        }

        return notificationMapper.toResponseDto(notification);
    }
}
