package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.common.enums.InvoiceStatus;
import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import fpt.edu.vn.gms.common.enums.TransactionMethod;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.request.PayInvoiceRequestDto;
import fpt.edu.vn.gms.dto.response.TransactionResponseDto;
import fpt.edu.vn.gms.dto.response.CustomerDebtResponseDto;
import fpt.edu.vn.gms.dto.response.InvoiceDetailResDto;
import fpt.edu.vn.gms.dto.response.InvoiceListResDto;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.Debt;
import fpt.edu.vn.gms.entity.Invoice;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.entity.Transaction;
import fpt.edu.vn.gms.exception.PaymentNotFoundException;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.CustomerDebtMapper;
import fpt.edu.vn.gms.mapper.InvoiceMapper;
import fpt.edu.vn.gms.repository.DebtRepository;
import fpt.edu.vn.gms.repository.InvoiceRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.repository.TransactionRepository;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.CustomerService;
import fpt.edu.vn.gms.service.TransactionService;
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
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    ServiceTicketRepository serviceTicketRepo;
    @Mock
    PriceQuotationRepository priceQuotationRepo;
    @Mock
    InvoiceRepository invoiceRepo;
    @Mock
    DebtRepository debtRepo;
    @Mock
    TransactionRepository transactionRepo;
    @Mock
    TransactionService transactionService;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    CustomerDebtMapper customerDebtMapper;
    @Mock
    InvoiceMapper mapper;
    @Mock
    CustomerService customerService;

    @InjectMocks
    InvoiceServiceImpl service;

    @Captor
    ArgumentCaptor<Invoice> invoiceCaptor;

    private ServiceTicket serviceTicket;
    private PriceQuotation quotation;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .customerId(1L)
                .fullName("Nguyen Van A")
                .build();

        serviceTicket = ServiceTicket.builder()
                .serviceTicketId(10L)
                .customer(customer)
                .customerName("Nguyen Van A")
                .customerPhone("0909000000")
                .build();

        quotation = PriceQuotation.builder()
                .priceQuotationId(100L)
                .estimateAmount(new BigDecimal("1000000"))
                .build();

        customer.setDiscountPolicy(
                fpt.edu.vn.gms.entity.DiscountPolicy.builder()
                        .discountRate(new BigDecimal("10"))
                        .build()
        );
    }

    @Test
    void createInvoice_ShouldCalculateAmountsAndSaveInvoice_WhenValid() {
        when(serviceTicketRepo.findById(10L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(100L)).thenReturn(Optional.of(quotation));
        when(debtRepo.getTotalDebt(1L)).thenReturn(new BigDecimal("200000"));
        when(codeSequenceService.generateCode("HD")).thenReturn("HD001");

        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.createInvoice(10L, 100L);

        verify(invoiceRepo).save(invoiceCaptor.capture());
        Invoice saved = invoiceCaptor.getValue();

        // itemTotal = 1,000,000; discountRate=10%; discount=100,000
        // previousDebt=200,000; deposit=0
        // finalAmount = 1,000,000 - 100,000 + 200,000 = 1,100,000
        assertEquals(new BigDecimal("1100000"), saved.getFinalAmount());
        assertEquals("HD001", saved.getCode());
        assertEquals(serviceTicket, saved.getServiceTicket());
        assertEquals(quotation, saved.getQuotation());
        assertEquals(BigDecimal.ZERO, saved.getDepositReceived());
    }

    @Test
    void createInvoice_ShouldThrow_WhenServiceTicketNotFound() {
        when(serviceTicketRepo.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.createInvoice(10L, 100L));
    }

    @Test
    void createInvoice_ShouldThrow_WhenQuotationNotFound() {
        when(serviceTicketRepo.findById(10L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.createInvoice(10L, 100L));
    }

    @Test
    void createInvoice_ShouldThrow_WhenCustomerNull() {
        ServiceTicket stWithoutCustomer = ServiceTicket.builder()
                .serviceTicketId(11L)
                .customer(null)
                .build();
        when(serviceTicketRepo.findById(11L)).thenReturn(Optional.of(stWithoutCustomer));
        when(priceQuotationRepo.findById(100L)).thenReturn(Optional.of(quotation));

        assertThrows(ResourceNotFoundException.class,
                () -> service.createInvoice(11L, 100L));
    }

    @Test
    void getInvoiceList_ShouldReturnMappedPage() {
        String sort = "createdAt,desc";
        int page = 0;
        int size = 5;

        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Invoice invoice = Invoice.builder()
                .id(1L)
                .build();
        Page<Invoice> entityPage = new PageImpl<>(List.of(invoice), pageable, 1);
        InvoiceListResDto dto = InvoiceListResDto.builder().build();

        when(invoiceRepo.findAllWithRelations(pageable)).thenReturn(entityPage);
        when(mapper.toListDto(invoice)).thenReturn(dto);

        Page<InvoiceListResDto> result = service.getInvoiceList(page, size, sort);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(invoiceRepo).findAllWithRelations(pageable);
    }

    @Test
    void getInvoiceDetail_ShouldReturnDto_WhenFound() {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .build();
        InvoiceDetailResDto dto = InvoiceDetailResDto.builder().build();

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
        when(mapper.toDetailDto(invoice)).thenReturn(dto);

        InvoiceDetailResDto result = service.getInvoiceDetail(1L);
        assertSame(dto, result);
        verify(invoiceRepo).findById(1L);
        verify(mapper).toDetailDto(invoice);
    }

    @Test
    void getInvoiceDetail_ShouldThrow_WhenNotFound() {
        when(invoiceRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getInvoiceDetail(1L));
    }

    @Test
    void createDebtFromInvoice_ShouldReturnNull_WhenNoRemainingDebt() {
        Invoice payment = Invoice.builder()
                .id(1L)
                .finalAmount(new BigDecimal("500000"))
                .serviceTicket(serviceTicket)
                .build();

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(payment));

        Transaction transaction = Transaction.builder()
                .id(10L)
                .amount(500000L)
                .build();
        when(transactionRepo.findByInvoiceAndIsActiveTrue(payment))
                .thenReturn(List.of(transaction));

        CustomerDebtResponseDto result = service.createDebtFromInvoice(1L, LocalDate.now());

        assertNull(result);
        verify(debtRepo, never()).save(any(Debt.class));
    }

    @Test
    void createDebtFromInvoice_ShouldCreateDebt_WhenThereIsRemainingAmount() {
        Invoice payment = Invoice.builder()
                .id(1L)
                .finalAmount(new BigDecimal("500000"))
                .serviceTicket(serviceTicket)
                .build();

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(payment));

        Transaction transaction = Transaction.builder()
                .id(10L)
                .amount(200000L)
                .build();
        when(transactionRepo.findByInvoiceAndIsActiveTrue(payment))
                .thenReturn(List.of(transaction));

        Debt savedDebt = Debt.builder()
                .id(99L)
                .customer(customer)
                .serviceTicket(serviceTicket)
                .amount(new BigDecimal("300000"))
                .paidAmount(BigDecimal.ZERO)
                .status(DebtStatus.OUTSTANDING)
                .dueDate(LocalDate.now().plusDays(30))
                .build();

        when(debtRepo.save(any(Debt.class))).thenReturn(savedDebt);

        CustomerDebtResponseDto dto = CustomerDebtResponseDto.builder()
                .id(99L)
                .build();
        when(customerDebtMapper.toDto(savedDebt)).thenReturn(dto);

        LocalDate dueDate = LocalDate.of(2025, 1, 1);
        CustomerDebtResponseDto result = service.createDebtFromInvoice(1L, dueDate);

        assertNotNull(result);
        assertEquals(new BigDecimal("300000"), result.getTotalAmount());
        assertEquals(BigDecimal.ZERO, result.getPaidAmount());

        verify(debtRepo).save(any(Debt.class));
        verify(customerDebtMapper).toDto(savedDebt);
    }

    @Test
    void createDebtFromInvoice_ShouldUseDefaultDueDate_WhenNull() {
        Invoice payment = Invoice.builder()
                .id(1L)
                .finalAmount(new BigDecimal("500000"))
                .serviceTicket(serviceTicket)
                .build();

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(payment));
        when(transactionRepo.findByInvoiceAndIsActiveTrue(payment))
                .thenReturn(Collections.emptyList());

        when(debtRepo.save(any(Debt.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerDebtMapper.toDto(any(Debt.class)))
                .thenReturn(CustomerDebtResponseDto.builder().build());

        CustomerDebtResponseDto result = service.createDebtFromInvoice(1L, null);

        assertNotNull(result);
        verify(debtRepo).save(any(Debt.class));
    }

    @Test
    void createDebtFromInvoice_ShouldThrow_WhenInvoiceNotFound() {
        when(invoiceRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.createDebtFromInvoice(1L, LocalDate.now()));
    }

    @Test
    void payInvoice_ShouldReturnTransactionImmediately_WhenBankTransfer() throws Exception {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .serviceTicket(serviceTicket)
                .finalAmount(new BigDecimal("500000"))
                .build();

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));

        PayInvoiceRequestDto request = PayInvoiceRequestDto.builder()
                .type(PaymentTransactionType.PAYMENT.getValue())
                .method(TransactionMethod.BANK_TRANSFER.getValue())
                .price(500000L)
                .build();

        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(10L)
                .method(TransactionMethod.BANK_TRANSFER.getValue())
                .type(PaymentTransactionType.PAYMENT.getValue())
                .amount(500000L)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        TransactionResponseDto result = service.payInvoice(1L, request);

        assertSame(transaction, result);
        verify(customerService, never()).updateTotalSpending(anyLong(), any());
        verify(invoiceRepo, never()).save(any(Invoice.class));
    }

    @Test
    void payInvoice_ShouldHandleDepositTransaction() throws Exception {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .serviceTicket(serviceTicket)
                .finalAmount(new BigDecimal("500000"))
                .depositReceived(new BigDecimal("100000"))
                .build();

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));

        PayInvoiceRequestDto request = PayInvoiceRequestDto.builder()
                .type(PaymentTransactionType.DEPOSIT.getValue())
                .method(TransactionMethod.CASH.getValue())
                .price(50000L)
                .build();

        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(10L)
                .method(TransactionMethod.CASH.getValue())
                .type(PaymentTransactionType.DEPOSIT.getValue())
                .amount(50000L)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        TransactionResponseDto result = service.payInvoice(1L, request);

        assertSame(transaction, result);
        assertEquals(new BigDecimal("150000"), invoice.getDepositReceived());
        assertEquals(new BigDecimal("350000"), invoice.getFinalAmount());

        verify(customerService).updateTotalSpending(eq(customer.getCustomerId()),
                eq(new BigDecimal("50000")));
        verify(invoiceRepo).save(invoice);
    }

    @Test
    void payInvoice_ShouldHandleNormalPayment_Underpaid() throws Exception {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .serviceTicket(serviceTicket)
                .finalAmount(new BigDecimal("500000"))
                .build();

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));

        PayInvoiceRequestDto request = PayInvoiceRequestDto.builder()
                .type(PaymentTransactionType.PAYMENT.getValue())
                .method(TransactionMethod.CASH.getValue())
                .price(200000L)
                .build();

        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(10L)
                .method(TransactionMethod.CASH.getValue())
                .type(PaymentTransactionType.PAYMENT.getValue())
                .amount(200000L)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        TransactionResponseDto result = service.payInvoice(1L, request);

        assertSame(transaction, result);
        assertEquals(new BigDecimal("300000"), invoice.getFinalAmount());
        assertEquals(InvoiceStatus.UNDERPAID, invoice.getStatus());

        verify(customerService).updateTotalSpending(eq(customer.getCustomerId()),
                eq(new BigDecimal("200000")));
        verify(invoiceRepo).save(invoice);
    }

    @Test
    void payInvoice_ShouldHandleNormalPayment_PaidInFullOrOverpaid() throws Exception {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .serviceTicket(serviceTicket)
                .finalAmount(new BigDecimal("300000"))
                .build();

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));

        PayInvoiceRequestDto request = PayInvoiceRequestDto.builder()
                .type(PaymentTransactionType.PAYMENT.getValue())
                .method(TransactionMethod.CASH.getValue())
                .price(500000L)
                .build();

        TransactionResponseDto transaction = TransactionResponseDto.builder()
                .id(10L)
                .method(TransactionMethod.CASH.getValue())
                .type(PaymentTransactionType.PAYMENT.getValue())
                .amount(500000L)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(transaction);

        TransactionResponseDto result = service.payInvoice(1L, request);

        assertSame(transaction, result);
        assertEquals(new BigDecimal("-200000"), invoice.getFinalAmount());
        assertEquals(InvoiceStatus.PAID_IN_FULL, invoice.getStatus());

        // amount vs finalAmount: amount.compareTo(finalAmount) >= 0 => use finalAmount
        verify(customerService).updateTotalSpending(eq(customer.getCustomerId()),
                eq(new BigDecimal("-200000").min(new BigDecimal("500000"))));
        verify(invoiceRepo).save(invoice);
    }

    @Test
    void payInvoice_ShouldThrow_WhenInvoiceNotFound() {
        when(invoiceRepo.findById(1L)).thenReturn(Optional.empty());

        PayInvoiceRequestDto request = PayInvoiceRequestDto.builder()
                .type(PaymentTransactionType.PAYMENT.getValue())
                .method(TransactionMethod.CASH.getValue())
                .price(100000L)
                .build();

        assertThrows(PaymentNotFoundException.class,
                () -> service.payInvoice(1L, request));
    }
}


