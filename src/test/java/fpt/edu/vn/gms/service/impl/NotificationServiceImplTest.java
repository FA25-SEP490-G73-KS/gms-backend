package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.NotificationStatus;
import fpt.edu.vn.gms.common.enums.NotificationType;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.Notification;
import fpt.edu.vn.gms.mapper.NotificationMapper;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceImplTest extends BaseServiceTest {

  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private EmployeeRepository employeeRepository;
  @Mock
  private NotificationMapper notificationMapper;
  @Mock
  private NotificationSocketService socketService;

  @InjectMocks
  private NotificationServiceImpl notificationServiceImpl;

  @Test
  void createNotification_WhenReceiverExists_ShouldSaveAndPushNotification() {
    Employee receiver = getMockEmployee(Role.SERVICE_ADVISOR);
    receiver.setEmployeeId(1L);

    Notification notification = Notification.builder()
        .receiver(receiver)
        .type(NotificationType.QUOTATION_CONFIRMED)
        .title("Test Title")
        .message("Test Message")
        .referenceId(123L)
        .actionPath("/test")
        .status(NotificationStatus.UNREAD)
        .createdAt(LocalDateTime.now())
        .build();

    NotificationResponseDto dto = NotificationResponseDto.builder().title("Test Title").build();

    when(employeeRepository.findById(1L)).thenReturn(Optional.of(receiver));
    when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
    when(notificationMapper.toResponseDto(any(Notification.class))).thenReturn(dto);

    NotificationResponseDto result = notificationServiceImpl.createNotification(
        1L, "Test Title", "Test Message", NotificationType.QUOTATION_CONFIRMED, "123", "/test");

    assertNotNull(result);
    assertEquals("Test Title", result.getTitle());
    verify(notificationRepository).save(any(Notification.class));
    verify(socketService).pushToUser(eq(1L), any(Notification.class));
  }

  @Test
  void createNotification_WhenReceiverNotFound_ShouldThrowException() {
    when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class, () -> notificationServiceImpl.createNotification(
        99L, "Title", "Msg", NotificationType.QUOTATION_CONFIRMED, null, null));
    assertTrue(ex.getMessage().contains("Receiver not found"));
    verify(notificationRepository, never()).save(any());
  }

  @Test
  void getNotificationsForUser_WhenNotificationsExist_ShouldReturnDtoList() {
    Employee receiver = getMockEmployee(Role.SERVICE_ADVISOR);
    Notification notification = Notification.builder()
        .id(1L)
        .receiver(receiver)
        .title("Title")
        .build();
    NotificationResponseDto dto = NotificationResponseDto.builder().title("Title").build();

    when(notificationRepository.findByReceiver_PhoneOrderByCreatedAtDesc("0123456789"))
        .thenReturn(List.of(notification));
    when(notificationMapper.toResponseDto(notification)).thenReturn(dto);

    List<NotificationResponseDto> result = notificationServiceImpl.getNotificationsForUser("0123456789");

    assertEquals(1, result.size());
    assertEquals("Title", result.get(0).getTitle());
  }

  @Test
  void getNotificationsForUser_WhenNoNotifications_ShouldReturnEmptyList() {
    when(notificationRepository.findByReceiver_PhoneOrderByCreatedAtDesc("0123456789"))
        .thenReturn(List.of());

    List<NotificationResponseDto> result = notificationServiceImpl.getNotificationsForUser("0123456789");

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void markAsRead_WhenNotificationExistsAndBelongsToUser_ShouldUpdateStatus() {
    Employee receiver = getMockEmployee(Role.SERVICE_ADVISOR);
    receiver.setPhone("0123456789");
    Notification notification = Notification.builder()
        .id(1L)
        .receiver(receiver)
        .status(NotificationStatus.UNREAD)
        .build();

    when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
    when(notificationRepository.save(notification)).thenReturn(notification);

    notificationServiceImpl.markAsRead(1L, "0123456789");

    assertEquals(NotificationStatus.READ, notification.getStatus());
    verify(notificationRepository).save(notification);
  }

  @Test
  void markAsRead_WhenNotificationNotFound_ShouldThrowException() {
    when(notificationRepository.findById(99L)).thenReturn(Optional.empty());
    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> notificationServiceImpl.markAsRead(99L, "0123456789"));
    assertTrue(ex.getMessage().contains("Notification not found"));
  }

  @Test
  void markAsRead_WhenNotificationDoesNotBelongToUser_ShouldThrowException() {
    Employee receiver = getMockEmployee(Role.SERVICE_ADVISOR);
    receiver.setPhone("0987654321");
    Notification notification = Notification.builder()
        .id(1L)
        .receiver(receiver)
        .status(NotificationStatus.UNREAD)
        .build();

    when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> notificationServiceImpl.markAsRead(1L, "0123456789"));
    assertTrue(ex.getMessage().contains("Notification does not belong to this user"));
    verify(notificationRepository, never()).save(any());
  }
}
