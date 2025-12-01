package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.NotificationStatus;
import fpt.edu.vn.gms.common.enums.NotificationType;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.Notification;
import fpt.edu.vn.gms.mapper.NotificationMapper;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    NotificationRepository notificationRepository;
    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    NotificationMapper notificationMapper;
    @Mock
    NotificationSocketService socketService;

    @InjectMocks
    NotificationServiceImpl service;

    @Test
    void createNotification_ShouldCreateAndReturnDto() {
        Employee receiver = Employee.builder()
                .employeeId(1L)
                .fullName("Receiver")
                .phone("0912345678")
                .build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(receiver));

        Notification savedNotification = Notification.builder()
                .id(100L)
                .receiver(receiver)
                .type(NotificationType.QUOTATION_CONFIRMED)
                .title("Test Title")
                .message("Test Message")
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        NotificationResponseDto dto = NotificationResponseDto.builder()
                .id(100L)
                .title("Test Title")
                .message("Test Message")
                .build();
        when(notificationMapper.toResponseDto(savedNotification)).thenReturn(dto);

        NotificationResponseDto result = service.createNotification(
                1L, "Test Title", "Test Message",
                NotificationType.QUOTATION_CONFIRMED, "123", "/path");

        assertSame(dto, result);
        verify(employeeRepository).findById(1L);
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).toResponseDto(savedNotification);
        verify(socketService).pushToUser(1L, savedNotification);
    }

    @Test
    void createNotification_ShouldThrow_WhenReceiverNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.createNotification(1L, "Title", "Message",
                        NotificationType.PURCHASE_REQUEST, "123", "/path"));
        verify(employeeRepository).findById(1L);
        verify(notificationRepository, never()).save(any());
        verify(socketService, never()).pushToUser(anyLong(), any());
    }

    @Test
    void getNotificationsForUser_ShouldReturnListOfDtos() {
        Employee receiver = Employee.builder()
                .employeeId(1L)
                .phone("0912345678")
                .build();
        Notification noti1 = Notification.builder()
                .id(1L)
                .receiver(receiver)
                .title("Title 1")
                .build();
        Notification noti2 = Notification.builder()
                .id(2L)
                .receiver(receiver)
                .title("Title 2")
                .build();
        when(notificationRepository.findByReceiver_PhoneOrderByCreatedAtDesc("0912345678"))
                .thenReturn(List.of(noti1, noti2));

        NotificationResponseDto dto1 = NotificationResponseDto.builder().id(1L).title("Title 1").build();
        NotificationResponseDto dto2 = NotificationResponseDto.builder().id(2L).title("Title 2").build();
        when(notificationMapper.toResponseDto(noti1)).thenReturn(dto1);
        when(notificationMapper.toResponseDto(noti2)).thenReturn(dto2);

        List<NotificationResponseDto> result = service.getNotificationsForUser("0912345678");

        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));
        verify(notificationRepository).findByReceiver_PhoneOrderByCreatedAtDesc("0912345678");
        verify(notificationMapper).toResponseDto(noti1);
        verify(notificationMapper).toResponseDto(noti2);
    }

    @Test
    void markAsRead_ShouldUpdateStatus_WhenNotificationBelongsToUser() {
        Employee receiver = Employee.builder()
                .employeeId(1L)
                .phone("0912345678")
                .build();
        Notification notification = Notification.builder()
                .id(100L)
                .receiver(receiver)
                .status(NotificationStatus.UNREAD)
                .build();
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        service.markAsRead(100L, "0912345678");

        assertEquals(NotificationStatus.READ, notification.getStatus());
        verify(notificationRepository).findById(100L);
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_ShouldThrow_WhenNotificationNotFound() {
        when(notificationRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.markAsRead(100L, "0912345678"));
        verify(notificationRepository).findById(100L);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markAsRead_ShouldThrow_WhenNotificationDoesNotBelongToUser() {
        Employee receiver = Employee.builder()
                .employeeId(1L)
                .phone("0912345678")
                .build();
        Notification notification = Notification.builder()
                .id(100L)
                .receiver(receiver)
                .status(NotificationStatus.UNREAD)
                .build();
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        assertThrows(RuntimeException.class,
                () -> service.markAsRead(100L, "0987654321"));
        verify(notificationRepository).findById(100L);
        verify(notificationRepository, never()).save(any());
    }
}

