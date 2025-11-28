package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.common.enums.NotificationTemplate;
import fpt.edu.vn.gms.common.enums.NotificationType;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PurchaseRequestDetailMapper;
import fpt.edu.vn.gms.mapper.PurchaseRequestItemMapper;
import fpt.edu.vn.gms.mapper.PurchaseRequestMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PurchaseRequestServiceImplTest extends BaseServiceTest {

  @Mock
  private PurchaseRequestRepository purchaseRequestRepo;
  @Mock
  private PurchaseRequestItemRepository purchaseRequestItemRepo;
  @Mock
  private AccountRepository accountRepository;
  @Mock
  private NotificationService notificationService;
  @Mock
  private PurchaseRequestMapper purchaseRequestMapper;
  @Mock
  private PurchaseRequestItemMapper purchaseRequestItemMapper;
  @Mock
  private PurchaseRequestDetailMapper purchaseRequestDetailMapper;

  @InjectMocks
  private PurchaseRequestServiceImpl purchaseRequestServiceImpl;

  @Test
  void getPurchaseRequests_WhenRequestsExist_ShouldReturnPagedDtos() {
    PurchaseRequest pr = PurchaseRequest.builder().id(1L).build();
    PurchaseRequestResponseDto dto = PurchaseRequestResponseDto.builder().id(1L).build();
    Page<PurchaseRequest> page = new PageImpl<>(List.of(pr));
    when(purchaseRequestRepo.findAll(any(Pageable.class))).thenReturn(page);
    when(purchaseRequestMapper.toResponseDto(pr)).thenReturn(dto);

    Page<PurchaseRequestResponseDto> result = purchaseRequestServiceImpl.getPurchaseRequests(0, 10);

    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getId());
  }

  @Test
  void getPurchaseRequestItems_WhenRequestExists_ShouldReturnDetailDto() {
    PurchaseRequest pr = PurchaseRequest.builder().id(1L).build();
    PurchaseRequestResponseDto prDto = PurchaseRequestResponseDto.builder().id(1L).build();
    PurchaseRequestItemResponseDto itemDto = PurchaseRequestItemResponseDto.builder().itemId(10L).build();
    PurchaseRequestDetailDto dto = PurchaseRequestDetailDto.builder()
        .purchaseRequest(prDto)
        .items(List.of(itemDto))
        .build();
    when(purchaseRequestRepo.findById(1L)).thenReturn(Optional.of(pr));
    when(purchaseRequestDetailMapper.toDetailDto(pr)).thenReturn(dto);

    PurchaseRequestDetailDto result = purchaseRequestServiceImpl.getPurchaseRequestItems(1L);

    assertNotNull(result);
    assertEquals(1L, result.getPurchaseRequest().getId());
    assertEquals(1, result.getItems().size());
    assertEquals(10L, result.getItems().get(0).getItemId());
  }

  @Test
  void getPurchaseRequestItems_WhenRequestNotFound_ShouldThrowResourceNotFoundException() {
    when(purchaseRequestRepo.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> purchaseRequestServiceImpl.getPurchaseRequestItems(99L));
  }

  @Test
  void reviewItem_WhenApproveTrue_ShouldSetStatusApprovedAndUpdatePRStatus() {
    Employee manager = getMockEmployee(Role.MANAGER);
    PurchaseRequest pr = PurchaseRequest.builder().id(1L).build();
    PurchaseRequestItem item = PurchaseRequestItem.builder()
        .purchaseRequest(pr)
        .reviewStatus(ManagerReviewStatus.PENDING)
        .build();
    PurchaseRequestItemResponseDto dto = PurchaseRequestItemResponseDto.builder().itemId(2L).build();

    when(purchaseRequestItemRepo.findById(2L)).thenReturn(Optional.of(item));
    when(purchaseRequestItemRepo.save(item)).thenReturn(item);
    when(purchaseRequestItemMapper.toResponseDto(item)).thenReturn(dto);

    // Mock PR items for updatePRStatus
    pr.setItems(List.of(item));
    when(purchaseRequestRepo.save(pr)).thenReturn(pr);

    PurchaseRequestItemResponseDto result = purchaseRequestServiceImpl.reviewItem(2L, true, "OK", manager);

    assertEquals(ManagerReviewStatus.APPROVED, item.getReviewStatus());
    assertEquals("OK", item.getNote());
    assertNotNull(item.getUpdatedAt());
    assertEquals(manager.getEmployeeId(), item.getUpdatedBy());
    assertNotNull(result);
  }

  @Test
  void reviewItem_WhenApproveFalse_ShouldSetStatusRejectedAndUpdatePRStatus() {
    Employee manager = getMockEmployee(Role.MANAGER);
    PurchaseRequest pr = PurchaseRequest.builder().id(1L).build();
    PurchaseRequestItem item = PurchaseRequestItem.builder()
        .purchaseRequest(pr)
        .reviewStatus(ManagerReviewStatus.PENDING)
        .build();
    PurchaseRequestItemResponseDto dto = PurchaseRequestItemResponseDto.builder().itemId(2L).build();

    when(purchaseRequestItemRepo.findById(2L)).thenReturn(Optional.of(item));
    when(purchaseRequestItemRepo.save(item)).thenReturn(item);
    when(purchaseRequestItemMapper.toResponseDto(item)).thenReturn(dto);

    // Mock PR items for updatePRStatus
    pr.setItems(List.of(item));
    when(purchaseRequestRepo.save(pr)).thenReturn(pr);

    PurchaseRequestItemResponseDto result = purchaseRequestServiceImpl.reviewItem(2L, false, "Not OK", manager);

    assertEquals(ManagerReviewStatus.REJECTED, item.getReviewStatus());
    assertEquals("Not OK", item.getNote());
    assertNotNull(item.getUpdatedAt());
    assertEquals(manager.getEmployeeId(), item.getUpdatedBy());
    assertNotNull(result);
  }

  @Test
  void reviewItem_WhenItemNotFound_ShouldThrowResourceNotFoundException() {
    Employee manager = getMockEmployee(Role.MANAGER);
    when(purchaseRequestItemRepo.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class,
        () -> purchaseRequestServiceImpl.reviewItem(99L, true, "note", manager));
  }

  @Test
  void updatePRStatus_WhenAnyRejected_ShouldSetPRRejectedAndNotifyWarehouse() {
    PurchaseRequest pr = PurchaseRequest.builder().id(1L).build();
    PurchaseRequestItem item1 = PurchaseRequestItem.builder().reviewStatus(ManagerReviewStatus.APPROVED).build();
    PurchaseRequestItem item2 = PurchaseRequestItem.builder().reviewStatus(ManagerReviewStatus.REJECTED).build();
    pr.setItems(List.of(item1, item2));
    when(purchaseRequestRepo.save(pr)).thenReturn(pr);

    Account acc = Account.builder().employee(getMockEmployee(Role.WAREHOUSE)).role(Role.WAREHOUSE).build();
    when(accountRepository.findByRole(Role.WAREHOUSE)).thenReturn(List.of(acc));

    // Use reflection to call protected method
    invokeUpdatePRStatus(pr);

    assertEquals(ManagerReviewStatus.REJECTED, pr.getReviewStatus());
    verify(notificationService).createNotification(
        anyLong(),
        eq(NotificationTemplate.PURCHASE_REQUEST_REJECTED.getTitle()),
        anyString(),
        eq(NotificationType.PURCHASE_REQUEST),
        eq(pr.getId().toString()),
        anyString());
  }

  @Test
  void updatePRStatus_WhenAllApproved_ShouldSetPRApprovedAndNotifyWarehouse() {
    PurchaseRequest pr = PurchaseRequest.builder().id(1L).build();
    PurchaseRequestItem item1 = PurchaseRequestItem.builder().reviewStatus(ManagerReviewStatus.APPROVED).build();
    PurchaseRequestItem item2 = PurchaseRequestItem.builder().reviewStatus(ManagerReviewStatus.APPROVED).build();
    pr.setItems(List.of(item1, item2));
    when(purchaseRequestRepo.save(pr)).thenReturn(pr);

    Account acc = Account.builder().employee(getMockEmployee(Role.WAREHOUSE)).role(Role.WAREHOUSE).build();
    when(accountRepository.findByRole(Role.WAREHOUSE)).thenReturn(List.of(acc));

    invokeUpdatePRStatus(pr);

    assertEquals(ManagerReviewStatus.APPROVED, pr.getReviewStatus());
    verify(notificationService).createNotification(
        anyLong(),
        eq(NotificationTemplate.PURCHASE_REQUEST_CONFIRMED.getTitle()),
        anyString(),
        eq(NotificationType.PURCHASE_REQUEST),
        eq(pr.getId().toString()),
        anyString());
  }

  @Test
  void updatePRStatus_WhenPending_ShouldSetPRPending() {
    PurchaseRequest pr = PurchaseRequest.builder().id(1L).build();
    PurchaseRequestItem item1 = PurchaseRequestItem.builder().reviewStatus(ManagerReviewStatus.APPROVED).build();
    PurchaseRequestItem item2 = PurchaseRequestItem.builder().reviewStatus(ManagerReviewStatus.PENDING).build();
    pr.setItems(List.of(item1, item2));
    when(purchaseRequestRepo.save(pr)).thenReturn(pr);

    invokeUpdatePRStatus(pr);

    assertEquals(ManagerReviewStatus.PENDING, pr.getReviewStatus());
    verify(purchaseRequestRepo).save(pr);
  }

  // Helper to invoke protected method updatePRStatus
  private void invokeUpdatePRStatus(PurchaseRequest pr) {
    try {
      java.lang.reflect.Method m = PurchaseRequestServiceImpl.class.getDeclaredMethod("updatePRStatus",
          PurchaseRequest.class);
      m.setAccessible(true);
      m.invoke(purchaseRequestServiceImpl, pr);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
