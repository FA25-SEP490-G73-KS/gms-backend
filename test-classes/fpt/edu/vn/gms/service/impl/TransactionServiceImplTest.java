package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.*;
import fpt.edu.vn.gms.dto.TransactionResponseDto;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.request.TransactionCallbackDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.TransactionNotFoundException;
import fpt.edu.vn.gms.mapper.TransactionMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import org.mockito.Mockito;
import vn.payos.service.blocking.v2.paymentRequests.PaymentRequestsService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    // Temporary interface to mock PaymentRequests
    @SuppressWarnings("unused")
    private interface PaymentRequestsMock {
        CreatePaymentLinkResponse create(CreatePaymentLinkRequest request);
        CreatePaymentLinkResponse get(String paymentLinkId);
    }

    @Mock
    PayOS payOS;
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    TransactionMapper transactionMapper;
    @Mock
    DebtRepository debtRepository;
    @Mock
    InvoiceRepository invoiceRepository;
    @Mock
    CustomerRepository customerRepository;
    @Mock
    CustomerService customerService;

    @InjectMocks
    TransactionServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "returnUrl", "http://localhost:3000");
    }

    @Test
    void createTransaction_ShouldCreateCashTransaction() throws Exception {
        CreateTransactionRequestDto request = CreateTransactionRequestDto.builder()
                .customerFullName("Customer Name")
                .customerPhone("0912345678")
                .price(1000000L)
                .type(PaymentTransactionType.PAYMENT)
                .method(TransactionMethod.CASH)
                .build();

        Transaction savedTransaction = Transaction.builder()
                .id(1L)
                .customerFullName("Customer Name")
                .amount(1000000L)
                .isActive(true)
                .build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponseDto dto = TransactionResponseDto.builder()
                .id(1L)
                .amount(1000000L)
                .build();
        when(transactionMapper.toResponseDto(savedTransaction)).thenReturn(dto);

        TransactionResponseDto result = service.createTransaction(request);

        assertSame(dto, result);
        assertTrue(savedTransaction.getIsActive());
        verify(transactionRepository).save(any(Transaction.class));
        verify(payOS, never()).paymentRequests();
    }

    @Test
    void createTransaction_ShouldCreateBankTransferTransaction() throws Exception {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .serviceTicket(ServiceTicket.builder()
                        .serviceTicketCode("STK-2025-00001")
                        .build())
                .build();

        CreateTransactionRequestDto request = CreateTransactionRequestDto.builder()
                .invoice(invoice)
                .customerFullName("Customer Name")
                .customerPhone("0912345678")
                .price(2000000L)
                .type(PaymentTransactionType.PAYMENT)
                .method(TransactionMethod.BANK_TRANSFER)
                .build();

        CreatePaymentLinkResponse payOSResponse = mock(CreatePaymentLinkResponse.class);
        when(payOSResponse.getPaymentLinkId()).thenReturn("payment-link-123");
        when(payOSResponse.getCheckoutUrl()).thenReturn("http://payos.checkout.url");

        // Mock PaymentRequests using a temporary interface
        PaymentRequestsMock paymentRequests = mock(PaymentRequestsMock.class);
        when(payOS.paymentRequests()).thenReturn((PaymentRequestsService) paymentRequests);
        when(paymentRequests.create(any(CreatePaymentLinkRequest.class))).thenReturn(payOSResponse);

        Transaction savedTransaction = Transaction.builder()
                .id(1L)
                .paymentLinkId("payment-link-123")
                .isActive(false)
                .build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponseDto dto = TransactionResponseDto.builder()
                .id(1L)
                .build();
        when(transactionMapper.toResponseDto(savedTransaction)).thenReturn(dto);

        TransactionResponseDto result = service.createTransaction(request);

        assertSame(dto, result);
        assertEquals("http://payos.checkout.url", result.getPaymentUrl());
        assertEquals("payment-link-123", savedTransaction.getPaymentLinkId());
        assertFalse(savedTransaction.getIsActive());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void handleCallback_ShouldActivateTransactionAndUpdateInvoice_WhenPaid() {
        Customer customer = Customer.builder()
                .customerId(1L)
                .build();
        ServiceTicket serviceTicket = ServiceTicket.builder()
                .serviceTicketId(100L)
                .customer(customer)
                .build();
        Invoice invoice = Invoice.builder()
                .id(1L)
                .serviceTicket(serviceTicket)
                .finalAmount(new BigDecimal("5000000"))
                .depositReceived(BigDecimal.ZERO)
                .status(InvoiceStatus.PENDING)
                .build();

        Transaction transaction = Transaction.builder()
                .id(1L)
                .invoice(invoice)
                .amount(5000000L)
                .type(PaymentTransactionType.PAYMENT)
                .isActive(false)
                .build();

        when(transactionRepository.findByPaymentLinkId("payment-link-123"))
                .thenReturn(Optional.of(transaction));

        CreatePaymentLinkResponse paymentInfo = mock(CreatePaymentLinkResponse.class);
        when(paymentInfo.getStatus()).thenReturn(PaymentLinkStatus.PAID);
        // Mock PaymentRequests using a temporary interface
        PaymentRequestsMock paymentRequests = mock(PaymentRequestsMock.class);
        when(payOS.paymentRequests()).thenReturn((PaymentRequestsService) paymentRequests);
        when(paymentRequests.get("payment-link-123")).thenReturn(paymentInfo);

        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionCallbackDto callbackDto = new TransactionCallbackDto("payment-link-123");

        service.handleCallback(callbackDto);

        assertTrue(transaction.getIsActive());
        assertEquals(InvoiceStatus.PAID_IN_FULL, invoice.getStatus());
        verify(transactionRepository).save(transaction);
        verify(invoiceRepository).save(invoice);
        verify(customerService).updateTotalSpending(1L, new BigDecimal("5000000"));
    }

    @Test
    void handleCallback_ShouldUpdateDebt_WhenPaid() {
        Customer customer = Customer.builder()
                .customerId(1L)
                .build();
        Debt debt = Debt.builder()
                .id(1L)
                .customer(customer)
                .amount(new BigDecimal("3000000"))
                .paidAmount(BigDecimal.ZERO)
                .status(DebtStatus.OUTSTANDING)
                .build();

        Transaction transaction = Transaction.builder()
                .id(1L)
                .debt(debt)
                .amount(3000000L)
                .type(PaymentTransactionType.PAYMENT)
                .isActive(false)
                .build();

        when(transactionRepository.findByPaymentLinkId("payment-link-123"))
                .thenReturn(Optional.of(transaction));

        CreatePaymentLinkResponse paymentInfo = mock(CreatePaymentLinkResponse.class);
        when(paymentInfo.getStatus()).thenReturn(PaymentLinkStatus.PAID);
        // Mock PaymentRequests using a temporary interface
        PaymentRequestsMock paymentRequests = mock(PaymentRequestsMock.class);
        when(payOS.paymentRequests()).thenReturn((PaymentRequestsService) paymentRequests);
        when(paymentRequests.get("payment-link-123")).thenReturn(paymentInfo);

        when(debtRepository.save(debt)).thenReturn(debt);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionCallbackDto callbackDto = new TransactionCallbackDto("payment-link-123");

        service.handleCallback(callbackDto);

        assertTrue(transaction.getIsActive());
        assertEquals(DebtStatus.PAID_IN_FULL, debt.getStatus());
        assertEquals(new BigDecimal("3000000"), debt.getPaidAmount());
        verify(debtRepository).save(debt);
        verify(customerService).updateTotalSpending(1L, new BigDecimal("3000000"));
    }

    @Test
    void handleCallback_ShouldDeleteTransaction_WhenCancelled() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .isActive(false)
                .build();

        when(transactionRepository.findByPaymentLinkId("payment-link-123"))
                .thenReturn(Optional.of(transaction));

        CreatePaymentLinkResponse paymentInfo = mock(CreatePaymentLinkResponse.class);
        when(paymentInfo.getStatus()).thenReturn(PaymentLinkStatus.CANCELLED);
        // Mock PaymentRequests using a temporary interface
        PaymentRequestsMock paymentRequests = mock(PaymentRequestsMock.class);
        when(payOS.paymentRequests()).thenReturn((PaymentRequestsService) paymentRequests);
        when(paymentRequests.get("payment-link-123")).thenReturn(paymentInfo);

        TransactionCallbackDto callbackDto = new TransactionCallbackDto("payment-link-123");

        service.handleCallback(callbackDto);

        verify(transactionRepository).delete(transaction);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void handleCallback_ShouldThrow_WhenTransactionNotFound() {
        when(transactionRepository.findByPaymentLinkId("payment-link-123"))
                .thenReturn(Optional.empty());

        TransactionCallbackDto callbackDto = new TransactionCallbackDto("payment-link-123");

        assertThrows(TransactionNotFoundException.class,
                () -> service.handleCallback(callbackDto));
        verify(transactionRepository).findByPaymentLinkId("payment-link-123");
    }

    @Test
    void handleCallback_ShouldUpdateInvoiceDeposit_WhenDepositType() {
        Customer customer = Customer.builder()
                .customerId(1L)
                .build();
        ServiceTicket serviceTicket = ServiceTicket.builder()
                .serviceTicketId(100L)
                .customer(customer)
                .build();
        Invoice invoice = Invoice.builder()
                .id(1L)
                .serviceTicket(serviceTicket)
                .finalAmount(new BigDecimal("10000000"))
                .depositReceived(BigDecimal.ZERO)
                .status(InvoiceStatus.PENDING)
                .build();

        Transaction transaction = Transaction.builder()
                .id(1L)
                .invoice(invoice)
                .amount(2000000L)
                .type(PaymentTransactionType.DEPOSIT)
                .isActive(false)
                .build();

        when(transactionRepository.findByPaymentLinkId("payment-link-123"))
                .thenReturn(Optional.of(transaction));

        CreatePaymentLinkResponse paymentInfo = mock(CreatePaymentLinkResponse.class);
        when(paymentInfo.getStatus()).thenReturn(PaymentLinkStatus.PAID);
        // Mock PaymentRequests using a temporary interface
        PaymentRequestsMock paymentRequests = mock(PaymentRequestsMock.class);
        when(payOS.paymentRequests()).thenReturn((PaymentRequestsService) paymentRequests);
        when(paymentRequests.get("payment-link-123")).thenReturn(paymentInfo);

        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionCallbackDto callbackDto = new TransactionCallbackDto("payment-link-123");

        service.handleCallback(callbackDto);

        assertEquals(new BigDecimal("2000000"), invoice.getDepositReceived());
        assertEquals(new BigDecimal("8000000"), invoice.getFinalAmount());
        verify(customerService).updateTotalSpending(1L, new BigDecimal("2000000"));
    }
}

