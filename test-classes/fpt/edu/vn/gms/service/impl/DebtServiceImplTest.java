package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import fpt.edu.vn.gms.common.enums.TransactionMethod;
import fpt.edu.vn.gms.dto.request.CreateDebtDto;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.request.PayDebtRequestDto;
import fpt.edu.vn.gms.dto.response.CustomerDebtSummaryDto;
import fpt.edu.vn.gms.dto.response.TransactionResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerDebtResponseDto;
import fpt.edu.vn.gms.dto.response.DebtDetailResponseDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketDebtDetail;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.CustomerNotFoundException;
import fpt.edu.vn.gms.exception.DebtNotFoundException;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.exception.ServiceTicketNotFoundException;
import fpt.edu.vn.gms.mapper.CustomerDebtMapper;
import fpt.edu.vn.gms.mapper.DebtMapper;
import fpt.edu.vn.gms.mapper.ServiceTicketDebtDetailMapper;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.repository.DebtRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.repository.TransactionRepository;
import fpt.edu.vn.gms.service.CustomerService;
import fpt.edu.vn.gms.service.TransactionService;
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
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebtServiceImplTest {

    @Mock DebtRepository debtRepository;
    @Mock CustomerRepository customerRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock CustomerService customerService;
    @Mock ServiceTicketRepository serviceTicketRepository;
    @Mock DebtMapper debtMapper;
    @Mock CustomerDebtMapper customerDebtMapper;
    @Mock ServiceTicketDebtDetailMapper serviceTicketDebtDetailMapper;
    @Mock TransactionService transactionService;

    @InjectMocks DebtServiceImpl service;

    private Customer customer;

    @BeforeEach
    void init() {
        customer = Customer.builder()
                .customerId(10L)
                .fullName("Nguyễn Văn B")
                .phone("0909000000")
                .address("HN")
                .build();
    }

    @Test
    void getAllDebtsSummary_ShouldUseRepositoryAndReturnPage() {
        // Repository returns Page<Object[]>, service maps to CustomerDebtSummaryDto
        Object[] rawRow = new Object[]{
                10L,                                    // customerId
                "Nguyễn Văn B",                         // fullName
                "0909000000",                           // phone
                new BigDecimal("100000"),               // totalAmount
                new BigDecimal("0"),                    // totalPaidAmount
                new BigDecimal("100000"),              // totalRemaining
                LocalDate.now().plusDays(7),           // dueDate
                "OUTSTANDING"                           // status
        };

        List<Object[]> rawRows = new ArrayList<>();
        rawRows.add(rawRow);
        Page<Object[]> rawPage = new PageImpl<>(
                rawRows,
                PageRequest.of(0, 5),
                1
        );

        when(debtRepository.findTotalDebtGroupedByCustomer(any(Pageable.class)))
                .thenReturn(rawPage);

        Page<CustomerDebtSummaryDto> result = service.getAllDebtsSummary(0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals("Nguyễn Văn B", result.getContent().get(0).getCustomerFullName());
        assertEquals(new BigDecimal("100000"), result.getContent().get(0).getTotalAmount());

        verify(debtRepository).findTotalDebtGroupedByCustomer(any(Pageable.class));
    }

    @Test
    void getDebtsByCustomer_ShouldReturnDetail_WhenCustomerExists() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        Debt debt1 = Debt.builder()
                .id(1L)
                .customer(customer)
                .amount(new BigDecimal("100000"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(LocalDate.now().plusDays(7))
                .status(DebtStatus.OUTSTANDING)
                .build();

        Debt debt2 = Debt.builder()
                .id(2L)
                .customer(customer)
                .amount(new BigDecimal("50000"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(LocalDate.now().plusDays(7))
                .status(DebtStatus.OUTSTANDING)
                .build();

        Page<Debt> page = new PageImpl<>(List.of(debt1, debt2), PageRequest.of(0, 10), 2);
        when(debtRepository.findByCustomerAndFilter(eq(10L), eq(DebtStatus.OUTSTANDING), isNull(), any()))
                .thenReturn(page);

        CustomerDebtResponseDto row1 = CustomerDebtResponseDto.builder()
                .id(1L)
                .totalAmount(new BigDecimal("100000"))
                .paidAmount(BigDecimal.ZERO)
                .status(DebtStatus.OUTSTANDING.name())
                .build();
        CustomerDebtResponseDto row2 = CustomerDebtResponseDto.builder()
                .id(2L)
                .totalAmount(new BigDecimal("50000"))
                .paidAmount(BigDecimal.ZERO)
                .status(DebtStatus.OUTSTANDING.name())
                .build();
        when(customerDebtMapper.toDto(anyList())).thenReturn(List.of(row1, row2));

        DebtDetailResponseDto result =
                service.getDebtsByCustomer(10L, DebtStatus.OUTSTANDING, null, 0, 10, null);

        assertNotNull(result);
        assertEquals(customer.getFullName(), result.getCustomerName());
        assertEquals(customer.getPhone(), result.getPhone());
        assertEquals(new BigDecimal("150000"), result.getTotalRemainingAmount());
        assertEquals(2, result.getDebts().size());

        verify(customerRepository).findById(10L);
        verify(debtRepository).findByCustomerAndFilter(eq(10L), eq(DebtStatus.OUTSTANDING), isNull(), any());
        verify(customerDebtMapper).toDto(anyList());
    }

    @Test
    void getDebtsByCustomer_ShouldSupportSortingParameter() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        Page<Debt> page = new PageImpl<>(Collections.emptyList());
        when(debtRepository.findByCustomerAndFilter(eq(10L), isNull(), anyString(), any()))
                .thenReturn(page);

        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        DebtDetailResponseDto result =
                service.getDebtsByCustomer(10L, null, " keyword ", 0, 10, "amount,asc");

        assertNotNull(result);
        assertEquals(customer.getFullName(), result.getCustomerName());
        assertEquals(0, result.getDebts().size());

        verify(debtRepository).findByCustomerAndFilter(eq(10L), isNull(), eq("keyword"), any());
    }

    @Test
    void getDebtsByCustomer_ShouldThrow_WhenCustomerNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getDebtsByCustomer(999L, null, null, 0, 10, null));
        verify(customerRepository).findById(999L);
        verifyNoInteractions(debtRepository);
    }

    @Test
    void createDebt_ShouldSaveAndReturnDto_WhenValid() {
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(10L)
                .serviceTicketId(5L)
                .amount(new BigDecimal("250000"))
                .build();

        ServiceTicket st = ServiceTicket.builder().serviceTicketId(5L).build();

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(5L)).thenReturn(Optional.of(st));

        Debt saved = Debt.builder()
                .id(123L)
                .customer(customer)
                .serviceTicket(st)
                .amount(new BigDecimal("250000"))
                .dueDate(LocalDate.now().plusDays(14))
                .build();
        when(debtRepository.save(any(Debt.class))).thenReturn(saved);

        DebtDetailResponseDto mapped = DebtDetailResponseDto.builder()
                .customerName(customer.getFullName())
                .debts(Collections.emptyList())
                .totalRemainingAmount(new BigDecimal("250000"))
                .build();
        when(debtMapper.toDto(saved)).thenReturn(mapped);

        DebtDetailResponseDto result = service.createDebt(dto);
        assertNotNull(result);
        assertEquals(new BigDecimal("250000"), result.getTotalRemainingAmount());

        verify(customerRepository).findById(10L);
        verify(serviceTicketRepository).findById(5L);
        verify(debtRepository).save(any(Debt.class));
        verify(debtMapper).toDto(saved);
    }

    @Test
    void payDebt_ShouldReturnTransactionImmediately_WhenBankTransfer() throws Exception {
        Debt debt = Debt.builder()
                .id(1L)
                .amount(new BigDecimal("500000"))
                .paidAmount(BigDecimal.ZERO)
                .customer(customer)
                .build();

        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.BANK_TRANSFER.getValue())
                .price(500000L)
                .build();

        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.BANK_TRANSFER.getValue())
                .amount(500000L)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        TransactionResponseDto result = service.payDebt(1L, request);

        assertEquals(transaction, result);

        verify(transactionService).createTransaction(any(CreateTransactionRequestDto.class));
        verify(customerService, never()).updateTotalSpending(anyLong(), any());
        verify(debtRepository, never()).save(any(Debt.class));
    }

    @Test
    void payDebt_ShouldUpdateDebtAndCustomer_WhenCashAndNotFullyPaid() throws Exception {
        Debt debt = Debt.builder()
                .id(1L)
                .amount(new BigDecimal("500000"))
                .paidAmount(new BigDecimal("100000"))
                .customer(customer)
                .status(DebtStatus.OUTSTANDING)
                .build();

        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(200000L)
                .build();

        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(200000L)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        TransactionResponseDto result = service.payDebt(1L, request);

        assertEquals(transaction, result);
        assertEquals(new BigDecimal("300000"), debt.getPaidAmount());
        assertEquals(DebtStatus.OUTSTANDING, debt.getStatus());

        verify(customerService).updateTotalSpending(eq(customer.getCustomerId()),
                eq(new BigDecimal("200000")));
        verify(debtRepository).save(debt);
    }

    @Test
    void payDebt_ShouldMarkPaidInFullAndUpdateSpendingWithOverPaidAmount_WhenOverPayment() throws Exception {
        Debt debt = Debt.builder()
                .id(1L)
                .amount(new BigDecimal("500000"))
                .paidAmount(new BigDecimal("400000"))
                .customer(customer)
                .status(DebtStatus.OUTSTANDING)
                .build();

        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(200000L)
                .build();

        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(200000L)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        TransactionResponseDto result = service.payDebt(1L, request);

        assertEquals(transaction, result);
        assertEquals(new BigDecimal("500000"), debt.getPaidAmount());
        assertEquals(DebtStatus.PAID_IN_FULL, debt.getStatus());

        verify(customerService).updateTotalSpending(eq(customer.getCustomerId()),
                eq(new BigDecimal("100000")));
        verify(debtRepository).save(debt);
    }

    @Test
    void getDebtDetailByServiceTicketId_ShouldReturnDetail_WhenFound() {
        ServiceTicket st = ServiceTicket.builder()
                .serviceTicketId(5L)
                .customer(customer)
                .build();
        when(serviceTicketRepository.findById(5L)).thenReturn(Optional.of(st));
        when(transactionRepository.findAllByCustomerPhone(customer.getPhone()))
                .thenReturn(Collections.emptyList());

        ServiceTicketDebtDetail detail = ServiceTicketDebtDetail.builder()
                .serviceTicketResponseDto(null)
                .transactionResponseDto(Collections.emptyList())
                .build();
        when(serviceTicketDebtDetailMapper.toDebtDetail(eq(st), anyList()))
                .thenReturn(detail);

        ServiceTicketDebtDetail result = service.getDebtDetailByServiceTicketId(5L);
        assertNotNull(result);

        verify(serviceTicketRepository).findById(5L);
        verify(transactionRepository).findAllByCustomerPhone(customer.getPhone());
        verify(serviceTicketDebtDetailMapper).toDebtDetail(eq(st), anyList());
    }

    // ========== Additional test cases for getAllDebtsSummary ==========

    @Test
    void getAllDebtsSummary_ShouldReturnEmptyPage_WhenNoDebts() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Object[]> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(debtRepository.findTotalDebtGroupedByCustomer(pageable)).thenReturn(emptyPage);

        Page<CustomerDebtSummaryDto> result = service.getAllDebtsSummary(0, 5);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(debtRepository).findTotalDebtGroupedByCustomer(pageable);
    }

    @Test
    void getAllDebtsSummary_ShouldHandleNullValuesInRow() {
        Object[] rawRow = new Object[]{
                null,                                    // customerId
                null,                                    // fullName
                null,                                    // phone
                null,                                    // totalAmount
                null,                                    // totalPaidAmount
                null,                                    // totalRemaining
                null,                                    // dueDate
                null                                     // status
        };

        List<Object[]> rawRows = new ArrayList<>();
        rawRows.add(rawRow);
        Page<Object[]> rawPage = new PageImpl<>(rawRows, PageRequest.of(0, 5), 1);

        when(debtRepository.findTotalDebtGroupedByCustomer(any(Pageable.class))).thenReturn(rawPage);

        Page<CustomerDebtSummaryDto> result = service.getAllDebtsSummary(0, 5);

        assertEquals(1, result.getTotalElements());
        CustomerDebtSummaryDto dto = result.getContent().get(0);
        assertNull(dto.getCustomerId());
        assertNull(dto.getCustomerFullName());
        assertEquals(BigDecimal.ZERO, dto.getTotalAmount());
    }

    @Test
    void getAllDebtsSummary_ShouldHandleMultipleCustomers() {
        Object[] row1 = new Object[]{
                10L, "Customer 1", "0909000001",
                new BigDecimal("100000"), new BigDecimal("50000"), new BigDecimal("50000"),
                LocalDate.now().plusDays(7), "OUTSTANDING"
        };
        Object[] row2 = new Object[]{
                20L, "Customer 2", "0909000002",
                new BigDecimal("200000"), new BigDecimal("200000"), new BigDecimal("0"),
                LocalDate.now().plusDays(14), "PAID_IN_FULL"
        };

        List<Object[]> rawRows = new ArrayList<>();
        rawRows.add(row1);
        rawRows.add(row2);
        Page<Object[]> rawPage = new PageImpl<>(rawRows, PageRequest.of(0, 5), 2);

        when(debtRepository.findTotalDebtGroupedByCustomer(any(Pageable.class))).thenReturn(rawPage);

        Page<CustomerDebtSummaryDto> result = service.getAllDebtsSummary(0, 5);

        assertEquals(2, result.getTotalElements());
        assertEquals("Customer 1", result.getContent().get(0).getCustomerFullName());
        assertEquals("Customer 2", result.getContent().get(1).getCustomerFullName());
    }

    @Test
    void getAllDebtsSummary_ShouldHandleSecondPage() {
        Object[] row = new Object[]{
                30L, "Customer 3", "0909000003",
                new BigDecimal("300000"), new BigDecimal("0"), new BigDecimal("300000"),
                LocalDate.now().plusDays(30), "OUTSTANDING"
        };

        List<Object[]> rawRows = new ArrayList<>();
        rawRows.add(row);
        Page<Object[]> rawPage = new PageImpl<>(rawRows, PageRequest.of(1, 5), 6);

        when(debtRepository.findTotalDebtGroupedByCustomer(any(Pageable.class))).thenReturn(rawPage);

        Page<CustomerDebtSummaryDto> result = service.getAllDebtsSummary(1, 5);

        assertEquals(6, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAllDebtsSummary_ShouldMapAllFieldsCorrectly() {
        Object[] rawRow = new Object[]{
                40L,                                    // customerId
                "Customer 4",                          // fullName
                "0909000004",                          // phone
                new BigDecimal("500000"),             // totalAmount
                new BigDecimal("200000"),             // totalPaidAmount
                new BigDecimal("300000"),             // totalRemaining
                LocalDate.of(2025, 12, 31),           // dueDate
                "OUTSTANDING"                          // status
        };

        List<Object[]> rawRows = new ArrayList<>();
        rawRows.add(rawRow);
        Page<Object[]> rawPage = new PageImpl<>(rawRows, PageRequest.of(0, 5), 1);

        when(debtRepository.findTotalDebtGroupedByCustomer(any(Pageable.class))).thenReturn(rawPage);

        Page<CustomerDebtSummaryDto> result = service.getAllDebtsSummary(0, 5);

        CustomerDebtSummaryDto dto = result.getContent().get(0);
        assertEquals(40L, dto.getCustomerId());
        assertEquals("Customer 4", dto.getCustomerFullName());
        assertEquals("0909000004", dto.getCustomerPhone());
        assertEquals(new BigDecimal("500000"), dto.getTotalAmount());
        assertEquals(new BigDecimal("200000"), dto.getTotalPaidAmount());
        assertEquals(new BigDecimal("300000"), dto.getTotalRemaining());
        assertEquals(LocalDate.of(2025, 12, 31), dto.getDueDate());
        assertEquals("OUTSTANDING", dto.getStatus());
    }

    // ========== Additional test cases for getDebtsByCustomer ==========

    @Test
    void getDebtsByCustomer_ShouldHandleEmptyDebtList() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        Page<Debt> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(debtRepository.findByCustomerAndFilter(eq(10L), isNull(), isNull(), any()))
                .thenReturn(emptyPage);

        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        DebtDetailResponseDto result = service.getDebtsByCustomer(10L, null, null, 0, 10, null);

        assertNotNull(result);
        assertEquals(customer.getFullName(), result.getCustomerName());
        assertEquals(BigDecimal.ZERO, result.getTotalRemainingAmount());
        assertEquals(0, result.getDebts().size());
    }

    @Test
    void getDebtsByCustomer_ShouldCalculateTotalRemainingCorrectly() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        Debt debt1 = Debt.builder()
                .id(1L)
                .amount(new BigDecimal("100000"))
                .build();
        Debt debt2 = Debt.builder()
                .id(2L)
                .amount(new BigDecimal("200000"))
                .build();
        Debt debt3 = Debt.builder()
                .id(3L)
                .amount(new BigDecimal("300000"))
                .build();

        Page<Debt> page = new PageImpl<>(List.of(debt1, debt2, debt3), PageRequest.of(0, 10), 3);
        when(debtRepository.findByCustomerAndFilter(eq(10L), isNull(), isNull(), any()))
                .thenReturn(page);

        CustomerDebtResponseDto dto1 = CustomerDebtResponseDto.builder().id(1L).build();
        CustomerDebtResponseDto dto2 = CustomerDebtResponseDto.builder().id(2L).build();
        CustomerDebtResponseDto dto3 = CustomerDebtResponseDto.builder().id(3L).build();
        when(customerDebtMapper.toDto(anyList())).thenReturn(List.of(dto1, dto2, dto3));

        DebtDetailResponseDto result = service.getDebtsByCustomer(10L, null, null, 0, 10, null);

        assertEquals(new BigDecimal("600000"), result.getTotalRemainingAmount()); // 100000 + 200000 + 300000
    }

    @Test
    void getDebtsByCustomer_ShouldHandleCustomerWithVehicles() {
        Vehicle vehicle = Vehicle.builder()
                .vehicleId(1L)
                .licensePlate("30A-12345")
                .customer(customer)
                .build();
        customer.setVehicles(List.of(vehicle));

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        Page<Debt> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(debtRepository.findByCustomerAndFilter(eq(10L), isNull(), isNull(), any()))
                .thenReturn(page);
        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        DebtDetailResponseDto result = service.getDebtsByCustomer(10L, null, null, 0, 10, null);

        assertEquals("30A-12345", result.getLicensePlate());
    }

    @Test
    void getDebtsByCustomer_ShouldHandleCustomerWithoutVehicles() {
        customer.setVehicles(Collections.emptyList());

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        Page<Debt> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(debtRepository.findByCustomerAndFilter(eq(10L), isNull(), isNull(), any()))
                .thenReturn(page);
        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        DebtDetailResponseDto result = service.getDebtsByCustomer(10L, null, null, 0, 10, null);

        assertNull(result.getLicensePlate());
    }

    @Test
    void getDebtsByCustomer_ShouldHandleStatusFilter() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        Debt debt = Debt.builder()
                .id(1L)
                .status(DebtStatus.OUTSTANDING)
                .amount(new BigDecimal("100000"))
                .build();

        Page<Debt> page = new PageImpl<>(List.of(debt), PageRequest.of(0, 10), 1);
        when(debtRepository.findByCustomerAndFilter(eq(10L), eq(DebtStatus.OUTSTANDING), isNull(), any()))
                .thenReturn(page);

        CustomerDebtResponseDto dto = CustomerDebtResponseDto.builder().id(1L).build();
        when(customerDebtMapper.toDto(anyList())).thenReturn(List.of(dto));

        DebtDetailResponseDto result = service.getDebtsByCustomer(10L, DebtStatus.OUTSTANDING, null, 0, 10, null);

        assertEquals(1, result.getDebts().size());
        verify(debtRepository).findByCustomerAndFilter(eq(10L), eq(DebtStatus.OUTSTANDING), isNull(), any());
    }

    // ========== Additional test cases for createDebt ==========

    @Test
    void createDebt_ShouldThrow_WhenCustomerNotFound() {
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(999L)
                .serviceTicketId(5L)
                .amount(new BigDecimal("250000"))
                .build();

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.createDebt(dto));
        verify(customerRepository).findById(999L);
        verify(serviceTicketRepository, never()).findById(any());
        verify(debtRepository, never()).save(any());
    }

    @Test
    void createDebt_ShouldThrow_WhenServiceTicketNotFound() {
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(10L)
                .serviceTicketId(999L)
                .amount(new BigDecimal("250000"))
                .build();

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ServiceTicketNotFoundException.class, () -> service.createDebt(dto));
        verify(serviceTicketRepository).findById(999L);
        verify(debtRepository, never()).save(any());
    }

    @Test
    void createDebt_ShouldSetDefaultDueDate() {
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(10L)
                .serviceTicketId(5L)
                .amount(new BigDecimal("250000"))
                .build();

        ServiceTicket st = ServiceTicket.builder().serviceTicketId(5L).build();

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(5L)).thenReturn(Optional.of(st));

        Debt saved = Debt.builder()
                .id(123L)
                .customer(customer)
                .serviceTicket(st)
                .amount(new BigDecimal("250000"))
                .dueDate(LocalDate.now().plusDays(14))
                .build();
        when(debtRepository.save(any(Debt.class))).thenAnswer(invocation -> {
            Debt debt = invocation.getArgument(0);
            assertEquals(LocalDate.now().plusDays(14), debt.getDueDate());
            return saved;
        });

        DebtDetailResponseDto mapped = DebtDetailResponseDto.builder()
                .customerName(customer.getFullName())
                .debts(Collections.emptyList())
                .totalRemainingAmount(new BigDecimal("250000"))
                .build();
        when(debtMapper.toDto(saved)).thenReturn(mapped);

        DebtDetailResponseDto result = service.createDebt(dto);
        assertNotNull(result);
        verify(debtRepository).save(any(Debt.class));
    }

    @Test
    void createDebt_ShouldSaveDebtWithCorrectFields() {
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(10L)
                .serviceTicketId(5L)
                .amount(new BigDecimal("500000"))
                .build();

        ServiceTicket st = ServiceTicket.builder().serviceTicketId(5L).build();

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(5L)).thenReturn(Optional.of(st));

        Debt saved = Debt.builder()
                .id(123L)
                .customer(customer)
                .serviceTicket(st)
                .amount(new BigDecimal("500000"))
                .dueDate(LocalDate.now().plusDays(14))
                .build();
        when(debtRepository.save(any(Debt.class))).thenAnswer(invocation -> {
            Debt debt = invocation.getArgument(0);
            assertEquals(customer, debt.getCustomer());
            assertEquals(st, debt.getServiceTicket());
            assertEquals(new BigDecimal("500000"), debt.getAmount());
            return saved;
        });

        DebtDetailResponseDto mapped = DebtDetailResponseDto.builder()
                .customerName(customer.getFullName())
                .debts(Collections.emptyList())
                .totalRemainingAmount(new BigDecimal("500000"))
                .build();
        when(debtMapper.toDto(saved)).thenReturn(mapped);

        DebtDetailResponseDto result = service.createDebt(dto);
        assertNotNull(result);
        assertEquals(new BigDecimal("500000"), result.getTotalRemainingAmount());
    }

    @Test
    void createDebt_ShouldMapToDetailDto() {
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(10L)
                .serviceTicketId(5L)
                .amount(new BigDecimal("750000"))
                .build();

        ServiceTicket st = ServiceTicket.builder().serviceTicketId(5L).build();

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(5L)).thenReturn(Optional.of(st));

        Debt saved = Debt.builder()
                .id(123L)
                .customer(customer)
                .serviceTicket(st)
                .amount(new BigDecimal("750000"))
                .dueDate(LocalDate.now().plusDays(14))
                .build();
        when(debtRepository.save(any(Debt.class))).thenReturn(saved);

        DebtDetailResponseDto mapped = DebtDetailResponseDto.builder()
                .customerName(customer.getFullName())
                .phone(customer.getPhone())
                .debts(Collections.emptyList())
                .totalRemainingAmount(new BigDecimal("750000"))
                .build();
        when(debtMapper.toDto(saved)).thenReturn(mapped);

        DebtDetailResponseDto result = service.createDebt(dto);
        assertSame(mapped, result);
        verify(debtMapper).toDto(saved);
    }

    // ========== Additional test cases for payDebt ==========

    @Test
    void payDebt_ShouldThrow_WhenDebtNotFound() throws Exception {
        when(debtRepository.findById(999L)).thenReturn(Optional.empty());

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(100000L)
                .build();

        assertThrows(DebtNotFoundException.class, () -> service.payDebt(999L, request));
        verify(debtRepository).findById(999L);
        verify(transactionService, never()).createTransaction(any());
    }

    @Test
    void payDebt_ShouldHandleExactPaymentAmount() throws Exception {
        Debt debt = Debt.builder()
                .id(1L)
                .amount(new BigDecimal("500000"))
                .paidAmount(BigDecimal.ZERO)
                .customer(customer)
                .status(DebtStatus.OUTSTANDING)
                .build();

        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(500000L)
                .build();

        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(500000L)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        TransactionResponseDto result = service.payDebt(1L, request);

        assertEquals(transaction, result);
        assertEquals(new BigDecimal("500000"), debt.getPaidAmount());
        assertEquals(DebtStatus.PAID_IN_FULL, debt.getStatus());
        verify(customerService).updateTotalSpending(eq(customer.getCustomerId()), eq(new BigDecimal("500000")));
        verify(debtRepository).save(debt);
    }

    @Test
    void payDebt_ShouldHandlePartialPayment() throws Exception {
        Debt debt = Debt.builder()
                .id(1L)
                .amount(new BigDecimal("1000000"))
                .paidAmount(new BigDecimal("200000"))
                .customer(customer)
                .status(DebtStatus.OUTSTANDING)
                .build();

        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(300000L)
                .build();

        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(300000L)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        TransactionResponseDto result = service.payDebt(1L, request);

        assertEquals(transaction, result);
        assertEquals(new BigDecimal("500000"), debt.getPaidAmount()); // 200000 + 300000
        assertEquals(DebtStatus.OUTSTANDING, debt.getStatus());
        verify(customerService).updateTotalSpending(eq(customer.getCustomerId()), eq(new BigDecimal("300000")));
        verify(debtRepository).save(debt);
    }

    @Test
    void payDebt_ShouldHandleOverPayment() throws Exception {
        Debt debt = Debt.builder()
                .id(1L)
                .amount(new BigDecimal("500000"))
                .paidAmount(new BigDecimal("400000"))
                .customer(customer)
                .status(DebtStatus.OUTSTANDING)
                .build();

        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(200000L)
                .build();

        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(200000L)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        TransactionResponseDto result = service.payDebt(1L, request);

        assertEquals(transaction, result);
        assertEquals(new BigDecimal("500000"), debt.getPaidAmount()); // Capped at debt amount
        assertEquals(DebtStatus.PAID_IN_FULL, debt.getStatus());
        // Overpaid amount = 600000 - 500000 = 100000
        verify(customerService).updateTotalSpending(eq(customer.getCustomerId()), eq(new BigDecimal("100000")));
        verify(debtRepository).save(debt);
    }

    @Test
    void payDebt_ShouldNotUpdateDebt_WhenBankTransfer() throws Exception {
        Debt debt = Debt.builder()
                .id(1L)
                .amount(new BigDecimal("500000"))
                .paidAmount(BigDecimal.ZERO)
                .customer(customer)
                .status(DebtStatus.OUTSTANDING)
                .build();

        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.BANK_TRANSFER.getValue())
                .price(500000L)
                .build();

        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.BANK_TRANSFER.getValue())
                .amount(500000L)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        TransactionResponseDto result = service.payDebt(1L, request);

        assertEquals(transaction, result);
        assertEquals(BigDecimal.ZERO, debt.getPaidAmount()); // Unchanged
        assertEquals(DebtStatus.OUTSTANDING, debt.getStatus()); // Unchanged
        verify(customerService, never()).updateTotalSpending(anyLong(), any());
        verify(debtRepository, never()).save(any(Debt.class));
    }

    // ========== Additional test cases for getDebtDetailByServiceTicketId ==========

    @Test
    void getDebtDetailByServiceTicketId_ShouldThrow_WhenServiceTicketNotFound() {
        when(serviceTicketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getDebtDetailByServiceTicketId(999L));
        verify(serviceTicketRepository).findById(999L);
        verify(transactionRepository, never()).findAllByCustomerPhone(anyString());
    }

    @Test
    void getDebtDetailByServiceTicketId_ShouldHandleMultipleTransactions() {
        ServiceTicket st = ServiceTicket.builder()
                .serviceTicketId(5L)
                .customer(customer)
                .build();
        when(serviceTicketRepository.findById(5L)).thenReturn(Optional.of(st));

        Transaction transaction1 = Transaction.builder()
                .id(1L)
                .amount(100000L)
                .build();
        Transaction transaction2 = Transaction.builder()
                .id(2L)
                .amount(200000L)
                .build();
        when(transactionRepository.findAllByCustomerPhone(customer.getPhone()))
                .thenReturn(List.of(transaction1, transaction2));

        ServiceTicketDebtDetail detail = ServiceTicketDebtDetail.builder()
                .serviceTicketResponseDto(null)
                .transactionResponseDto(Collections.emptyList())
                .build();
        when(serviceTicketDebtDetailMapper.toDebtDetail(eq(st), eq(List.of(transaction1, transaction2))))
                .thenReturn(detail);

        ServiceTicketDebtDetail result = service.getDebtDetailByServiceTicketId(5L);
        assertNotNull(result);

        verify(transactionRepository).findAllByCustomerPhone(customer.getPhone());
        verify(serviceTicketDebtDetailMapper).toDebtDetail(eq(st), eq(List.of(transaction1, transaction2)));
    }

    @Test
    void getDebtDetailByServiceTicketId_ShouldHandleEmptyTransactionList() {
        ServiceTicket st = ServiceTicket.builder()
                .serviceTicketId(5L)
                .customer(customer)
                .build();
        when(serviceTicketRepository.findById(5L)).thenReturn(Optional.of(st));
        when(transactionRepository.findAllByCustomerPhone(customer.getPhone()))
                .thenReturn(Collections.emptyList());

        ServiceTicketDebtDetail detail = ServiceTicketDebtDetail.builder()
                .serviceTicketResponseDto(null)
                .transactionResponseDto(Collections.emptyList())
                .build();
        when(serviceTicketDebtDetailMapper.toDebtDetail(eq(st), eq(Collections.emptyList())))
                .thenReturn(detail);

        ServiceTicketDebtDetail result = service.getDebtDetailByServiceTicketId(5L);
        assertNotNull(result);

        verify(serviceTicketDebtDetailMapper).toDebtDetail(eq(st), eq(Collections.emptyList()));
    }

    @Test
    void getDebtDetailByServiceTicketId_ShouldUseCustomerPhoneFromServiceTicket() {
        Customer customerWithPhone = Customer.builder()
                .customerId(20L)
                .fullName("Customer Phone")
                .phone("0911111111")
                .build();

        ServiceTicket st = ServiceTicket.builder()
                .serviceTicketId(5L)
                .customer(customerWithPhone)
                .build();
        when(serviceTicketRepository.findById(5L)).thenReturn(Optional.of(st));
        when(transactionRepository.findAllByCustomerPhone("0911111111"))
                .thenReturn(Collections.emptyList());

        ServiceTicketDebtDetail detail = ServiceTicketDebtDetail.builder().build();
        when(serviceTicketDebtDetailMapper.toDebtDetail(eq(st), anyList())).thenReturn(detail);

        service.getDebtDetailByServiceTicketId(5L);

        verify(transactionRepository).findAllByCustomerPhone("0911111111");
    }

    @Test
    void getDebtDetailByServiceTicketId_ShouldReturnMappedDetail() {
        ServiceTicket st = ServiceTicket.builder()
                .serviceTicketId(5L)
                .customer(customer)
                .build();
        when(serviceTicketRepository.findById(5L)).thenReturn(Optional.of(st));
        when(transactionRepository.findAllByCustomerPhone(customer.getPhone()))
                .thenReturn(Collections.emptyList());

        ServiceTicketDebtDetail expectedDetail = ServiceTicketDebtDetail.builder()
                .serviceTicketResponseDto(null)
                .transactionResponseDto(Collections.emptyList())
                .build();
        when(serviceTicketDebtDetailMapper.toDebtDetail(eq(st), anyList()))
                .thenReturn(expectedDetail);

        ServiceTicketDebtDetail result = service.getDebtDetailByServiceTicketId(5L);

        assertSame(expectedDetail, result);
        verify(serviceTicketDebtDetailMapper).toDebtDetail(eq(st), anyList());
    }
}
