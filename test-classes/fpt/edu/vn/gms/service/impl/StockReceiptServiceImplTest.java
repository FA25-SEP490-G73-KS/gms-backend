package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.*;
import fpt.edu.vn.gms.dto.request.StockReceiveRequest;
import fpt.edu.vn.gms.dto.response.StockReceiptItemResponse;
import fpt.edu.vn.gms.dto.response.StockReceiptResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.StockReceiptItemMapper;
import fpt.edu.vn.gms.mapper.StockReceiptMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockReceiptServiceImplTest {

    @Mock
    StockReceiptRepository stockReceiptRepo;
    @Mock
    StockReceiptItemRepository stockReceiptItemRepo;
    @Mock
    PurchaseRequestRepository purchaseRequestRepo;
    @Mock
    PurchaseRequestItemRepository purchaseRequestItemRepo;
    @Mock
    PartRepository partRepository;
    @Mock
    PriceQuotationItemRepository quotationItemRepo;
    @Mock
    AccountRepository accountRepository;
    @Mock
    NotificationService notificationService;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    FileStorageService fileStorageService;
    @Mock
    StockReceiptItemMapper stockReceiptItemMapper;
    @Mock
    StockReceiptMapper stockReceiptMapper;

    @InjectMocks
    StockReceiptServiceImpl service;

    @Mock
    MultipartFile multipartFile;

    @Captor
    ArgumentCaptor<StockReceipt> receiptCaptor;

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
                .exportStatus(ExportStatus.WAITING_TO_EXPORT)
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
                .status(PurchaseReqItemStatus.PENDING)
                .build();

        purchaseRequest.setItems(List.of(prItem));
    }

    @Test
    void receiveItem_ShouldCreateReceiptAndUpdateRelatedEntities() {
        StockReceiveRequest request = StockReceiveRequest.builder()
                .quantityReceived(5.0)
                .note("Nhập đủ")
                .build();

        when(purchaseRequestItemRepo.findById(5L)).thenReturn(Optional.of(prItem));
        when(fileStorageService.upload(multipartFile)).thenReturn("http://file.url");
        when(stockReceiptRepo.findByPurchaseRequest(purchaseRequest))
                .thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("SR")).thenReturn("SR001");

        StockReceipt savedReceipt = StockReceipt.builder()
                .receiptId(99L)
                .purchaseRequest(purchaseRequest)
                .code("SR001")
                .createdBy(employee)
                .totalAmount(BigDecimal.ZERO)
                .status(StockReceiptStatus.CREATED)
                .build();
        when(stockReceiptRepo.save(any(StockReceipt.class))).thenReturn(savedReceipt);

        StockReceiptItem receiptItem = StockReceiptItem.builder()
                .stockReceipt(savedReceipt)
                .purchaseRequestItem(prItem)
                .quantityReceived(5.0)
                .requestedQuantity(10.0)
                .receivedById(employee.getEmployeeId())
                .receivedByName(employee.getFullName())
                .actualUnitPrice(part.getPurchasePrice())
                .actualTotalPrice(part.getPurchasePrice().multiply(BigDecimal.valueOf(5.0)))
                .build();
        when(stockReceiptItemRepo.save(any(StockReceiptItem.class))).thenReturn(receiptItem);

        StockReceiptItemResponse dto = StockReceiptItemResponse.builder().build();
        when(stockReceiptItemMapper.toDto(receiptItem)).thenReturn(dto);

        StockReceiptItemResponse result =
                service.receiveItem(5L, request, multipartFile, employee);

        assertSame(dto, result);

        // Part stock updated
        verify(partRepository).save(argThat(p -> {
            assertEquals(5.0 + 5.0, p.getQuantityInStock());
            assertEquals(2.0 + 5.0, p.getReservedQuantity());
            return true;
        }));

        // PurchaseRequestItem updated
        verify(purchaseRequestItemRepo).save(argThat(item -> {
            assertEquals(8.0, item.getQuantityReceived());
            assertEquals(PurchaseReqItemStatus.PENDING, item.getStatus());
            return true;
        }));

        // PurchaseRequest status updated
        verify(purchaseRequestRepo).save(argThat(pr -> {
            assertEquals(PurchaseRequestStatus.PENDING, pr.getStatus());
            return true;
        }));

        // Quotation item inventory updated
        verify(quotationItemRepo, never()).save(any());

        // Notification calls
        verify(notificationService, atLeastOnce()).createNotification(
                anyLong(), anyString(), anyString(), any(), anyString(), anyString());
    }

    @Test
    void receiveItem_ShouldThrow_WhenQuantityNonPositiveOrExceedRemaining() {
        StockReceiveRequest badReq1 = StockReceiveRequest.builder()
                .quantityReceived(0.0)
                .build();
        when(purchaseRequestItemRepo.findById(5L)).thenReturn(Optional.of(prItem));

        assertThrows(IllegalArgumentException.class,
                () -> service.receiveItem(5L, badReq1, multipartFile, employee));

        StockReceiveRequest badReq2 = StockReceiveRequest.builder()
                .quantityReceived(20.0)
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> service.receiveItem(5L, badReq2, multipartFile, employee));
    }

    @Test
    void receiveItem_ShouldThrow_WhenPrItemNotFound() {
        when(purchaseRequestItemRepo.findById(999L)).thenReturn(Optional.empty());
        StockReceiveRequest req = StockReceiveRequest.builder()
                .quantityReceived(1.0)
                .build();

        assertThrows(ResourceNotFoundException.class,
                () -> service.receiveItem(999L, req, multipartFile, employee));
    }

    @Test
    void getReceiptsForAccounting_ShouldMapPageToDto() {
        Pageable pageable = PageRequest.of(0, 5);
        StockReceipt receipt = StockReceipt.builder()
                .receiptId(1L)
                .code("SR001")
                .build();
        Page<StockReceipt> page = new PageImpl<>(List.of(receipt), pageable, 1);

        when(stockReceiptRepo.searchForAccounting("SR", pageable)).thenReturn(page);

        StockReceiptResponseDto dto = StockReceiptResponseDto.builder().build();
        when(stockReceiptMapper.toDto(receipt)).thenReturn(dto);

        Page<StockReceiptResponseDto> result = service.getReceiptsForAccounting(0, 5, "SR");

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(stockReceiptRepo).searchForAccounting("SR", pageable);
    }

    @Test
    void getReceiptItems_ShouldReturnItems_WhenReceiptExists() {
        StockReceipt receipt = StockReceipt.builder()
                .receiptId(1L).build();
        when(stockReceiptRepo.findById(1L)).thenReturn(Optional.of(receipt));

        List<StockReceiptItem> items = List.of(StockReceiptItem.builder().build());
        when(stockReceiptItemRepo.findByStockReceipt(receipt)).thenReturn(items);

        StockReceiptItemResponse dto = StockReceiptItemResponse.builder().build();
        when(stockReceiptItemMapper.toDtos(items)).thenReturn(List.of(dto));

        List<StockReceiptItemResponse> result = service.getReceiptItems(1L);

        assertEquals(1, result.size());
        assertSame(dto, result.get(0));

        verify(stockReceiptRepo).findById(1L);
        verify(stockReceiptItemRepo).findByStockReceipt(receipt);
    }

    @Test
    void getReceiptItems_ShouldThrow_WhenReceiptNotFound() {
        when(stockReceiptRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getReceiptItems(1L));
    }
}


