package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.LedgerVoucherCategory;
import fpt.edu.vn.gms.common.enums.ManualVoucherStatus;
import fpt.edu.vn.gms.common.enums.ManualVoucherType;
import fpt.edu.vn.gms.dto.request.ExpenseVoucherCreateRequest;
import fpt.edu.vn.gms.dto.request.ManualVoucherCreateRequest;
import fpt.edu.vn.gms.dto.response.ManualVoucherListResponseDto;
import fpt.edu.vn.gms.dto.response.ManualVoucherResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ManualVoucherMapper;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.repository.ManualVoucherRepository;
import fpt.edu.vn.gms.repository.StockReceiptItemRepository;
import fpt.edu.vn.gms.repository.SupplierRepository;
import fpt.edu.vn.gms.service.CodeSequenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManualVoucherServiceImplTest {

    @Mock
    ManualVoucherRepository manualRepo;
    @Mock
    StockReceiptItemRepository stockReceiptItemRepo;
    @Mock
    EmployeeRepository employeeRepo;
    @Mock
    SupplierRepository supplierRepo;
    @Mock
    ManualVoucherMapper manualVoucherMapper;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    FileStorageService fileStorageService;

    @InjectMocks
    ManualVoucherServiceImpl service;

    @Test
    void payForStockReceiptItem_ShouldCreateVoucherAndMarkPaid_WhenItemHasActualUnitPrice() {
        Employee accountant = Employee.builder()
                .employeeId(1L)
                .fullName("Accountant")
                .build();

        PurchaseRequestItem prItem = PurchaseRequestItem.builder()
                .itemId(10L)
                .quantity(5.0)
                .estimatedPurchasePrice(new BigDecimal("1000000"))
                .build();

        StockReceiptItem item = StockReceiptItem.builder()
                .id(100L)
                .purchaseRequestItem(prItem)
                .quantityReceived(3.0)
                .actualUnitPrice(new BigDecimal("200000"))
                .paid(false)
                .build();

        when(stockReceiptItemRepo.findById(100L)).thenReturn(Optional.of(item));
        when(codeSequenceService.generateCode("PAY")).thenReturn("PAY-2025-00001");

        ExpenseVoucherCreateRequest request = ExpenseVoucherCreateRequest.builder()
                .supplierId(5L)
                .attachmentUrl("http://file.url")
                .build();

        LedgerVoucher savedVoucher = LedgerVoucher.builder()
                .id(200L)
                .code("PAY-2025-00001")
                .type(ManualVoucherType.PAYMENT)
                .status(ManualVoucherStatus.APPROVED)
                .amount(new BigDecimal("600000"))
                .relatedSupplierId(5L)
                .createdBy(accountant)
                .createdAt(LocalDateTime.now())
                .build();
        when(manualRepo.save(any(LedgerVoucher.class))).thenReturn(savedVoucher);

        ManualVoucherResponseDto dto = ManualVoucherResponseDto.builder()
                .id(200L)
                .code("PAY-2025-00001")
                .amount(new BigDecimal("600000"))
                .build();
        when(manualVoucherMapper.toDto(savedVoucher)).thenReturn(dto);

        ManualVoucherResponseDto result = service.payForStockReceiptItem(100L, request, accountant);

        assertSame(dto, result);
        assertTrue(item.getPaid());
        verify(stockReceiptItemRepo).findById(100L);
        verify(codeSequenceService).generateCode("PAY");
        verify(manualRepo).save(any(LedgerVoucher.class));
        verify(stockReceiptItemRepo).save(item);
    }

    @Test
    void payForStockReceiptItem_ShouldCalculateUnitPriceFromPrItem_WhenActualUnitPriceIsNull() {
        Employee accountant = Employee.builder()
                .employeeId(1L)
                .fullName("Accountant")
                .build();

        PurchaseRequestItem prItem = PurchaseRequestItem.builder()
                .itemId(10L)
                .quantity(5.0)
                .estimatedPurchasePrice(new BigDecimal("1000000"))
                .build();

        StockReceiptItem item = StockReceiptItem.builder()
                .id(100L)
                .purchaseRequestItem(prItem)
                .quantityReceived(2.0)
                .actualUnitPrice(null)
                .paid(false)
                .build();

        when(stockReceiptItemRepo.findById(100L)).thenReturn(Optional.of(item));
        when(codeSequenceService.generateCode("PAY")).thenReturn("PAY-2025-00001");

        ExpenseVoucherCreateRequest request = ExpenseVoucherCreateRequest.builder()
                .supplierId(5L)
                .build();

        LedgerVoucher savedVoucher = LedgerVoucher.builder()
                .id(200L)
                .code("PAY-2025-00001")
                .amount(new BigDecimal("400000"))
                .build();
        when(manualRepo.save(any(LedgerVoucher.class))).thenReturn(savedVoucher);

        ManualVoucherResponseDto dto = ManualVoucherResponseDto.builder()
                .id(200L)
                .amount(new BigDecimal("400000"))
                .build();
        when(manualVoucherMapper.toDto(savedVoucher)).thenReturn(dto);

        ManualVoucherResponseDto result = service.payForStockReceiptItem(100L, request, accountant);

        assertSame(dto, result);
        verify(manualRepo).save(argThat(v -> {
            BigDecimal expected = new BigDecimal("1000000")
                    .divide(BigDecimal.valueOf(5.0))
                    .multiply(BigDecimal.valueOf(2.0));
            return expected.compareTo(v.getAmount()) == 0;
        }));
    }

    @Test
    void payForStockReceiptItem_ShouldThrow_WhenItemNotFound() {
        when(stockReceiptItemRepo.findById(100L)).thenReturn(Optional.empty());

        ExpenseVoucherCreateRequest request = ExpenseVoucherCreateRequest.builder().build();
        Employee accountant = Employee.builder().employeeId(1L).build();

        assertThrows(ResourceNotFoundException.class,
                () -> service.payForStockReceiptItem(100L, request, accountant));
        verify(stockReceiptItemRepo).findById(100L);
        verify(manualRepo, never()).save(any());
    }

    @Test
    void payForStockReceiptItem_ShouldThrow_WhenCannotCalculateUnitPrice() {
        PurchaseRequestItem prItem = PurchaseRequestItem.builder()
                .itemId(10L)
                .quantity(null)
                .estimatedPurchasePrice(null)
                .build();

        StockReceiptItem item = StockReceiptItem.builder()
                .id(100L)
                .purchaseRequestItem(prItem)
                .actualUnitPrice(null)
                .build();

        when(stockReceiptItemRepo.findById(100L)).thenReturn(Optional.of(item));

        ExpenseVoucherCreateRequest request = ExpenseVoucherCreateRequest.builder().build();
        Employee accountant = Employee.builder().employeeId(1L).build();

        assertThrows(IllegalStateException.class,
                () -> service.payForStockReceiptItem(100L, request, accountant));
    }

    @Test
    void create_ShouldCreateVoucherWithFile_WhenFileProvided() {
        Employee creator = Employee.builder()
                .employeeId(1L)
                .fullName("Creator")
                .build();
        Employee manager = Employee.builder()
                .employeeId(3L)
                .fullName("Manager")
                .build();

        when(employeeRepo.findById(3L)).thenReturn(Optional.of(manager));
        when(fileStorageService.upload(any(MultipartFile.class))).thenReturn("http://file.url");
        when(codeSequenceService.generateCode("PAY")).thenReturn("PAY-2025-00001");

        ManualVoucherCreateRequest req = ManualVoucherCreateRequest.builder()
                .type(ManualVoucherType.PAYMENT)
                .category(LedgerVoucherCategory.OTHER)
                .amount(new BigDecimal("500000"))
                .description("Test")
                .relatedSupplierId(5L)
                .build();

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        LedgerVoucher savedVoucher = LedgerVoucher.builder()
                .id(100L)
                .code("PAY-2025-00001")
                .type(ManualVoucherType.PAYMENT)
                .amount(new BigDecimal("500000"))
                .attachmentUrl("http://file.url")
                .createdBy(creator)
                .approvedBy(manager)
                .status(ManualVoucherStatus.PENDING)
                .build();
        when(manualRepo.save(any(LedgerVoucher.class))).thenReturn(savedVoucher);

        ManualVoucherResponseDto dto = ManualVoucherResponseDto.builder()
                .id(100L)
                .code("PAY-2025-00001")
                .build();
        when(manualVoucherMapper.toDto(savedVoucher)).thenReturn(dto);

        Supplier supplier = Supplier.builder()
                .id(5L)
                .name("Supplier Name")
                .build();
        when(supplierRepo.findById(5L)).thenReturn(Optional.of(supplier));

        ManualVoucherResponseDto result = service.create(req, file, creator);

        assertNotNull(result);
        assertEquals("Supplier Name", result.getTargetName());
        verify(fileStorageService).upload(file);
        verify(codeSequenceService).generateCode("PAY");
        verify(manualRepo).save(any(LedgerVoucher.class));
    }

    @Test
    void create_ShouldCreateVoucherWithoutFile_WhenFileIsNull() {
        Employee creator = Employee.builder()
                .employeeId(1L)
                .fullName("Creator")
                .build();
        Employee manager = Employee.builder()
                .employeeId(3L)
                .fullName("Manager")
                .build();

        when(employeeRepo.findById(3L)).thenReturn(Optional.of(manager));
        when(codeSequenceService.generateCode("RECEIPT")).thenReturn("RECEIPT-2025-00001");

        ManualVoucherCreateRequest req = ManualVoucherCreateRequest.builder()
                .type(ManualVoucherType.RECEIPT)
                .amount(new BigDecimal("300000"))
                .relatedEmployeeId(10L)
                .build();

        LedgerVoucher savedVoucher = LedgerVoucher.builder()
                .id(100L)
                .code("RECEIPT-2025-00001")
                .build();
        when(manualRepo.save(any(LedgerVoucher.class))).thenReturn(savedVoucher);

        ManualVoucherResponseDto dto = ManualVoucherResponseDto.builder()
                .id(100L)
                .build();
        when(manualVoucherMapper.toDto(savedVoucher)).thenReturn(dto);

        Employee relatedEmployee = Employee.builder()
                .employeeId(10L)
                .fullName("Employee Name")
                .build();
        when(employeeRepo.findById(10L)).thenReturn(Optional.of(relatedEmployee));

        ManualVoucherResponseDto result = service.create(req, null, creator);

        assertNotNull(result);
        assertEquals("Employee Name", result.getTargetName());
        verify(fileStorageService, never()).upload(any());
        verify(codeSequenceService).generateCode("RECEIPT");
    }

    @Test
    void create_ShouldThrow_WhenManagerNotFound() {
        when(employeeRepo.findById(3L)).thenReturn(Optional.empty());

        ManualVoucherCreateRequest req = ManualVoucherCreateRequest.builder()
                .type(ManualVoucherType.PAYMENT)
                .build();

        Employee creator = Employee.builder().employeeId(1L).build();

        assertThrows(ResourceNotFoundException.class,
                () -> service.create(req, null, creator));
        verify(employeeRepo).findById(3L);
        verify(manualRepo, never()).save(any());
    }

    @Test
    void getList_ShouldReturnPagedDtos() {
        Pageable pageable = PageRequest.of(0, 5);
        LedgerVoucher voucher = LedgerVoucher.builder()
                .id(1L)
                .code("PAY-2025-00001")
                .type(ManualVoucherType.PAYMENT)
                .amount(new BigDecimal("500000"))
                .relatedSupplierId(5L)
                .build();
        Page<LedgerVoucher> page = new PageImpl<>(List.of(voucher), pageable, 1);

        when(manualRepo.findAll(pageable)).thenReturn(page);

        ManualVoucherListResponseDto listDto = ManualVoucherListResponseDto.builder()
                .id(1L)
                .code("PAY-2025-00001")
                .build();
        when(manualVoucherMapper.toListDto(voucher)).thenReturn(listDto);

        Supplier supplier = Supplier.builder()
                .id(5L)
                .name("Supplier Name")
                .build();
        when(supplierRepo.findById(5L)).thenReturn(Optional.of(supplier));

        Page<ManualVoucherListResponseDto> result = service.getList(0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals("Supplier Name", result.getContent().get(0).getTargetName());
        verify(manualRepo).findAll(pageable);
    }

    @Test
    void getDetail_ShouldReturnDto_WhenFound() {
        LedgerVoucher voucher = LedgerVoucher.builder()
                .id(1L)
                .code("PAY-2025-00001")
                .relatedEmployeeId(10L)
                .build();
        when(manualRepo.findById(1L)).thenReturn(Optional.of(voucher));

        ManualVoucherResponseDto dto = ManualVoucherResponseDto.builder()
                .id(1L)
                .code("PAY-2025-00001")
                .build();
        when(manualVoucherMapper.toDto(voucher)).thenReturn(dto);

        Employee employee = Employee.builder()
                .employeeId(10L)
                .fullName("Employee Name")
                .build();
        when(employeeRepo.findById(10L)).thenReturn(Optional.of(employee));

        ManualVoucherResponseDto result = service.getDetail(1L);

        assertSame(dto, result);
        assertEquals("Employee Name", result.getTargetName());
        verify(manualRepo).findById(1L);
    }

    @Test
    void getDetail_ShouldThrow_WhenNotFound() {
        when(manualRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getDetail(1L));
        verify(manualRepo).findById(1L);
    }

    @Test
    void getDetail_ShouldReturnUnknownTarget_WhenNoRelatedEntity() {
        LedgerVoucher voucher = LedgerVoucher.builder()
                .id(1L)
                .code("PAY-2025-00001")
                .relatedEmployeeId(null)
                .relatedSupplierId(null)
                .build();
        when(manualRepo.findById(1L)).thenReturn(Optional.of(voucher));

        ManualVoucherResponseDto dto = ManualVoucherResponseDto.builder()
                .id(1L)
                .build();
        when(manualVoucherMapper.toDto(voucher)).thenReturn(dto);

        ManualVoucherResponseDto result = service.getDetail(1L);

        assertEquals("Không xác định", result.getTargetName());
    }

    @Test
    void approveVoucher_ShouldUpdateStatus_WhenPending() {
        Employee approver = Employee.builder()
                .employeeId(2L)
                .fullName("Approver")
                .build();

        LedgerVoucher voucher = LedgerVoucher.builder()
                .id(1L)
                .code("PAY-2025-00001")
                .type(ManualVoucherType.PAYMENT)
                .amount(new BigDecimal("500000"))
                .description("Test")
                .status(ManualVoucherStatus.PENDING)
                .build();
        when(manualRepo.findById(1L)).thenReturn(Optional.of(voucher));
        when(manualRepo.save(voucher)).thenReturn(voucher);

        ManualVoucherResponseDto result = service.approveVoucher(1L, approver);

        assertEquals(ManualVoucherStatus.APPROVED, voucher.getStatus());
        assertNotNull(voucher.getApprovedAt());
        assertEquals(approver, voucher.getApprovedBy());
        assertEquals("Approver", result.getApprovedBy());
        verify(manualRepo).save(voucher);
    }

    @Test
    void approveVoucher_ShouldThrow_WhenNotFound() {
        when(manualRepo.findById(1L)).thenReturn(Optional.empty());

        Employee approver = Employee.builder().employeeId(2L).build();

        assertThrows(ResourceNotFoundException.class,
                () -> service.approveVoucher(1L, approver));
        verify(manualRepo).findById(1L);
        verify(manualRepo, never()).save(any());
    }

    @Test
    void approveVoucher_ShouldThrow_WhenAlreadyApproved() {
        Employee approver = Employee.builder()
                .employeeId(2L)
                .fullName("Approver")
                .build();

        LedgerVoucher voucher = LedgerVoucher.builder()
                .id(1L)
                .status(ManualVoucherStatus.APPROVED)
                .build();
        when(manualRepo.findById(1L)).thenReturn(Optional.of(voucher));

        assertThrows(RuntimeException.class,
                () -> service.approveVoucher(1L, approver));
        verify(manualRepo).findById(1L);
        verify(manualRepo, never()).save(any());
    }
}

