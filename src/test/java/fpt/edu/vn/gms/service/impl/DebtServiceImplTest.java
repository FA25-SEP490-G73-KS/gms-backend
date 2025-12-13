package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import fpt.edu.vn.gms.common.enums.TransactionMethod;
import fpt.edu.vn.gms.dto.request.CreateDebtDto;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.request.PayDebtRequestDto;
import fpt.edu.vn.gms.dto.response.CustomerDebtResponseDto;
import fpt.edu.vn.gms.dto.response.DebtDetailResponseDto;
import fpt.edu.vn.gms.dto.response.TransactionResponseDto;
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
import org.springframework.data.domain.*;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for DebtServiceImpl
 * Based on TEST_CASE_DESIGN_DOCUMENT.md
 * Matrix: DE-001, DE-002, DE-003, DE-004
 * Total: 46 test cases (0 EXISTING, 46 NEW)
 */
@ExtendWith(MockitoExtension.class)
class DebtServiceImplTest_NEW {

    @Mock
    DebtRepository debtRepository;
    @Mock
    CustomerRepository customerRepository;
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    CustomerService customerService;
    @Mock
    ServiceTicketRepository serviceTicketRepository;
    @Mock
    DebtMapper debtMapper;
    @Mock
    CustomerDebtMapper customerDebtMapper;
    @Mock
    ServiceTicketDebtDetailMapper serviceTicketDebtDetailMapper;
    @Mock
    TransactionService transactionService;

    @InjectMocks
    DebtServiceImpl service;

    private Customer customer;
    private Debt debt;
    private ServiceTicket serviceTicket;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .customerId(1L)
                .fullName("Nguyễn Văn A")
                .phone("0901234567")
                .address("Hà Nội")
                .build();

        serviceTicket = ServiceTicket.builder()
                .serviceTicketId(1L)
                .serviceTicketCode("PDV-000001")
                .customer(customer)
                .build();

