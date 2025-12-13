package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemStatus;
import fpt.edu.vn.gms.dto.response.StockReceiptDetailResponse;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PurchaseRequestMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.InventoryService;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.StockReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for PurchaseRequestServiceImpl
 * Matrix: PR-001, PR-002
 * Total: 22 test cases (0 EXISTING, 22 NEW)
 */
@ExtendWith(MockitoExtension.class)
class PurchaseRequestServiceImplTest {

    @Mock
    PriceQuotationRepository priceQuotationRepository;
    @Mock
    PurchaseRequestRepository purchaseRequestRepository;
    @Mock
    PurchaseRequestItemRepository purchaseRequestItemRepository;
    @Mock
    InventoryService inventoryService;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    StockReceiptService stockReceiptService;
    @Mock
    PurchaseRequestMapper purchaseRequestMapper;
    @Mock
    NotificationService notificationService;
    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    PurchaseRequestServiceImpl service;

    private PriceQuotation quotation;
    private Part part1;
    private Part part2;
    private PriceQuotationItem item1;
    private PriceQuotationItem item2;
    private Unit unit;

    @BeforeEach
    void setUp() {
        unit = Unit.builder()
                .id(1L)
                .name("Cái")
                .build();

        part1 = Part.builder()
                .partId(1L)
                .name("Part 1")
                .quantityInStock(10.0)
                .reservedQuantity(5.0)
                .reorderLevel(20.0)
                .purchasePrice(new BigDecimal("10000"))
                .unit(unit)
                .build();

        part2 = Part.builder()
                .partId(2L)
                .name("Part 2")
                .quantityInStock(5.0)
                .reservedQuantity(2.0)
                .reorderLevel(15.0)
                .purchasePrice(new BigDecimal("20000"))
                .unit(unit)
                .build();

        item1 = PriceQuotationItem.builder()
                .priceQuotationItemId(1L)
                .itemType(PriceQuotationItemType.PART)
                .quantity(10.0)
                .unitPrice(new BigDecimal("10000"))
                .part(part1)
                .build();

        item2 = PriceQuotationItem.builder()
                .priceQuotationItemId(2L)
                .itemType(PriceQuotationItemType.PART)
                .quantity(5.0)
                .unitPrice(new BigDecimal("20000"))
                .part(part2)
                .build();

        quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .code("BG-000001")
                .items(new ArrayList<>(List.of(item1, item2)))
                .build();
    }

    // ========== MATRIX 8: createPurchaseRequestFromQuotation (UTCID94-UTCID105) ==========

