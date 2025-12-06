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
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.Debt;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.entity.Transaction;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
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
}