        debt = Debt.builder()
                .id(1L)
                .customer(customer)
                .serviceTicket(serviceTicket)
                .amount(new BigDecimal("1000000"))
                .paidAmount(BigDecimal.ZERO)
                .status(DebtStatus.OUTSTANDING)
                .dueDate(LocalDate.now().plusDays(14))
                .build();
    }

    // ========== MATRIX 4: payDebt (DE-001) - UTCID43-UTCID54 ==========

    /**
     * UTCID43: Valid debt, CASH payment, partial payment
     * Precondition: Valid debt, CASH payment, partial payment
     * Input: debtId=1L, method=CASH, price=500000
     * Expected: Updates debt.paidAmount, status=OUTSTANDING, updates customer spending
     * Type: N (Normal)
     */
    @Test
    void payDebt_UTCID43_ShouldUpdateDebt_WhenCashPartialPayment() throws Exception {
        // Given
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(500000L)
                .build();
        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);
        when(debtRepository.save(any(Debt.class))).thenReturn(debt);
        doNothing().when(customerService).updateTotalSpending(anyLong(), any(BigDecimal.class));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(500000L)
                .build();

        // When
        TransactionResponseDto result = service.payDebt(1L, request);

        // Then
        assertNotNull(result);
        verify(debtRepository).save(argThat(d ->
            d.getPaidAmount().compareTo(new BigDecimal("500000")) == 0 &&
            d.getStatus() == DebtStatus.OUTSTANDING
        ));
        verify(customerService).updateTotalSpending(eq(1L), eq(new BigDecimal("500000")));
    }

    /**
     * UTCID44: Valid debt, CASH payment, full payment
     * Precondition: Valid debt, CASH payment, full payment
     * Input: debtId=1L, method=CASH, price=1000000
     * Expected: Updates debt.paidAmount=amount, status=PAID_IN_FULL
     * Type: N (Normal)
     */
    @Test
    void payDebt_UTCID44_ShouldMarkAsPaidInFull_WhenCashFullPayment() throws Exception {
        // Given
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(1000000L)
                .build();
        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);
        when(debtRepository.save(any(Debt.class))).thenReturn(debt);
        doNothing().when(customerService).updateTotalSpending(anyLong(), any(BigDecimal.class));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(1000000L)
                .build();

        // When
        service.payDebt(1L, request);

        // Then
        verify(debtRepository).save(argThat(d ->
            d.getPaidAmount().compareTo(new BigDecimal("1000000")) == 0 &&
            d.getStatus() == DebtStatus.PAID_IN_FULL
        ));
    }

    /**
     * UTCID45: Valid debt, CASH payment, overpayment
     * Precondition: Valid debt, CASH payment, overpayment
     * Input: debtId=1L, method=CASH, price=1500000
     * Expected: debt.paidAmount=amount, status=PAID_IN_FULL, customer spending = overpayment
     * Type: N (Normal)
     */
    @Test
    void payDebt_UTCID45_ShouldHandleOverpayment_WhenCashOverpayment() throws Exception {
        // Given
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(1500000L)
                .build();
        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);
        when(debtRepository.save(any(Debt.class))).thenReturn(debt);
        doNothing().when(customerService).updateTotalSpending(anyLong(), any(BigDecimal.class));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(1500000L)
                .build();

        // When
        service.payDebt(1L, request);

        // Then
        verify(debtRepository).save(argThat(d ->
            d.getPaidAmount().compareTo(new BigDecimal("1000000")) == 0 &&
            d.getStatus() == DebtStatus.PAID_IN_FULL
        ));
        verify(customerService).updateTotalSpending(eq(1L), eq(new BigDecimal("500000")));
    }

    /**
     * UTCID46: Valid debt, BANK_TRANSFER payment
     * Precondition: Valid debt, BANK_TRANSFER payment
     * Input: debtId=1L, method=BANK_TRANSFER, price=500000
     * Expected: Returns transaction immediately, no debt update
     * Type: N (Normal)
     */
    @Test
    void payDebt_UTCID46_ShouldReturnImmediately_WhenBankTransfer() throws Exception {
        // Given
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.BANK_TRANSFER.getValue())
                .amount(500000L)
                .build();
        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.BANK_TRANSFER.getValue())
                .price(500000L)
                .build();

        // When
        TransactionResponseDto result = service.payDebt(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(transaction, result);
        verify(debtRepository, never()).save(any(Debt.class));
        verify(customerService, never()).updateTotalSpending(anyLong(), any(BigDecimal.class));
    }

    /**
     * UTCID47: Debt not found
     * Precondition: Debt not found
     * Input: debtId=999L
     * Expected: Throws DebtNotFoundException
     * Type: A (Abnormal)
     */
    @Test
    void payDebt_UTCID47_ShouldThrowException_WhenDebtNotFound() {
        // Given
        when(debtRepository.findById(999L)).thenReturn(Optional.empty());
        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(500000L)
                .build();

        // When & Then
        assertThrows(DebtNotFoundException.class, () -> service.payDebt(999L, request));
    }

    /**
     * UTCID48: Invalid payment method
     * Precondition: Invalid payment method
     * Input: method="INVALID"
     * Expected: Throws ResourceNotFoundException
     * Type: A (Abnormal)
     */
    @Test
    void payDebt_UTCID48_ShouldThrowException_WhenInvalidPaymentMethod() {
        // Given
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method("INVALID")
                .price(500000L)
                .build();

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.payDebt(1L, request));
    }

    /**
     * UTCID49: Price = 0
     * Precondition: Price = 0
     * Input: price=0
     * Expected: Either throws exception or allows
     * Type: B (Boundary)
     */
    @Test
    void payDebt_UTCID49_ShouldHandle_WhenPriceIsZero() throws Exception {
        // Given
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(0L)
                .build();
        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);
        when(debtRepository.save(any(Debt.class))).thenReturn(debt);
        doNothing().when(customerService).updateTotalSpending(anyLong(), any(BigDecimal.class));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(0L)
                .build();

        // When & Then
        assertDoesNotThrow(() -> service.payDebt(1L, request));
    }

    /**
     * UTCID50: Price = Long.MAX_VALUE
     * Precondition: Price = Long.MAX_VALUE
     * Input: price=Long.MAX_VALUE
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void payDebt_UTCID50_ShouldHandle_WhenPriceIsMaxValue() throws Exception {
        // Given
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(Long.MAX_VALUE)
                .build();
        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);
        when(debtRepository.save(any(Debt.class))).thenReturn(debt);
        doNothing().when(customerService).updateTotalSpending(anyLong(), any(BigDecimal.class));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(Long.MAX_VALUE)
                .build();

        // When & Then
        assertDoesNotThrow(() -> service.payDebt(1L, request));
    }

    /**
     * UTCID51: Transaction creation fails
     * Precondition: Transaction creation fails
     * Input: transactionService throws exception
     * Expected: Throws Exception
     * Type: A (Abnormal)
     */
    @Test
    void payDebt_UTCID51_ShouldThrowException_WhenTransactionCreationFails() throws Exception {
        // Given
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenThrow(new RuntimeException("Transaction creation failed"));
        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(500000L)
                .build();

        // When & Then
        assertThrows(Exception.class, () -> service.payDebt(1L, request));
    }

    /**
     * UTCID52: Database save fails
     * Precondition: Database save fails
     * Input: repository.save() throws exception
     * Expected: Throws DataAccessException
     * Type: A (Abnormal)
     */
    @Test
    void payDebt_UTCID52_ShouldThrowException_WhenDatabaseSaveFails() throws Exception {
        // Given
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(500000L)
                .build();
        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);
        when(debtRepository.save(any(Debt.class)))
                .thenThrow(new DataAccessException("Database error") {});

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(500000L)
                .build();

        // When & Then
        assertThrows(DataAccessException.class, () -> service.payDebt(1L, request));
    }

    /**
     * UTCID53: Customer update fails
     * Precondition: Customer update fails
     * Input: customerService throws exception
     * Expected: Either throws exception or continues
     * Type: A (Abnormal)
     */
    @Test
    void payDebt_UTCID53_ShouldHandle_WhenCustomerUpdateFails() throws Exception {
        // Given
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(100L)
                .method(TransactionMethod.CASH.getValue())
                .amount(500000L)
                .build();
        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);
        // Note: debtRepository.save() is not called when customerService.updateTotalSpending() throws exception
        doThrow(new RuntimeException("Customer update failed")).when(customerService)
                .updateTotalSpending(anyLong(), any(BigDecimal.class));

        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(500000L)
                .build();

        // When & Then
        assertThrows(RuntimeException.class, () -> service.payDebt(1L, request));
    }

    /**
     * UTCID54: Boundary: debtId = Long.MAX_VALUE
     * Precondition: Boundary: debtId = Long.MAX_VALUE
     * Input: debtId=Long.MAX_VALUE
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void payDebt_UTCID54_ShouldHandleBoundary_WhenDebtIdIsMaxValue() {
        // Given
        Long maxDebtId = Long.MAX_VALUE;
        when(debtRepository.findById(maxDebtId)).thenReturn(Optional.empty());
        PayDebtRequestDto request = PayDebtRequestDto.builder()
                .method(TransactionMethod.CASH.getValue())
                .price(500000L)
                .build();

        // When & Then
        assertThrows(DebtNotFoundException.class, () -> service.payDebt(maxDebtId, request));
    }

    // ========== MATRIX 12: getDebtsByCustomer (DE-002) - UTCID140-UTCID151 ==========

    /**
     * UTCID140: Valid customer, status=OUTSTANDING, no keyword
     * Precondition: Valid customer, status=OUTSTANDING, no keyword
     * Input: customerId=1L, status=OUTSTANDING, keyword=null, page=0, size=10
     * Expected: Returns debts list with totalRemainingAmount
     * Type: N (Normal)
     */
    @Test
    void getDebtsByCustomer_UTCID140_ShouldReturnDebts_WhenValidCustomerAndStatus() {
        // Given
        Debt debt1 = Debt.builder()
                .id(1L)
                .amount(new BigDecimal("1000000"))
                .paidAmount(BigDecimal.ZERO)
                .status(DebtStatus.OUTSTANDING)
                .build();
        Debt debt2 = Debt.builder()
                .id(2L)
                .amount(new BigDecimal("500000"))
                .paidAmount(BigDecimal.ZERO)
                .status(DebtStatus.OUTSTANDING)
                .build();

        Page<Debt> debtPage = new PageImpl<>(List.of(debt1, debt2), PageRequest.of(0, 10), 2);
        CustomerDebtResponseDto dto1 = CustomerDebtResponseDto.builder()
                .id(1L)
                .totalAmount(new BigDecimal("1000000"))
                .build();
        CustomerDebtResponseDto dto2 = CustomerDebtResponseDto.builder()
                .id(2L)
                .totalAmount(new BigDecimal("500000"))
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(debtRepository.findByCustomerAndFilter(eq(1L), eq(DebtStatus.OUTSTANDING), isNull(), any(Pageable.class)))
                .thenReturn(debtPage);
        when(customerDebtMapper.toDto(anyList())).thenReturn(List.of(dto1, dto2));

        // When
        DebtDetailResponseDto result = service.getDebtsByCustomer(1L, DebtStatus.OUTSTANDING, null, 0, 10, null);

        // Then
        assertNotNull(result);
        assertEquals(customer.getFullName(), result.getCustomerName());
        assertEquals(customer.getPhone(), result.getPhone());
        assertEquals(new BigDecimal("1500000"), result.getTotalRemainingAmount());
        assertEquals(2, result.getDebts().size());
    }

    /**
     * UTCID141: Valid customer, status=null, with keyword
     * Precondition: Valid customer, status=null, with keyword
     * Input: customerId=1L, status=null, keyword="PDV", page=0, size=10
     * Expected: Returns filtered debts by keyword
     * Type: N (Normal)
     */
    @Test
    void getDebtsByCustomer_UTCID141_ShouldReturnFilteredDebts_WhenKeywordProvided() {
        // Given
        Page<Debt> debtPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(debtRepository.findByCustomerAndFilter(eq(1L), isNull(), eq("PDV"), any(Pageable.class)))
                .thenReturn(debtPage);
        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        // When
        DebtDetailResponseDto result = service.getDebtsByCustomer(1L, null, " PDV ", 0, 10, null);

        // Then
        assertNotNull(result);
        verify(debtRepository).findByCustomerAndFilter(eq(1L), isNull(), eq("PDV"), any(Pageable.class));
    }

    /**
     * UTCID142: Customer not found
     * Precondition: Customer not found
     * Input: customerId=999L
     * Expected: Throws ResourceNotFoundException
     * Type: A (Abnormal)
     */
    @Test
    void getDebtsByCustomer_UTCID142_ShouldThrowException_WhenCustomerNotFound() {
        // Given
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> service.getDebtsByCustomer(999L, null, null, 0, 10, null));
    }

    /**
     * UTCID143: Empty debts list
     * Precondition: Empty debts list
     * Input: customerId=1L, no debts
     * Expected: Returns empty list, totalRemainingAmount=0
     * Type: N (Normal)
     */
    @Test
    void getDebtsByCustomer_UTCID143_ShouldReturnEmptyList_WhenNoDebts() {
        // Given
        Page<Debt> debtPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(debtRepository.findByCustomerAndFilter(eq(1L), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(debtPage);
        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        // When
        DebtDetailResponseDto result = service.getDebtsByCustomer(1L, null, null, 0, 10, null);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getDebts().size());
        assertEquals(BigDecimal.ZERO, result.getTotalRemainingAmount());
    }

    /**
     * UTCID144: Pagination: page=1, size=5
     * Precondition: Pagination: page=1, size=5
     * Input: page=1, size=5
     * Expected: Returns page 1 with 5 items
     * Type: N (Normal)
     */
    @Test
    void getDebtsByCustomer_UTCID144_ShouldHandlePagination_WhenPageAndSizeProvided() {
        // Given
        Page<Debt> debtPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(1, 5), 0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(debtRepository.findByCustomerAndFilter(eq(1L), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(debtPage);
        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        // When
        service.getDebtsByCustomer(1L, null, null, 1, 5, null);

        // Then
        verify(debtRepository).findByCustomerAndFilter(eq(1L), isNull(), isNull(),
                argThat(pageable -> pageable.getPageNumber() == 1 && pageable.getPageSize() == 5));
    }

    /**
     * UTCID145: Sort parameter: "amount,asc"
     * Precondition: Sort parameter: "amount,asc"
     * Input: sort="amount,asc"
     * Expected: Returns sorted debts by amount ascending
     * Type: N (Normal)
     */
    @Test
    void getDebtsByCustomer_UTCID145_ShouldSortDebts_WhenSortParameterProvided() {
        // Given
        Page<Debt> debtPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(debtRepository.findByCustomerAndFilter(eq(1L), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(debtPage);
        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        // When
        service.getDebtsByCustomer(1L, null, null, 0, 10, "amount,asc");

        // Then
        verify(debtRepository).findByCustomerAndFilter(eq(1L), isNull(), isNull(),
                argThat(pageable -> pageable.getSort().getOrderFor("amount") != null &&
                        pageable.getSort().getOrderFor("amount").getDirection() == Sort.Direction.ASC));
    }

    /**
     * UTCID146: Sort parameter: "createdAt,desc" (default)
     * Precondition: Sort parameter: "createdAt,desc" (default)
     * Input: sort=null
     * Expected: Returns sorted debts by createdAt descending (default)
     * Type: N (Normal)
     */
    @Test
    void getDebtsByCustomer_UTCID146_ShouldUseDefaultSort_WhenSortIsNull() {
        // Given
        Page<Debt> debtPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(debtRepository.findByCustomerAndFilter(eq(1L), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(debtPage);
        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        // When
        service.getDebtsByCustomer(1L, null, null, 0, 10, null);

        // Then
        verify(debtRepository).findByCustomerAndFilter(eq(1L), isNull(), isNull(),
                argThat(pageable -> pageable.getSort().getOrderFor("createdAt") != null &&
                        pageable.getSort().getOrderFor("createdAt").getDirection() == Sort.Direction.DESC));
    }

    /**
     * UTCID147: Customer has vehicles
     * Precondition: Customer has vehicles
     * Input: customer.vehicles not empty
     * Expected: licensePlate = first vehicle's licensePlate
     * Type: N (Normal)
     */
    @Test
    void getDebtsByCustomer_UTCID147_ShouldSetLicensePlate_WhenCustomerHasVehicles() {
        // Given
        Vehicle vehicle = Vehicle.builder()
                .vehicleId(1L)
                .licensePlate("30A-12345")
                .build();
        customer.setVehicles(List.of(vehicle));

        Page<Debt> debtPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(debtRepository.findByCustomerAndFilter(eq(1L), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(debtPage);
        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        // When
        DebtDetailResponseDto result = service.getDebtsByCustomer(1L, null, null, 0, 10, null);

        // Then
        assertEquals("30A-12345", result.getLicensePlate());
    }

    /**
     * UTCID148: Customer has no vehicles
     * Precondition: Customer has no vehicles
     * Input: customer.vehicles is empty
     * Expected: licensePlate = null
     * Type: N (Normal)
     */
    @Test
    void getDebtsByCustomer_UTCID148_ShouldSetNullLicensePlate_WhenCustomerHasNoVehicles() {
        // Given
        customer.setVehicles(Collections.emptyList());
        Page<Debt> debtPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(debtRepository.findByCustomerAndFilter(eq(1L), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(debtPage);
        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        // When
        DebtDetailResponseDto result = service.getDebtsByCustomer(1L, null, null, 0, 10, null);

        // Then
        assertNull(result.getLicensePlate());
    }

    /**
     * UTCID149: Total remaining amount calculation
     * Precondition: Total remaining amount calculation
     * Input: Multiple debts with different amounts
     * Expected: totalRemainingAmount = sum of all debt amounts
     * Type: N (Normal)
     */
    @Test
    void getDebtsByCustomer_UTCID149_ShouldCalculateTotalRemaining_WhenMultipleDebts() {
        // Given
        Debt debt1 = Debt.builder().id(1L).amount(new BigDecimal("1000000")).build();
        Debt debt2 = Debt.builder().id(2L).amount(new BigDecimal("500000")).build();
        Debt debt3 = Debt.builder().id(3L).amount(new BigDecimal("200000")).build();

        Page<Debt> debtPage = new PageImpl<>(List.of(debt1, debt2, debt3), PageRequest.of(0, 10), 3);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(debtRepository.findByCustomerAndFilter(eq(1L), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(debtPage);
        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        // When
        DebtDetailResponseDto result = service.getDebtsByCustomer(1L, null, null, 0, 10, null);

        // Then
        assertEquals(new BigDecimal("1700000"), result.getTotalRemainingAmount());
    }

    /**
     * UTCID150: Boundary: page = Integer.MAX_VALUE
     * Precondition: Boundary: page = Integer.MAX_VALUE
     * Input: page=Integer.MAX_VALUE
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void getDebtsByCustomer_UTCID150_ShouldHandleBoundary_WhenPageIsMaxValue() {
        // Given
        Page<Debt> debtPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(Integer.MAX_VALUE, 10), 0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(debtRepository.findByCustomerAndFilter(eq(1L), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(debtPage);
        when(customerDebtMapper.toDto(anyList())).thenReturn(Collections.emptyList());

        // When & Then
        assertDoesNotThrow(() -> service.getDebtsByCustomer(1L, null, null, Integer.MAX_VALUE, 10, null));
    }

    /**
     * UTCID151: Boundary: customerId = Long.MAX_VALUE
     * Precondition: Boundary: customerId = Long.MAX_VALUE
     * Input: customerId=Long.MAX_VALUE
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void getDebtsByCustomer_UTCID151_ShouldHandleBoundary_WhenCustomerIdIsMaxValue() {
        // Given
        Long maxCustomerId = Long.MAX_VALUE;
        when(customerRepository.findById(maxCustomerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> service.getDebtsByCustomer(maxCustomerId, null, null, 0, 10, null));
    }

    // ========== MATRIX 16: createDebt (DE-003) - UTCID182-UTCID193 ==========

    /**
     * UTCID182: Valid customer and service ticket
     * Precondition: Valid customer and service ticket
     * Input: customerId=1L, serviceTicketId=1L, amount=1000000
     * Expected: Creates debt with dueDate = today + 14 days
     * Type: N (Normal)
     */
    @Test
    void createDebt_UTCID182_ShouldCreateDebt_WhenValidCustomerAndTicket() {
        // Given
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(1L)
                .serviceTicketId(1L)
                .amount(new BigDecimal("1000000"))
                .build();

        Debt savedDebt = Debt.builder()
                .id(1L)
                .customer(customer)
                .serviceTicket(serviceTicket)
                .amount(new BigDecimal("1000000"))
                .dueDate(LocalDate.now().plusDays(14))
                .build();

        DebtDetailResponseDto responseDto = DebtDetailResponseDto.builder()
                .customerName(customer.getFullName())
                .totalRemainingAmount(new BigDecimal("1000000"))
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(debtRepository.save(any(Debt.class))).thenReturn(savedDebt);
        when(debtMapper.toDto(savedDebt)).thenReturn(responseDto);

        // When
        DebtDetailResponseDto result = service.createDebt(dto);

        // Then
        assertNotNull(result);
        verify(debtRepository).save(argThat(debt ->
            debt.getCustomer().equals(customer) &&
            debt.getServiceTicket().equals(serviceTicket) &&
            debt.getAmount().compareTo(new BigDecimal("1000000")) == 0 &&
            debt.getDueDate().equals(LocalDate.now().plusDays(14))
        ));
    }

    /**
     * UTCID183: Customer not found
     * Precondition: Customer not found
     * Input: customerId=999L
     * Expected: Throws CustomerNotFoundException
     * Type: A (Abnormal)
     */
    @Test
    void createDebt_UTCID183_ShouldThrowException_WhenCustomerNotFound() {
        // Given
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(999L)
                .serviceTicketId(1L)
                .amount(new BigDecimal("1000000"))
                .build();
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> service.createDebt(dto));
    }

    /**
     * UTCID184: Service ticket not found
     * Precondition: Service ticket not found
     * Input: serviceTicketId=999L
     * Expected: Throws ServiceTicketNotFoundException
     * Type: A (Abnormal)
     */
    @Test
    void createDebt_UTCID184_ShouldThrowException_WhenServiceTicketNotFound() {
        // Given
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(1L)
                .serviceTicketId(999L)
                .amount(new BigDecimal("1000000"))
                .build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ServiceTicketNotFoundException.class, () -> service.createDebt(dto));
    }

    /**
     * UTCID185: Amount = 0
     * Precondition: Amount = 0
     * Input: amount=0
     * Expected: Either throws exception or allows
     * Type: B (Boundary)
     */
    @Test
    void createDebt_UTCID185_ShouldHandle_WhenAmountIsZero() {
        // Given
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(1L)
                .serviceTicketId(1L)
                .amount(BigDecimal.ZERO)
                .build();

        Debt savedDebt = Debt.builder()
                .id(1L)
                .amount(BigDecimal.ZERO)
                .build();

        DebtDetailResponseDto responseDto = DebtDetailResponseDto.builder().build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(debtRepository.save(any(Debt.class))).thenReturn(savedDebt);
        when(debtMapper.toDto(savedDebt)).thenReturn(responseDto);

        // When & Then
        assertDoesNotThrow(() -> service.createDebt(dto));
    }

    /**
     * UTCID186: Amount = BigDecimal.MAX_VALUE
     * Precondition: Amount = BigDecimal.MAX_VALUE
     * Input: amount=BigDecimal.MAX_VALUE
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void createDebt_UTCID186_ShouldHandle_WhenAmountIsMaxValue() {
        // Given
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(1L)
                .serviceTicketId(1L)
                .amount(BigDecimal.valueOf(Long.MAX_VALUE))
                .build();

        Debt savedDebt = Debt.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(Long.MAX_VALUE))
                .build();

        DebtDetailResponseDto responseDto = DebtDetailResponseDto.builder().build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(debtRepository.save(any(Debt.class))).thenReturn(savedDebt);
        when(debtMapper.toDto(savedDebt)).thenReturn(responseDto);

        // When & Then
        assertDoesNotThrow(() -> service.createDebt(dto));
    }

    /**
     * UTCID187: Database save fails
     * Precondition: Database save fails
     * Input: repository.save() throws exception
     * Expected: Throws DataAccessException
     * Type: A (Abnormal)
     */
    @Test
    void createDebt_UTCID187_ShouldThrowException_WhenDatabaseSaveFails() {
        // Given
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(1L)
                .serviceTicketId(1L)
                .amount(new BigDecimal("1000000"))
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(debtRepository.save(any(Debt.class)))
                .thenThrow(new DataAccessException("Database error") {});

        // When & Then
        assertThrows(DataAccessException.class, () -> service.createDebt(dto));
    }

    /**
     * UTCID188: Due date calculation
     * Precondition: Due date calculation
     * Input: All valid
     * Expected: dueDate = today + 14 days
     * Type: N (Normal)
     */
    @Test
    void createDebt_UTCID188_ShouldSetDueDate_WhenAllValid() {
        // Given
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(1L)
                .serviceTicketId(1L)
                .amount(new BigDecimal("1000000"))
                .build();

        Debt savedDebt = Debt.builder()
                .id(1L)
                .dueDate(LocalDate.now().plusDays(14))
                .build();

        DebtDetailResponseDto responseDto = DebtDetailResponseDto.builder().build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(debtRepository.save(any(Debt.class))).thenReturn(savedDebt);
        when(debtMapper.toDto(savedDebt)).thenReturn(responseDto);

        // When
        service.createDebt(dto);

        // Then
        verify(debtRepository).save(argThat(debt ->
            debt.getDueDate().equals(LocalDate.now().plusDays(14))
        ));
    }

    /**
     * UTCID189: Debt status default
     * Precondition: Debt status default
     * Input: All valid
     * Expected: status = OUTSTANDING (default)
     * Type: N (Normal)
     */
    @Test
    void createDebt_UTCID189_ShouldSetDefaultStatus_WhenAllValid() {
        // Given
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(1L)
                .serviceTicketId(1L)
                .amount(new BigDecimal("1000000"))
                .build();

        Debt savedDebt = Debt.builder()
                .id(1L)
                .status(DebtStatus.OUTSTANDING)
                .build();

        DebtDetailResponseDto responseDto = DebtDetailResponseDto.builder().build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(debtRepository.save(any(Debt.class))).thenReturn(savedDebt);
        when(debtMapper.toDto(savedDebt)).thenReturn(responseDto);

        // When
        service.createDebt(dto);

        // Then
        verify(debtRepository).save(argThat(debt ->
            debt.getStatus() == DebtStatus.OUTSTANDING
        ));
    }

    /**
     * UTCID190: Paid amount default
     * Precondition: Paid amount default
     * Input: All valid
     * Expected: paidAmount = 0 (default)
     * Type: N (Normal)
     */
    @Test
    void createDebt_UTCID190_ShouldSetDefaultPaidAmount_WhenAllValid() {
        // Given
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(1L)
                .serviceTicketId(1L)
                .amount(new BigDecimal("1000000"))
                .build();

        Debt savedDebt = Debt.builder()
                .id(1L)
                .paidAmount(BigDecimal.ZERO)
                .build();

        DebtDetailResponseDto responseDto = DebtDetailResponseDto.builder().build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(debtRepository.save(any(Debt.class))).thenReturn(savedDebt);
        when(debtMapper.toDto(savedDebt)).thenReturn(responseDto);

        // When
        service.createDebt(dto);

        // Then
        verify(debtRepository).save(argThat(debt ->
            debt.getPaidAmount().compareTo(BigDecimal.ZERO) == 0
        ));
    }

    /**
     * UTCID191: Boundary: customerId = Long.MAX_VALUE
     * Precondition: Boundary: customerId = Long.MAX_VALUE
     * Input: customerId=Long.MAX_VALUE
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void createDebt_UTCID191_ShouldHandleBoundary_WhenCustomerIdIsMaxValue() {
        // Given
        Long maxCustomerId = Long.MAX_VALUE;
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(maxCustomerId)
                .serviceTicketId(1L)
                .amount(new BigDecimal("1000000"))
                .build();
        when(customerRepository.findById(maxCustomerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> service.createDebt(dto));
    }

    /**
     * UTCID192: Boundary: serviceTicketId = Long.MAX_VALUE
     * Precondition: Boundary: serviceTicketId = Long.MAX_VALUE
     * Input: serviceTicketId=Long.MAX_VALUE
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void createDebt_UTCID192_ShouldHandleBoundary_WhenServiceTicketIdIsMaxValue() {
        // Given
        Long maxTicketId = Long.MAX_VALUE;
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(1L)
                .serviceTicketId(maxTicketId)
                .amount(new BigDecimal("1000000"))
                .build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(maxTicketId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ServiceTicketNotFoundException.class, () -> service.createDebt(dto));
    }

    /**
     * UTCID193: Mapper returns DTO
     * Precondition: Mapper returns DTO
     * Input: All valid
     * Expected: Returns mapped DTO
     * Type: N (Normal)
     */
    @Test
    void createDebt_UTCID193_ShouldReturnMappedDto_WhenAllValid() {
        // Given
        CreateDebtDto dto = CreateDebtDto.builder()
                .customerId(1L)
                .serviceTicketId(1L)
                .amount(new BigDecimal("1000000"))
                .build();

        Debt savedDebt = Debt.builder()
                .id(1L)
                .build();

        DebtDetailResponseDto responseDto = DebtDetailResponseDto.builder()
                .customerName("Test Customer")
                .totalRemainingAmount(new BigDecimal("1000000"))
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(debtRepository.save(any(Debt.class))).thenReturn(savedDebt);
        when(debtMapper.toDto(savedDebt)).thenReturn(responseDto);

        // When
        DebtDetailResponseDto result = service.createDebt(dto);

        // Then
        assertNotNull(result);
        assertEquals("Test Customer", result.getCustomerName());
        verify(debtMapper).toDto(savedDebt);
    }

    // ========== MATRIX 17: updateDueDate (DE-004) - UTCID194-UTCID203 ==========

    /**
     * UTCID194: Valid debt, dueDate after today
     * Precondition: Valid debt, dueDate after today
     * Input: debtId=1L, dueDate=today+7
     * Expected: Updates debt.dueDate
     * Type: N (Normal)
     */
    @Test
    void updateDueDate_UTCID194_ShouldUpdateDueDate_WhenDueDateAfterToday() {
        // Given
        LocalDate newDueDate = LocalDate.now().plusDays(7);
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        when(debtRepository.save(any(Debt.class))).thenReturn(debt);

        // When
        service.updateDueDate(1L, newDueDate);

        // Then
        verify(debtRepository).save(argThat(d ->
            d.getDueDate().equals(newDueDate)
        ));
    }

    /**
     * UTCID195: Debt not found
     * Precondition: Debt not found
     * Input: debtId=999L
     * Expected: Throws DebtNotFoundException
     * Type: A (Abnormal)
     */
    @Test
    void updateDueDate_UTCID195_ShouldThrowException_WhenDebtNotFound() {
        // Given
        when(debtRepository.findById(999L)).thenReturn(Optional.empty());
        LocalDate newDueDate = LocalDate.now().plusDays(7);

        // When & Then
        assertThrows(DebtNotFoundException.class, () -> service.updateDueDate(999L, newDueDate));
    }

    /**
     * UTCID196: DueDate = null
     * Precondition: DueDate = null
     * Input: dueDate=null
     * Expected: Throws IllegalArgumentException
     * Type: A (Abnormal)
     */
    @Test
    void updateDueDate_UTCID196_ShouldThrowException_WhenDueDateIsNull() {
        // Given
        // Note: debtRepository.findById() is not called when dueDate is null, as validation happens first

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> service.updateDueDate(1L, null));
    }

    /**
     * UTCID197: DueDate = today
     * Precondition: DueDate = today
     * Input: dueDate=today
     * Expected: Throws IllegalArgumentException
     * Type: A (Abnormal)
     */
    @Test
    void updateDueDate_UTCID197_ShouldThrowException_WhenDueDateIsToday() {
        // Given
        // Note: debtRepository.findById() is not called when dueDate <= today, as validation happens first
        LocalDate today = LocalDate.now();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> service.updateDueDate(1L, today));
    }

    /**
     * UTCID198: DueDate before today
     * Precondition: DueDate before today
     * Input: dueDate=today-1
     * Expected: Throws IllegalArgumentException
     * Type: A (Abnormal)
     */
    @Test
    void updateDueDate_UTCID198_ShouldThrowException_WhenDueDateBeforeToday() {
        // Given
        // Note: debtRepository.findById() is not called when dueDate <= today, as validation happens first
        LocalDate pastDate = LocalDate.now().minusDays(1);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> service.updateDueDate(1L, pastDate));
    }

    /**
     * UTCID199: DueDate = today + 1 day
     * Precondition: DueDate = today + 1 day
     * Input: dueDate=today+1
     * Expected: Updates successfully
     * Type: B (Boundary)
     */
    @Test
    void updateDueDate_UTCID199_ShouldUpdate_WhenDueDateIsTomorrow() {
        // Given
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        when(debtRepository.save(any(Debt.class))).thenReturn(debt);

        // When
        service.updateDueDate(1L, tomorrow);

        // Then
        verify(debtRepository).save(argThat(d ->
            d.getDueDate().equals(tomorrow)
        ));
    }

    /**
     * UTCID200: Database save fails
     * Precondition: Database save fails
     * Input: repository.save() throws exception
     * Expected: Throws DataAccessException
     * Type: A (Abnormal)
     */
    @Test
    void updateDueDate_UTCID200_ShouldThrowException_WhenDatabaseSaveFails() {
        // Given
        LocalDate newDueDate = LocalDate.now().plusDays(7);
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        when(debtRepository.save(any(Debt.class)))
                .thenThrow(new DataAccessException("Database error") {});

        // When & Then
        assertThrows(DataAccessException.class, () -> service.updateDueDate(1L, newDueDate));
    }

    /**
     * UTCID201: DueDate = LocalDate.MAX
     * Precondition: DueDate = LocalDate.MAX
     * Input: dueDate=LocalDate.MAX
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void updateDueDate_UTCID201_ShouldHandle_WhenDueDateIsMaxValue() {
        // Given
        LocalDate maxDate = LocalDate.MAX;
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        when(debtRepository.save(any(Debt.class))).thenReturn(debt);

        // When & Then
        assertDoesNotThrow(() -> service.updateDueDate(1L, maxDate));
    }

    /**
     * UTCID202: Boundary: debtId = Long.MAX_VALUE
     * Precondition: Boundary: debtId = Long.MAX_VALUE
     * Input: debtId=Long.MAX_VALUE
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void updateDueDate_UTCID202_ShouldHandleBoundary_WhenDebtIdIsMaxValue() {
        // Given
        Long maxDebtId = Long.MAX_VALUE;
        LocalDate newDueDate = LocalDate.now().plusDays(7);
        when(debtRepository.findById(maxDebtId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DebtNotFoundException.class, () -> service.updateDueDate(maxDebtId, newDueDate));
    }

    /**
     * UTCID203: Multiple updates
     * Precondition: Multiple updates
     * Input: Update same debt multiple times
     * Expected: Each update succeeds
     * Type: N (Normal)
     */
    @Test
    void updateDueDate_UTCID203_ShouldHandle_WhenMultipleUpdates() {
        // Given
        LocalDate firstDate = LocalDate.now().plusDays(7);
        LocalDate secondDate = LocalDate.now().plusDays(14);
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        when(debtRepository.save(any(Debt.class))).thenReturn(debt);

        // When
        service.updateDueDate(1L, firstDate);
        service.updateDueDate(1L, secondDate);

        // Then
        verify(debtRepository, times(2)).save(any(Debt.class));
    }

}

