package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.LedgerVoucherCategory;
import fpt.edu.vn.gms.common.enums.ManualVoucherStatus;
import fpt.edu.vn.gms.common.enums.ManualVoucherType;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.request.ExpenseVoucherCreateRequest;
import fpt.edu.vn.gms.dto.request.ManualVoucherCreateRequest;
import fpt.edu.vn.gms.dto.response.ManualVoucherListResponseDto;
import fpt.edu.vn.gms.dto.response.ManualVoucherResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.LedgerVoucher;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import fpt.edu.vn.gms.entity.StockReceiptItem;
import fpt.edu.vn.gms.entity.Supplier;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ManualVoucherMapper;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.repository.ManualVoucherRepository;
import fpt.edu.vn.gms.repository.StockReceiptItemRepository;
import fpt.edu.vn.gms.repository.SupplierRepository;
import fpt.edu.vn.gms.service.CodeSequenceService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ManualVoucherServiceImplTest extends BaseServiceTest {

  @Mock
  private ManualVoucherRepository manualRepo;
  @Mock
  private StockReceiptItemRepository stockReceiptItemRepo;
  @Mock
  private EmployeeRepository employeeRepo;
  @Mock
  private SupplierRepository supplierRepo;
  @Mock
  private ManualVoucherMapper manualVoucherMapper;
  @Mock
  private CodeSequenceService codeSequenceService;
  @Mock
  private FileStorageService fileStorageService;

  @InjectMocks
  private ManualVoucherServiceImpl manualVoucherServiceImpl;

  @Test
  void payForStockReceiptItem_WhenActualUnitPriceExists_ShouldCreateVoucherAndMarkPaid() {
    Employee accountant = getMockEmployee(Role.ACCOUNTANT);
    ExpenseVoucherCreateRequest req = ExpenseVoucherCreateRequest.builder()
        .supplierId(10L)
        .attachmentUrl("url")
        .build();

    PurchaseRequestItem prItem = PurchaseRequestItem.builder()
        .estimatedPurchasePrice(BigDecimal.valueOf(1000))
        .quantity(10D)
        .build();

    StockReceiptItem item = StockReceiptItem.builder()
        .id(1L)
        .actualUnitPrice(BigDecimal.valueOf(200))
        .quantityReceived(5D)
        .purchaseRequestItem(prItem)
        .paid(false)
        .build();

    LedgerVoucher voucher = LedgerVoucher.builder()
        .code("PAY-2024-000001")
        .type(ManualVoucherType.PAYMENT)
        .status(ManualVoucherStatus.APPROVED)
        .amount(BigDecimal.valueOf(1000))
        .relatedSupplierId(10L)
        .attachmentUrl("url")
        .createdBy(accountant)
        .createdAt(LocalDateTime.now())
        .build();

    ManualVoucherResponseDto dto = ManualVoucherResponseDto.builder().code("PAY-2024-000001").build();

    when(stockReceiptItemRepo.findById(1L)).thenReturn(Optional.of(item));
    when(codeSequenceService.generateCode("PAY")).thenReturn("PAY-2024-000001");
    when(manualRepo.save(any(LedgerVoucher.class))).thenReturn(voucher);
    when(stockReceiptItemRepo.save(any(StockReceiptItem.class))).thenReturn(item);
    when(manualVoucherMapper.toDto(any(LedgerVoucher.class))).thenReturn(dto);

    ManualVoucherResponseDto result = manualVoucherServiceImpl.payForStockReceiptItem(1L, req, accountant);

    assertNotNull(result);
    assertEquals("PAY-2024-000001", result.getCode());
    verify(manualRepo).save(any(LedgerVoucher.class));
    verify(stockReceiptItemRepo).save(item);
  }

  @Test
  void payForStockReceiptItem_WhenActualUnitPriceNull_ShouldCalculateFromEstimatedPrice() {
    Employee accountant = getMockEmployee(Role.ACCOUNTANT);
    ExpenseVoucherCreateRequest req = ExpenseVoucherCreateRequest.builder()
        .supplierId(10L)
        .attachmentUrl("url")
        .build();

    PurchaseRequestItem prItem = PurchaseRequestItem.builder()
        .estimatedPurchasePrice(BigDecimal.valueOf(1000))
        .quantity(10D)
        .build();

    StockReceiptItem item = StockReceiptItem.builder()
        .id(1L)
        .actualUnitPrice(null)
        .quantityReceived(2D)
        .purchaseRequestItem(prItem)
        .paid(false)
        .build();

    LedgerVoucher voucher = LedgerVoucher.builder().code("PAY-2024-000002").build();
    ManualVoucherResponseDto dto = ManualVoucherResponseDto.builder().code("PAY-2024-000002").build();

    when(stockReceiptItemRepo.findById(1L)).thenReturn(Optional.of(item));
    when(codeSequenceService.generateCode("PAY")).thenReturn("PAY-2024-000002");
    when(manualRepo.save(any(LedgerVoucher.class))).thenReturn(voucher);
    when(stockReceiptItemRepo.save(any(StockReceiptItem.class))).thenReturn(item);
    when(manualVoucherMapper.toDto(any(LedgerVoucher.class))).thenReturn(dto);

    ManualVoucherResponseDto result = manualVoucherServiceImpl.payForStockReceiptItem(1L, req, accountant);

    assertNotNull(result);
    assertEquals("PAY-2024-000002", result.getCode());
  }

  @Test
  void payForStockReceiptItem_WhenStockReceiptItemNotFound_ShouldThrowResourceNotFoundException() {
    when(stockReceiptItemRepo.findById(99L)).thenReturn(Optional.empty());
    ExpenseVoucherCreateRequest req = ExpenseVoucherCreateRequest.builder().build();
    Employee accountant = getMockEmployee(Role.ACCOUNTANT);

    assertThrows(ResourceNotFoundException.class,
        () -> manualVoucherServiceImpl.payForStockReceiptItem(99L, req, accountant));
  }

  @Test
  void create_WhenFileProvided_ShouldUploadAndSaveVoucher() {
    ManualVoucherCreateRequest req = ManualVoucherCreateRequest.builder()
        .type(ManualVoucherType.PAYMENT)
        .category(LedgerVoucherCategory.SALARY_PAYMENT)
        .amount(BigDecimal.valueOf(500000))
        .description("desc")
        .approvedByEmployeeId(2L)
        .build();
    Employee creator = getMockEmployee(Role.ACCOUNTANT);
    MultipartFile file = mock(MultipartFile.class);

    when(file.isEmpty()).thenReturn(false);
    when(fileStorageService.upload(file)).thenReturn("fileUrl");
    Employee approver = getMockEmployee(Role.ACCOUNTANT);
    approver.setEmployeeId(2L);
    when(employeeRepo.findById(2L)).thenReturn(Optional.of(approver));
    when(codeSequenceService.generateCode("PAY")).thenReturn("PAY-2024-000003");
    LedgerVoucher voucher = LedgerVoucher.builder().code("PAY-2024-000003").build();
    when(manualRepo.save(any(LedgerVoucher.class))).thenReturn(voucher);
    ManualVoucherResponseDto dto = ManualVoucherResponseDto.builder().code("PAY-2024-000003").build();
    when(manualVoucherMapper.toDto(any(LedgerVoucher.class))).thenReturn(dto);

    ManualVoucherResponseDto result = manualVoucherServiceImpl.create(req, file, creator);

    assertNotNull(result);
    assertEquals("PAY-2024-000003", result.getCode());
    verify(fileStorageService).upload(file);
    verify(manualRepo).save(any(LedgerVoucher.class));
  }

  @Test
  void create_WhenFileNull_ShouldSaveVoucherWithoutUpload() {
    ManualVoucherCreateRequest req = ManualVoucherCreateRequest.builder()
        .type(ManualVoucherType.PAYMENT)
        .category(LedgerVoucherCategory.SALARY_PAYMENT)
        .amount(BigDecimal.valueOf(500000))
        .description("desc")
        .approvedByEmployeeId(2L)
        .build();
    Employee creator = getMockEmployee(Role.ACCOUNTANT);
    Employee approver = getMockEmployee(Role.ACCOUNTANT);
    approver.setEmployeeId(2L);

    when(employeeRepo.findById(2L)).thenReturn(Optional.of(approver));
    when(codeSequenceService.generateCode("PAY")).thenReturn("PAY-2024-000004");
    LedgerVoucher voucher = LedgerVoucher.builder().code("PAY-2024-000004").build();
    when(manualRepo.save(any(LedgerVoucher.class))).thenReturn(voucher);
    ManualVoucherResponseDto dto = ManualVoucherResponseDto.builder().code("PAY-2024-000004").build();
    when(manualVoucherMapper.toDto(any(LedgerVoucher.class))).thenReturn(dto);

    ManualVoucherResponseDto result = manualVoucherServiceImpl.create(req, null, creator);

    assertNotNull(result);
    assertEquals("PAY-2024-000004", result.getCode());
    verify(manualRepo).save(any(LedgerVoucher.class));
  }

  @Test
  void create_WhenApproverNotFound_ShouldThrowResourceNotFoundException() {
    ManualVoucherCreateRequest req = ManualVoucherCreateRequest.builder()
        .type(ManualVoucherType.PAYMENT)
        .approvedByEmployeeId(99L)
        .build();
    Employee creator = getMockEmployee(Role.ACCOUNTANT);
    when(employeeRepo.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> manualVoucherServiceImpl.create(req, null, creator));
  }

  @Test
  void getList_WhenCalled_ShouldReturnPagedManualVoucherListResponseDto() {
    LedgerVoucher voucher = LedgerVoucher.builder().id(1L).build();
    ManualVoucherListResponseDto dto = ManualVoucherListResponseDto.builder().id(1L).build();
    Page<LedgerVoucher> page = new PageImpl<>(java.util.List.of(voucher));
    when(manualRepo.findAll(any(Pageable.class))).thenReturn(page);
    when(manualVoucherMapper.toListDto(voucher)).thenReturn(dto);

    Page<ManualVoucherListResponseDto> result = manualVoucherServiceImpl.getList(0, 10);

    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getId());
  }

  @Test
  void getDetail_WhenVoucherExists_ShouldReturnManualVoucherResponseDto() {
    LedgerVoucher voucher = LedgerVoucher.builder().id(1L).build();
    ManualVoucherResponseDto dto = ManualVoucherResponseDto.builder().code("PAY-2024-000005").build();
    when(manualRepo.findById(1L)).thenReturn(Optional.of(voucher));
    when(manualVoucherMapper.toDto(voucher)).thenReturn(dto);

    ManualVoucherResponseDto result = manualVoucherServiceImpl.getDetail(1L);

    assertNotNull(result);
    assertEquals("PAY-2024-000005", result.getCode());
  }

  @Test
  void getDetail_WhenVoucherNotFound_ShouldThrowResourceNotFoundException() {
    when(manualRepo.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> manualVoucherServiceImpl.getDetail(99L));
  }

  @Test
  void resolveTargetName_WhenRelatedEmployeeIdExists_ShouldReturnEmployeeName() {
    LedgerVoucher voucher = LedgerVoucher.builder().relatedEmployeeId(1L).build();
    Employee emp = getMockEmployee(Role.SERVICE_ADVISOR);
    emp.setFullName("Emp Name");
    when(employeeRepo.findById(1L)).thenReturn(Optional.of(emp));

    String result = invokeResolveTargetName(voucher);

    assertEquals("Emp Name", result);
  }

  @Test
  void resolveTargetName_WhenRelatedSupplierIdExists_ShouldReturnSupplierName() {
    LedgerVoucher voucher = LedgerVoucher.builder().relatedSupplierId(2L).build();
    Supplier supplier = Supplier.builder().name("Supplier X").build();
    when(supplierRepo.findById(2L)).thenReturn(Optional.of(supplier));

    String result = invokeResolveTargetName(voucher);

    assertEquals("Supplier X", result);
  }

  @Test
  void resolveTargetName_WhenNoRelation_ShouldReturnUnknown() {
    LedgerVoucher voucher = LedgerVoucher.builder().build();
    String result = invokeResolveTargetName(voucher);
    assertEquals("Không xác định", result);
  }

  // Helper to access private method
  private String invokeResolveTargetName(LedgerVoucher voucher) {
    try {
      java.lang.reflect.Method m = ManualVoucherServiceImpl.class.getDeclaredMethod("resolveTargetName",
          LedgerVoucher.class);
      m.setAccessible(true);
      return (String) m.invoke(manualVoucherServiceImpl, voucher);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
