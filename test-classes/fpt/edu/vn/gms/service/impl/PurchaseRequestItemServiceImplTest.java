package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.repository.PurchaseRequestItemRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseRequestItemServiceImplTest {

    @Mock
    PurchaseRequestItemRepository purchaseRequestItemRepository;

    @InjectMocks
    PurchaseRequestItemServiceImpl service;

    // Note: getItemsByPurchaseRequestId method has been commented out in the service implementation
    // These tests have been removed as the method is no longer available
    // If the method is re-enabled in the future, these tests should be restored:
    //
    // @Test
    // void getItemsByPurchaseRequestId_ShouldReturnListFromRepository() {
    //     Long prId = 1L;
    //     PurchaseRequestItemDetailDto dto1 = new PurchaseRequestItemDetailDto();
    //     PurchaseRequestItemDetailDto dto2 = new PurchaseRequestItemDetailDto();
    //     List<PurchaseRequestItemDetailDto> expected = List.of(dto1, dto2);
    //
    //     when(purchaseRequestItemRepository.findItemsByPurchaseRequestId(prId))
    //             .thenReturn(expected);
    //
    //     List<PurchaseRequestItemDetailDto> result = service.getItemsByPurchaseRequestId(prId);
    //
    //     assertSame(expected, result);
    //     assertEquals(2, result.size());
    //     verify(purchaseRequestItemRepository).findItemsByPurchaseRequestId(prId);
    // }
    //
    // @Test
    // void getItemsByPurchaseRequestId_ShouldReturnEmptyList_WhenNoItems() {
    //     Long prId = 1L;
    //     when(purchaseRequestItemRepository.findItemsByPurchaseRequestId(prId))
    //             .thenReturn(List.of());
    //
    //     List<PurchaseRequestItemDetailDto> result = service.getItemsByPurchaseRequestId(prId);
    //
    //     assertNotNull(result);
    //     assertTrue(result.isEmpty());
    //     verify(purchaseRequestItemRepository).findItemsByPurchaseRequestId(prId);
    // }
}

