package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemDetailDto;
import fpt.edu.vn.gms.repository.PurchaseRequestItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PurchaseRequestItemServiceImplTest extends BaseServiceTest {

  @Mock
  private PurchaseRequestItemRepository purchaseRequestItemRepository;

  @InjectMocks
  private PurchaseRequestItemServiceImpl purchaseRequestItemServiceImpl;

  @Test
  void getItemsByPurchaseRequestId_WhenItemsExist_ShouldReturnListOfDetailDto() {
    Long requestId = 1L;
    PurchaseRequestItemDetailDto dto1 = PurchaseRequestItemDetailDto.builder().itemId(1L).partName("Item 1").build();
    PurchaseRequestItemDetailDto dto2 = PurchaseRequestItemDetailDto.builder().itemId(2L).partName("Item 2").build();
    when(purchaseRequestItemRepository.findItemsByPurchaseRequestId(requestId)).thenReturn(List.of(dto1, dto2));

    List<PurchaseRequestItemDetailDto> result = purchaseRequestItemServiceImpl.getItemsByPurchaseRequestId(requestId);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Item 1", result.get(0).getPartName());
    assertEquals("Item 2", result.get(1).getPartName());
    verify(purchaseRequestItemRepository).findItemsByPurchaseRequestId(requestId);
  }

  @Test
  void getItemsByPurchaseRequestId_WhenNoItemsExist_ShouldReturnEmptyList() {
    Long requestId = 2L;
    when(purchaseRequestItemRepository.findItemsByPurchaseRequestId(requestId)).thenReturn(List.of());

    List<PurchaseRequestItemDetailDto> result = purchaseRequestItemServiceImpl.getItemsByPurchaseRequestId(requestId);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(purchaseRequestItemRepository).findItemsByPurchaseRequestId(requestId);
  }
}
