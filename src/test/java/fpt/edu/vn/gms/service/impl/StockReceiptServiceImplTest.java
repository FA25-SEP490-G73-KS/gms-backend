package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.*;
import fpt.edu.vn.gms.dto.request.StockReceiveRequest;
import fpt.edu.vn.gms.dto.response.StockReceiptItemResponseDto;
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
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StockReceiptServiceImplTest extends BaseServiceTest {

  @Mock
  private StockReceiptRepository stockReceiptRepo;
  @Mock
  private StockReceiptItemRepository stockReceiptItemRepo;
  @Mock
  private PurchaseRequestRepository purchaseRequestRepo;
  @Mock
  private PurchaseRequestItemRepository purchaseRequestItemRepo;
  @Mock
  private PartRepository partRepository;
  @Mock
  private PriceQuotationItemRepository quotationItemRepo;
  @Mock
  private AccountRepository accountRepository;
  @Mock
  private NotificationService notificationService;
  @Mock
  private CodeSequenceService codeSequenceService;
  @Mock
  private FileStorageService fileStorageService;
  @Mock
  private StockReceiptItemMapper stockReceiptItemMapper;
  @Mock
  private StockReceiptMapper stockReceiptMapper;

  @InjectMocks
  private StockReceiptServiceImpl stockReceiptServiceImpl;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void receiveItem_WhenValidRequest_ShouldCreateReceiptAndReturnDto() {
    Employee employee = getMockEmployee(Role.WAREHOUSE);
    PurchaseRequest pr = PurchaseRequest.builder().id(1L).items(new ArrayList<>()).build();
    Part part = Part.builder().partId(2L).quantityInStock(5.0).reservedQuantity(2.0).build();
    PurchaseRequestItem prItem = PurchaseRequestItem.builder()
        .purchaseRequest(pr)
        .itemId(3L)
        .quantity(10.0)
        .quantityReceived(2.0)
        .part(part)
        .status(PurchaseReqItemStatus.PENDING)
        .build();
    pr.setItems(List.of(prItem));
    StockReceiveRequest req = StockReceiveRequest.builder()
        .quantityReceived(3.0)
        .note("Nhập thêm")
        .build();
    MultipartFile file = mock(MultipartFile.class);
    StockReceipt receipt = StockReceipt.builder().receiptId(4L).purchaseRequest(pr).status(StockReceiptStatus.CREATED)
        .totalAmount(BigDecimal.ZERO).build();
    StockReceiptItem receiptItem = StockReceiptItem.builder().stockReceipt(receipt).purchaseRequestItem(prItem)
        .quantityReceived(3.0).build();
    StockReceiptItemResponseDto dto = StockReceiptItemResponseDto.builder().receiptItemId(5L).build();

    when(purchaseRequestItemRepo.findById(3L)).thenReturn(Optional.of(prItem));
    when(fileStorageService.upload(file)).thenReturn("fileUrl");
    when(stockReceiptRepo.findByPurchaseRequest(pr)).thenReturn(Optional.of(receipt));
    when(stockReceiptItemRepo.save(any(StockReceiptItem.class))).thenReturn(receiptItem);
    when(partRepository.save(any(Part.class))).thenReturn(part);
    when(purchaseRequestItemRepo.save(any(PurchaseRequestItem.class))).thenReturn(prItem);
    when(purchaseRequestRepo.save(any(PurchaseRequest.class))).thenReturn(pr);
    when(stockReceiptItemMapper.toDto(receiptItem)).thenReturn(dto);

    StockReceiptItemResponseDto result = stockReceiptServiceImpl.receiveItem(3L, req, file, employee);

    assertNotNull(result);
    assertEquals(5L, result.getReceiptItemId());
    verify(stockReceiptItemRepo).save(any(StockReceiptItem.class));
    verify(partRepository).save(part);
    verify(purchaseRequestItemRepo).save(prItem);
    verify(purchaseRequestRepo).save(pr);
    verify(notificationService, atLeastOnce()).createNotification(any(), any(), any(), any(), any(), any());
  }

  @Test
  void receiveItem_WhenPurchaseRequestItemNotFound_ShouldThrowResourceNotFoundException() {
    when(purchaseRequestItemRepo.findById(99L)).thenReturn(Optional.empty());
    StockReceiveRequest req = StockReceiveRequest.builder().quantityReceived(1.0).build();
    MultipartFile file = mock(MultipartFile.class);
    Employee employee = getMockEmployee(Role.WAREHOUSE);

    assertThrows(ResourceNotFoundException.class, () -> stockReceiptServiceImpl.receiveItem(99L, req, file, employee));
  }

  @Test
  void receiveItem_WhenQuantityReceivedIsZero_ShouldThrowIllegalArgumentException() {
    Employee employee = getMockEmployee(Role.WAREHOUSE);
    PurchaseRequest pr = PurchaseRequest.builder().id(1L).build();
    PurchaseRequestItem prItem = PurchaseRequestItem.builder()
        .purchaseRequest(pr)
        .itemId(3L)
        .quantity(10.0)
        .quantityReceived(2.0)
        .build();
    when(purchaseRequestItemRepo.findById(3L)).thenReturn(Optional.of(prItem));
    StockReceiveRequest req = StockReceiveRequest.builder().quantityReceived(0.0).build();
    MultipartFile file = mock(MultipartFile.class);

    assertThrows(IllegalArgumentException.class, () -> stockReceiptServiceImpl.receiveItem(3L, req, file, employee));
  }

  @Test
  void receiveItem_WhenQuantityReceivedExceedsRemaining_ShouldThrowIllegalArgumentException() {
    Employee employee = getMockEmployee(Role.WAREHOUSE);
    PurchaseRequest pr = PurchaseRequest.builder().id(1L).build();
    PurchaseRequestItem prItem = PurchaseRequestItem.builder()
        .purchaseRequest(pr)
        .itemId(3L)
        .quantity(5.0)
        .quantityReceived(4.0)
        .build();
    when(purchaseRequestItemRepo.findById(3L)).thenReturn(Optional.of(prItem));
    StockReceiveRequest req = StockReceiveRequest.builder().quantityReceived(2.0).build();
    MultipartFile file = mock(MultipartFile.class);

    assertThrows(IllegalArgumentException.class, () -> stockReceiptServiceImpl.receiveItem(3L, req, file, employee));
  }

  @Test
  void getReceiptsForAccounting_WhenReceiptsExist_ShouldReturnPagedDtos() {
    StockReceipt receipt = StockReceipt.builder().receiptId(1L).build();
    StockReceiptResponseDto dto = StockReceiptResponseDto.builder().receiptId(1L).build();
    Page<StockReceipt> page = new PageImpl<>(List.of(receipt));
    when(stockReceiptRepo.searchForAccounting(anyString(), any(Pageable.class))).thenReturn(page);
    when(stockReceiptMapper.toDto(receipt)).thenReturn(dto);

    Page<StockReceiptResponseDto> result = stockReceiptServiceImpl.getReceiptsForAccounting(0, 10, "search");

    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getReceiptId());
  }

  @Test
  void getReceiptsForAccounting_WhenNoReceiptsExist_ShouldReturnEmptyPage() {
    Page<StockReceipt> page = new PageImpl<>(List.of());
    when(stockReceiptRepo.searchForAccounting(anyString(), any(Pageable.class))).thenReturn(page);

    Page<StockReceiptResponseDto> result = stockReceiptServiceImpl.getReceiptsForAccounting(0, 10, "search");

    assertTrue(result.isEmpty());
  }

  @Test
  void getReceiptItems_WhenReceiptExists_ShouldReturnItemDtos() {
    StockReceipt receipt = StockReceipt.builder().receiptId(1L).build();
    StockReceiptItem item1 = StockReceiptItem.builder().stockReceipt(receipt).id(2L).build();
    StockReceiptItem item2 = StockReceiptItem.builder().stockReceipt(receipt).id(3L).build();
    StockReceiptItemResponseDto dto1 = StockReceiptItemResponseDto.builder().receiptItemId(2L).build();
    StockReceiptItemResponseDto dto2 = StockReceiptItemResponseDto.builder().receiptItemId(3L).build();

    when(stockReceiptRepo.findById(1L)).thenReturn(Optional.of(receipt));
    when(stockReceiptItemRepo.findByStockReceipt(receipt)).thenReturn(List.of(item1, item2));
    when(stockReceiptItemMapper.toDtos(List.of(item1, item2))).thenReturn(List.of(dto1, dto2));

    List<StockReceiptItemResponseDto> result = stockReceiptServiceImpl.getReceiptItems(1L);

    assertEquals(2, result.size());
    assertEquals(2L, result.get(0).getReceiptItemId());
    assertEquals(3L, result.get(1).getReceiptItemId());
  }

  @Test
  void getReceiptItems_WhenReceiptNotFound_ShouldThrowResourceNotFoundException() {
    when(stockReceiptRepo.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> stockReceiptServiceImpl.getReceiptItems(99L));
  }
}