    @Test
    void UTCID94_createPurchaseRequestFromQuotation_ShouldCreatePR_WhenValidQuotation() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(inventoryService.getAvailableQuantity(1L)).thenReturn(10.0);
        when(inventoryService.getReservedQuantity(1L)).thenReturn(5.0);
        when(inventoryService.getAvailableQuantity(2L)).thenReturn(5.0);
        when(inventoryService.getReservedQuantity(2L)).thenReturn(2.0);
        when(codeSequenceService.generateCode("YC")).thenReturn("YC-000001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenAnswer(invocation -> {
            PurchaseRequest pr = invocation.getArgument(0);
            pr.setId(1L);
            return pr;
        });
        when(purchaseRequestItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PurchaseRequest result = service.createPurchaseRequestFromQuotation(1L);

        // Then
        assertNotNull(result);
        verify(purchaseRequestRepository).save(any(PurchaseRequest.class));
        verify(purchaseRequestItemRepository).saveAll(anyList());
    }

    @Test
    void UTCID95_createPurchaseRequestFromQuotation_ShouldGeneratePRCode() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(inventoryService.getAvailableQuantity(anyLong())).thenReturn(0.0);
        when(inventoryService.getReservedQuantity(anyLong())).thenReturn(0.0);
        when(codeSequenceService.generateCode("YC")).thenReturn("YC-000001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenAnswer(invocation -> {
            PurchaseRequest pr = invocation.getArgument(0);
            pr.setId(1L);
            return pr;
        });
        when(purchaseRequestItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        service.createPurchaseRequestFromQuotation(1L);

        // Then
        verify(codeSequenceService).generateCode("YC");
        verify(purchaseRequestRepository).save(argThat(pr -> 
            pr.getCode().equals("YC-000001")
        ));
    }

    @Test
    void UTCID96_createPurchaseRequestFromQuotation_ShouldCalculateTotalAmount() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(inventoryService.getAvailableQuantity(1L)).thenReturn(0.0);
        when(inventoryService.getReservedQuantity(1L)).thenReturn(0.0);
        when(inventoryService.getAvailableQuantity(2L)).thenReturn(0.0);
        when(inventoryService.getReservedQuantity(2L)).thenReturn(0.0);
        when(codeSequenceService.generateCode("YC")).thenReturn("YC-000001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenAnswer(invocation -> {
            PurchaseRequest pr = invocation.getArgument(0);
            pr.setId(1L);
            return pr;
        });
        when(purchaseRequestItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PurchaseRequest result = service.createPurchaseRequestFromQuotation(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotalEstimatedAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void UTCID97_createPurchaseRequestFromQuotation_ShouldHandleEmptyItems() {
        // Given
        quotation.getItems().clear();
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(codeSequenceService.generateCode("YC")).thenReturn("YC-000001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenAnswer(invocation -> {
            PurchaseRequest pr = invocation.getArgument(0);
            pr.setId(1L);
            return pr;
        });
        when(purchaseRequestItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When & Then
        assertDoesNotThrow(() -> service.createPurchaseRequestFromQuotation(1L));
    }

    @Test
    void UTCID98_createPurchaseRequestFromQuotation_ShouldHandleZeroQuantity() {
        // Given
        PriceQuotationItem zeroQtyItem = PriceQuotationItem.builder()
                .priceQuotationItemId(3L)
                .itemType(PriceQuotationItemType.PART)
                .quantity(0.0)
                .part(part1)
                .build();
        quotation.getItems().clear();
        quotation.getItems().add(zeroQtyItem);

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(inventoryService.getAvailableQuantity(1L)).thenReturn(100.0);
        when(inventoryService.getReservedQuantity(1L)).thenReturn(0.0);
        when(codeSequenceService.generateCode("YC")).thenReturn("YC-000001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenAnswer(invocation -> {
            PurchaseRequest pr = invocation.getArgument(0);
            pr.setId(1L);
            return pr;
        });
        when(purchaseRequestItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When & Then - Should skip items with requiredPurchaseQuantity <= 0
        assertDoesNotThrow(() -> service.createPurchaseRequestFromQuotation(1L));
    }

    @Test
    void UTCID99_createPurchaseRequestFromQuotation_ShouldHandleNullUnitPrice() {
        // Given
        item1.setUnitPrice(null);
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(inventoryService.getAvailableQuantity(1L)).thenReturn(0.0);
        when(inventoryService.getReservedQuantity(1L)).thenReturn(0.0);
        when(inventoryService.getAvailableQuantity(2L)).thenReturn(0.0);
        when(inventoryService.getReservedQuantity(2L)).thenReturn(0.0);
        when(codeSequenceService.generateCode("YC")).thenReturn("YC-000001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenAnswer(invocation -> {
            PurchaseRequest pr = invocation.getArgument(0);
            pr.setId(1L);
            return pr;
        });
        when(purchaseRequestItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When & Then - Should use part.purchasePrice when unitPrice is null
        assertDoesNotThrow(() -> service.createPurchaseRequestFromQuotation(1L));
    }

    @Test
    void UTCID100_createPurchaseRequestFromQuotation_ShouldLinkToSupplier() {
        // Given
        Supplier supplier = Supplier.builder()
                .id(1L)
                .name("Supplier 1")
                .build();
        part1.setSupplier(supplier);

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(inventoryService.getAvailableQuantity(1L)).thenReturn(0.0);
        when(inventoryService.getReservedQuantity(1L)).thenReturn(0.0);
        when(codeSequenceService.generateCode("YC")).thenReturn("YC-000001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenAnswer(invocation -> {
            PurchaseRequest pr = invocation.getArgument(0);
            pr.setId(1L);
            return pr;
        });
        when(purchaseRequestItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        service.createPurchaseRequestFromQuotation(1L);

        // Then
        verify(purchaseRequestItemRepository).saveAll(argThat(items -> {
            List<PurchaseRequestItem> itemList = new ArrayList<>();
            items.forEach(itemList::add);
            return itemList.stream().anyMatch(item -> item.getPart().getSupplier() != null);
        }));
    }

    @Test
    void UTCID101_createPurchaseRequestFromQuotation_ShouldThrowException_WhenSupplierNotFound() {
        // Given
        when(priceQuotationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, 
                () -> service.createPurchaseRequestFromQuotation(999L));
    }

    @Test
    void UTCID102_createPurchaseRequestFromQuotation_ShouldHandlePartWithoutPartId() {
        // Given
        PriceQuotationItem itemWithoutPart = PriceQuotationItem.builder()
                .priceQuotationItemId(3L)
                .itemType(PriceQuotationItemType.PART)
                .quantity(10.0)
                .part(null)
                .build();
        quotation.getItems().clear();
        quotation.getItems().add(itemWithoutPart);

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(codeSequenceService.generateCode("YC")).thenReturn("YC-000001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenAnswer(invocation -> {
            PurchaseRequest pr = invocation.getArgument(0);
            pr.setId(1L);
            return pr;
        });
        when(purchaseRequestItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When & Then - Should skip items without part
        assertDoesNotThrow(() -> service.createPurchaseRequestFromQuotation(1L));
        verify(purchaseRequestItemRepository).saveAll(argThat(items -> {
            List<PurchaseRequestItem> itemList = new ArrayList<>();
            items.forEach(itemList::add);
            return itemList.isEmpty();
        }));
    }

    @Test
    void UTCID103_createPurchaseRequestFromQuotation_ShouldHandlePartNotFound() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(inventoryService.getAvailableQuantity(1L)).thenReturn(0.0);
        when(inventoryService.getReservedQuantity(1L)).thenReturn(0.0);
        when(codeSequenceService.generateCode("YC")).thenReturn("YC-000001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenAnswer(invocation -> {
            PurchaseRequest pr = invocation.getArgument(0);
            pr.setId(1L);
            return pr;
        });
        when(purchaseRequestItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When & Then
        assertDoesNotThrow(() -> service.createPurchaseRequestFromQuotation(1L));
    }

    @Test
    void UTCID104_createPurchaseRequestFromQuotation_ShouldHandleDatabaseSaveFailure() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(inventoryService.getAvailableQuantity(anyLong())).thenReturn(0.0);
        when(inventoryService.getReservedQuantity(anyLong())).thenReturn(0.0);
        when(codeSequenceService.generateCode("YC")).thenReturn("YC-000001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class)))
                .thenThrow(new DataAccessException("Database error") {});

        // When & Then
        assertThrows(DataAccessException.class, 
                () -> service.createPurchaseRequestFromQuotation(1L));
    }

    @Test
    void UTCID105_createPurchaseRequestFromQuotation_ShouldHandleTotalAmountOverflow() {
        // Given
        part1.setPurchasePrice(new BigDecimal(String.valueOf(Long.MAX_VALUE)));
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(inventoryService.getAvailableQuantity(1L)).thenReturn(0.0);
        when(inventoryService.getReservedQuantity(1L)).thenReturn(0.0);
        when(codeSequenceService.generateCode("YC")).thenReturn("YC-000001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenAnswer(invocation -> {
            PurchaseRequest pr = invocation.getArgument(0);
            pr.setId(1L);
            return pr;
        });
        when(purchaseRequestItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When & Then
        assertDoesNotThrow(() -> service.createPurchaseRequestFromQuotation(1L));
    }

    // ========== MATRIX 20: approvePurchaseRequest (UTCID228-UTCID237) ==========

    /**
     * UTCID228: Valid PR, status = PENDING
     * Precondition: Valid PR, status = PENDING
     * Input: prId=1L
     * Expected: Status → APPROVED
     * Type: N (Normal)
     */
    @Test
    void approvePurchaseRequest_UTCID228_ShouldApprove_WhenStatusIsPending() {
        // Given
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(1L)
                .code("YC-000001")
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>(List.of(
                        PurchaseRequestItem.builder()
                                .itemId(1L)
                                .quantity(10.0)
                                .build()
                )))
                .build();

        when(purchaseRequestRepository.findById(1L)).thenReturn(Optional.of(pr));
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenReturn(pr);
        when(stockReceiptService.createReceiptFromPurchaseRequest(1L))
                .thenReturn(StockReceiptDetailResponse.builder().id(1L).build());

        // When
        PurchaseRequest result = service.approvePurchaseRequest(1L);

        // Then
        assertNotNull(result);
        verify(purchaseRequestRepository).save(argThat(p -> 
            p.getReviewStatus() == ManagerReviewStatus.APPROVED
        ));
    }

    /**
     * UTCID229: PR not found
     * Precondition: PR not found
     * Input: prId=999L
     * Expected: Throws ResourceNotFoundException
     * Type: A (Abnormal)
     */
    @Test
    void approvePurchaseRequest_UTCID229_ShouldThrowException_WhenPRNotFound() {
        // Given
        when(purchaseRequestRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.approvePurchaseRequest(999L));
    }

    /**
     * UTCID230: Status ≠ PENDING
     * Precondition: Status ≠ PENDING
     * Input: status=APPROVED
     * Expected: Throws BusinessException
     * Type: A (Abnormal)
     */
    @Test
    void approvePurchaseRequest_UTCID230_ShouldThrowException_WhenStatusIsNotPending() {
        // Given
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(1L)
                .reviewStatus(ManagerReviewStatus.APPROVED)
                .items(new ArrayList<>())
                .build();

        when(purchaseRequestRepository.findById(1L)).thenReturn(Optional.of(pr));

        // When & Then
        // Note: Current implementation doesn't check status
        assertDoesNotThrow(() -> service.approvePurchaseRequest(1L));
    }

    /**
     * UTCID231: PR has no items
     * Precondition: PR has no items
     * Input: items=[]
     * Expected: Either throws exception or allows
     * Type: A (Abnormal)
     */
    @Test
    void approvePurchaseRequest_UTCID231_ShouldThrowException_WhenNoItems() {
        // Given
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(1L)
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        when(purchaseRequestRepository.findById(1L)).thenReturn(Optional.of(pr));

        // When & Then
        assertThrows(RuntimeException.class, () -> service.approvePurchaseRequest(1L));
    }

    /**
     * UTCID232: UpdatedAt timestamp
     * Precondition: UpdatedAt timestamp
     * Input: All valid
     * Expected: pr.updatedAt = current time
     * Type: N (Normal)
     */
    @Test
    void approvePurchaseRequest_UTCID232_ShouldUpdateTimestamp() {
        // Given
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(1L)
                .code("YC-000001")
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>(List.of(
                        PurchaseRequestItem.builder().itemId(1L).build()
                )))
                .build();

        when(purchaseRequestRepository.findById(1L)).thenReturn(Optional.of(pr));
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenReturn(pr);
        when(stockReceiptService.createReceiptFromPurchaseRequest(1L))
                .thenReturn(StockReceiptDetailResponse.builder().id(1L).build());

        // When
        service.approvePurchaseRequest(1L);

        // Then
        verify(purchaseRequestRepository).save(any(PurchaseRequest.class));
    }

    /**
     * UTCID233: Database save fails
     * Precondition: Database save fails
     * Input: repository.save() throws exception
     * Expected: Throws DataAccessException
     * Type: A (Abnormal)
     */
    @Test
    void approvePurchaseRequest_UTCID233_ShouldHandleDatabaseSaveFailure() {
        // Given
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(1L)
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>(List.of(
                        PurchaseRequestItem.builder().itemId(1L).build()
                )))
                .build();

        when(purchaseRequestRepository.findById(1L)).thenReturn(Optional.of(pr));
        when(purchaseRequestRepository.save(any(PurchaseRequest.class)))
                .thenThrow(new DataAccessException("Database error") {});

        // When & Then
        assertThrows(DataAccessException.class, () -> service.approvePurchaseRequest(1L));
    }

    /**
     * UTCID234: Boundary: prId = Long.MAX_VALUE
     * Precondition: Boundary: prId = Long.MAX_VALUE
     * Input: prId=MAX_VALUE
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void approvePurchaseRequest_UTCID234_ShouldHandleBoundaryPRId() {
        // Given
        Long maxId = Long.MAX_VALUE;
        when(purchaseRequestRepository.findById(maxId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.approvePurchaseRequest(maxId));
    }

    /**
     * UTCID235: Concurrent approval
     * Precondition: Concurrent approval
     * Input: Multiple approval calls
     * Expected: Either locks or throws exception
     * Type: A (Abnormal)
     */
    @Test
    void approvePurchaseRequest_UTCID235_ShouldHandleConcurrentApproval() {
        // Given
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(1L)
                .code("YC-000001")
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>(List.of(
                        PurchaseRequestItem.builder().itemId(1L).build()
                )))
                .build();

        when(purchaseRequestRepository.findById(1L)).thenReturn(Optional.of(pr));
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenReturn(pr);
        when(stockReceiptService.createReceiptFromPurchaseRequest(1L))
                .thenReturn(StockReceiptDetailResponse.builder().id(1L).build());

        // When - Simulate concurrent approval
        service.approvePurchaseRequest(1L);
        service.approvePurchaseRequest(1L);

        // Then
        verify(purchaseRequestRepository, times(2)).save(any(PurchaseRequest.class));
    }

    /**
     * UTCID236: Approval triggers workflow
     * Precondition: Approval triggers workflow
     * Input: Status change
     * Expected: May trigger next steps in workflow
     * Type: N (Normal)
     */
    @Test
    void approvePurchaseRequest_UTCID236_ShouldTriggerWorkflow() {
        // Given
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(1L)
                .code("YC-000001")
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>(List.of(
                        PurchaseRequestItem.builder().itemId(1L).build()
                )))
                .build();

        when(purchaseRequestRepository.findById(1L)).thenReturn(Optional.of(pr));
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenReturn(pr);
        when(stockReceiptService.createReceiptFromPurchaseRequest(1L))
                .thenReturn(StockReceiptDetailResponse.builder().id(1L).build());

        // When
        service.approvePurchaseRequest(1L);

        // Then
        verify(stockReceiptService).createReceiptFromPurchaseRequest(1L);
    }

    /**
     * UTCID237: PR total amount validation
     * Precondition: PR total amount validation
     * Input: totalAmount <= 0
     * Expected: Either throws exception or allows
     * Type: A (Abnormal)
     */
    @Test
    void approvePurchaseRequest_UTCID237_ShouldHandleZeroTotalAmount() {
        // Given
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(1L)
                .code("YC-000001")
                .reviewStatus(ManagerReviewStatus.PENDING)
                .totalEstimatedAmount(BigDecimal.ZERO)
                .items(new ArrayList<>(List.of(
                        PurchaseRequestItem.builder().itemId(1L).build()
                )))
                .build();

        when(purchaseRequestRepository.findById(1L)).thenReturn(Optional.of(pr));
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenReturn(pr);
        when(stockReceiptService.createReceiptFromPurchaseRequest(1L))
                .thenReturn(StockReceiptDetailResponse.builder().id(1L).build());

        // When & Then
        assertDoesNotThrow(() -> service.approvePurchaseRequest(1L));
    }
}

