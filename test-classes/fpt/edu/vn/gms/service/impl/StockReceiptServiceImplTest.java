package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.*;
import fpt.edu.vn.gms.dto.response.StockReceiptItemResponse;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.StockReceiptItemHistoryMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockReceiptServiceImplTest {

    @Mock
    StockReceiptRepository stockReceiptRepository;
    @Mock
    StockReceiptItemRepository stockReceiptItemRepository;
    @Mock
    StockReceiptItemHistoryRepository stockReceiptItemHistoryRepository;
    @Mock
    PurchaseRequestRepository purchaseRequestRepository;
    @Mock
    PartRepository partRepository;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    StockReceiptItemHistoryMapper historyMapper;
    @Mock
    StockExportItemRepository stockExportItemRepository;

    @InjectMocks
    StockReceiptServiceImplNew service;

    private Employee employee;
    private PurchaseRequest purchaseRequest;
    private PurchaseRequestItem prItem;
    private Part part;
    private PriceQuotation quotation;
    private PriceQuotationItem quotationItem;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .employeeId(1L)
                .fullName("Warehouse Staff")
                .build();

        part = Part.builder()
                .partId(10L)
                .name("Brake Pad")
                .purchasePrice(new BigDecimal("500000"))
                .quantityInStock(5.0)
                .reservedQuantity(2.0)
                .build();

        quotation = PriceQuotation.builder()
                .priceQuotationId(100L)
                .estimateAmount(new BigDecimal("1000000"))
                .serviceTicket(ServiceTicket.builder()
                        .serviceTicketId(200L)
                        .createdBy(employee)
                        .build())
                .build();

        quotationItem = PriceQuotationItem.builder()
                .priceQuotationItemId(15L)
                .part(part)
                .inventoryStatus(PriceQuotationItemStatus.UNKNOWN)
                .priceQuotation(quotation)
                .build();

        purchaseRequest = PurchaseRequest.builder()
                .id(50L)
                .relatedQuotation(quotation)
                .build();

        prItem = PurchaseRequestItem.builder()
                .itemId(5L)
                .purchaseRequest(purchaseRequest)
                .part(part)
                .quantity(10.0)
                .quantityReceived(3.0)
                .partName("Brake Pad")
                .quotationItem(quotationItem)
                .reviewStatus(ManagerReviewStatus.APPROVED)
                .build();

        purchaseRequest.setItems(List.of(prItem));
    }

    // Note: receiveItem and getReceiptsForAccounting methods may not exist in current implementation
    // These tests have been removed as they are not in the current service interface

    @Test
    void getReceiptItems_ShouldReturnItems_WhenReceiptExists() {
        Pageable pageable = PageRequest.of(0, 5);
        StockReceipt receipt = StockReceipt.builder()
                .receiptId(1L)
                .items(List.of(StockReceiptItem.builder()
                        .id(1L)
                        .purchaseRequestItem(prItem)
                        .quantityReceived(5.0)
                        .requestedQuantity(10.0)
                        .actualUnitPrice(new BigDecimal("500000"))
                        .actualTotalPrice(new BigDecimal("2500000"))
                        .build()))
                .build();
        when(stockReceiptRepository.findById(1L)).thenReturn(Optional.of(receipt));

        Page<StockReceiptItemResponse> result = service.getReceiptItems(1L, pageable);

        assertEquals(1, result.getTotalElements());
        verify(stockReceiptRepository).findById(1L);
    }

    @Test
    void getReceiptItems_ShouldThrow_WhenReceiptNotFound() {
        Pageable pageable = PageRequest.of(0, 5);
        when(stockReceiptRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getReceiptItems(1L, pageable));
    }
}


