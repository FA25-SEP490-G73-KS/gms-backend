package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.common.enums.InvoiceStatus;
import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import fpt.edu.vn.gms.dto.PayInvoiceRequestDto;
import fpt.edu.vn.gms.dto.TransactionMethod;
import fpt.edu.vn.gms.dto.TransactionResponseDto;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.response.DebtDetailResponseDto;
import fpt.edu.vn.gms.dto.response.InvoiceDetailResDto;
import fpt.edu.vn.gms.dto.response.InvoiceListResDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.PaymentNotFoundException;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.DebtMapper;
import fpt.edu.vn.gms.mapper.InvoiceMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.CustomerService;
import fpt.edu.vn.gms.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InvoiceServiceImplTest extends BaseServiceTest {

  @Mock
  private ServiceTicketRepository serviceTicketRepo;
  @Mock
  private PriceQuotationRepository priceQuotationRepo;
  @Mock
  private InvoiceRepository invoiceRepo;
  @Mock
  private DebtRepository debtRepo;
  @Mock
  private TransactionRepository transactionRepo;
  @Mock
  private TransactionService transactionService;
  @Mock
  private CodeSequenceService codeSequenceService;
  @Mock
  private DebtMapper debtMapper;
  @Mock
  private InvoiceMapper invoiceMapper;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private CustomerService customerService;

  @InjectMocks
  private InvoiceServiceImpl invoiceServiceImpl;

  @Test
  void createInvoice_WhenValidInput_ShouldSaveInvoice() {
    ServiceTicket ticket = ServiceTicket.builder()
        .serviceTicketId(1L)
        .customer(Customer.builder()
            .customerId(2L)
            .discountPolicy(DiscountPolicy.builder().discountRate(BigDecimal.valueOf(10)).build())
            .build())
        .build();
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(3L)
        .estimateAmount(BigDecimal.valueOf(1000))
        .build();

    when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(ticket));
    when(priceQuotationRepo.findById(3L)).thenReturn(Optional.of(quotation));
    when(debtRepo.getTotalDebt(2L)).thenReturn(BigDecimal.valueOf(200));
    when(codeSequenceService.generateCode("PAY")).thenReturn("PAY-2024-000001");

    ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);

    invoiceServiceImpl.createInvoice(1L, 3L);

    verify(invoiceRepo).save(captor.capture());
    Invoice saved = captor.getValue();
    assertEquals(ticket, saved.getServiceTicket());
    assertEquals(quotation, saved.getQuotation());
    assertEquals("PAY-2024-000001", saved.getCode());
    assertEquals(BigDecimal.valueOf(1000), saved.getItemTotal());
    assertEquals(BigDecimal.valueOf(100), saved.getDiscount());
    assertEquals(BigDecimal.valueOf(200),
        saved.getFinalAmount().subtract(saved.getItemTotal().subtract(saved.getDiscount())));
  }

  @Test
  void createInvoice_WhenServiceTicketNotFound_ShouldThrowResourceNotFoundException() {
    when(serviceTicketRepo.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> invoiceServiceImpl.createInvoice(1L, 2L));
  }

  @Test
  void createInvoice_WhenQuotationNotFound_ShouldThrowResourceNotFoundException() {
    ServiceTicket ticket = ServiceTicket.builder()
        .serviceTicketId(1L)
        .customer(Customer.builder().customerId(2L).build())
        .build();
    when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(ticket));
    when(priceQuotationRepo.findById(2L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> invoiceServiceImpl.createInvoice(1L, 2L));
  }

  @Test
  void createInvoice_WhenCustomerNull_ShouldThrowResourceNotFoundException() {
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(1L).customer(null).build();
    when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(ticket));
    PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(2L).build();
    when(priceQuotationRepo.findById(2L)).thenReturn(Optional.of(quotation));
    assertThrows(ResourceNotFoundException.class, () -> invoiceServiceImpl.createInvoice(1L, 2L));
  }

  @Test
  void getInvoiceList_WhenCalled_ShouldReturnPagedInvoiceListResDto() {
    Invoice invoice = Invoice.builder().id(1L).build();
    InvoiceListResDto dto = InvoiceListResDto.builder().id(1L).build();
    Page<Invoice> page = new PageImpl<>(List.of(invoice));
    when(invoiceRepo.findAllWithRelations(any(Pageable.class))).thenReturn(page);
    when(invoiceMapper.toListDto(invoice)).thenReturn(dto);

    Page<InvoiceListResDto> result = invoiceServiceImpl.getInvoiceList(0, 10, "createdAt,desc");

    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getId());
  }

  @Test
  void getInvoiceDetail_WhenInvoiceExists_ShouldReturnInvoiceDetailResDto() {
    Invoice invoice = Invoice.builder().id(1L).build();
    InvoiceDetailResDto dto = InvoiceDetailResDto.builder().id(1L).build();
    when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
    when(invoiceMapper.toDetailDto(invoice)).thenReturn(dto);

    InvoiceDetailResDto result = invoiceServiceImpl.getInvoiceDetail(1L);

    assertEquals(1L, result.getId());
  }

  @Test
  void getInvoiceDetail_WhenInvoiceNotFound_ShouldThrowResourceNotFoundException() {
    when(invoiceRepo.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> invoiceServiceImpl.getInvoiceDetail(99L));
  }

  @Test
  void createDebtFromInvoice_WhenInvoiceNotFound_ShouldThrowResourceNotFoundException() {
    when(invoiceRepo.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> invoiceServiceImpl.createDebtFromInvoice(99L, LocalDate.now()));
  }

  @Test
  void createDebtFromInvoice_WhenServiceTicketOrCustomerNull_ShouldThrowResourceNotFoundException() {
    Invoice invoice = Invoice.builder().id(1L).serviceTicket(null).build();
    when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
    assertThrows(ResourceNotFoundException.class, () -> invoiceServiceImpl.createDebtFromInvoice(1L, LocalDate.now()));

    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(2L).customer(null).build();
    invoice = Invoice.builder().id(1L).serviceTicket(ticket).build();
    when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
    assertThrows(ResourceNotFoundException.class, () -> invoiceServiceImpl.createDebtFromInvoice(1L, LocalDate.now()));
  }

  @Test
  void createDebtFromInvoice_WhenNoRemainingAmount_ShouldReturnNull() {
    Customer customer = Customer.builder().customerId(1L).build();
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(2L).customer(customer).build();
    Invoice invoice = Invoice.builder().id(3L).serviceTicket(ticket).finalAmount(BigDecimal.valueOf(100))
        .build();
    when(invoiceRepo.findById(3L)).thenReturn(Optional.of(invoice));
    Transaction transaction = Transaction.builder().amount(100L).build();
    when(transactionRepo.findByInvoiceAndIsActiveTrue(invoice)).thenReturn(List.of(transaction));

    DebtDetailResponseDto result = invoiceServiceImpl.createDebtFromInvoice(3L, LocalDate.now());
    assertNull(result);
  }

  @Test
  void createDebtFromInvoice_WhenRemainingAmount_ShouldSaveDebtAndReturnDto() {
    Customer customer = Customer.builder().customerId(1L).build();
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(2L).customer(customer).build();
    Invoice invoice = Invoice.builder().id(3L).serviceTicket(ticket).finalAmount(BigDecimal.valueOf(200))
        .build();
    when(invoiceRepo.findById(3L)).thenReturn(Optional.of(invoice));
    Transaction transaction = Transaction.builder().amount(50L).build();
    when(transactionRepo.findByInvoiceAndIsActiveTrue(invoice)).thenReturn(List.of(transaction));
    Debt debt = Debt.builder().id(4L).customer(customer).serviceTicket(ticket).amount(BigDecimal.valueOf(150))
        .paidAmount(BigDecimal.ZERO).status(DebtStatus.OUTSTANDING).dueDate(LocalDate.now().plusDays(30)).build();
    when(debtRepo.save(any(Debt.class))).thenReturn(debt);
    DebtDetailResponseDto dto = DebtDetailResponseDto.builder().id(4L).build();
    when(debtMapper.toDto(debt)).thenReturn(dto);

    DebtDetailResponseDto result = invoiceServiceImpl.createDebtFromInvoice(3L, null);

    assertNotNull(result);
    assertEquals(4L, result.getId());
    verify(debtRepo).save(any(Debt.class));
  }

  @Test
  void payInvoice_WhenInvoiceNotFound_ShouldThrowPaymentNotFoundException() {
    when(invoiceRepo.findById(99L)).thenReturn(Optional.empty());
    PayInvoiceRequestDto req = PayInvoiceRequestDto.builder().build();
    assertThrows(PaymentNotFoundException.class, () -> invoiceServiceImpl.payInvoice(99L, req));
  }

  @Test
  void payInvoice_WhenBankTransfer_ShouldReturnTransactionWithoutUpdate() throws Exception {
    Customer customer = Customer.builder().customerId(1L).build();
    ServiceTicket ticket = ServiceTicket.builder().customer(customer).customerName("A").customerPhone("0123").build();
    Invoice invoice = Invoice.builder().id(1L).serviceTicket(ticket).build();
    PayInvoiceRequestDto req = PayInvoiceRequestDto.builder().type(PaymentTransactionType.PAYMENT.getValue())
        .method(TransactionMethod.BANK_TRANSFER.getValue()).price(100L).build();
    TransactionResponseDto transaction = TransactionResponseDto.builder()
        .method(TransactionMethod.BANK_TRANSFER.getValue()).amount(100L)
        .type(PaymentTransactionType.PAYMENT.getValue()).build();

    when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
    when(transactionService.createTransaction(any(CreateTransactionRequestDto.class))).thenReturn(transaction);

    TransactionResponseDto result = invoiceServiceImpl.payInvoice(1L, req);

    assertEquals(TransactionMethod.BANK_TRANSFER.getValue(), result.getMethod());
    verify(invoiceRepo, never()).save(any());
    verify(customerService, never()).updateTotalSpending(anyLong(), any());
  }

  @Test
  void payInvoice_WhenDeposit_ShouldUpdateInvoiceAndCustomerSpending() throws Exception {
    Customer customer = Customer.builder().customerId(1L).build();
    ServiceTicket ticket = ServiceTicket.builder().customer(customer).customerName("A").customerPhone("0123").build();
    Invoice invoice = Invoice.builder().id(1L).serviceTicket(ticket).depositReceived(BigDecimal.ZERO)
        .finalAmount(BigDecimal.valueOf(200)).build();
    PayInvoiceRequestDto req = PayInvoiceRequestDto.builder().type(PaymentTransactionType.DEPOSIT.getValue())
        .method(TransactionMethod.CASH.getValue()).price(50L).build();
    TransactionResponseDto transaction = TransactionResponseDto.builder().method(TransactionMethod.CASH.getValue())
        .amount(50L).type(PaymentTransactionType.DEPOSIT.getValue()).build();

    when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
    when(transactionService.createTransaction(any(CreateTransactionRequestDto.class))).thenReturn(transaction);

    TransactionResponseDto result = invoiceServiceImpl.payInvoice(1L, req);

    assertEquals(TransactionMethod.CASH.getValue(), result.getMethod());
    verify(invoiceRepo).save(invoice);
    verify(customerService).updateTotalSpending(eq(1L), eq(BigDecimal.valueOf(50)));
  }

  @Test
  void payInvoice_WhenFullPayment_ShouldUpdateInvoiceStatusAndCustomerSpending() throws Exception {
    Customer customer = Customer.builder().customerId(1L).build();
    ServiceTicket ticket = ServiceTicket.builder().customer(customer).customerName("A").customerPhone("0123").build();
    Invoice invoice = Invoice.builder().id(1L).serviceTicket(ticket).finalAmount(BigDecimal.valueOf(100))
        .status(InvoiceStatus.UNDERPAID).depositReceived(BigDecimal.ZERO).build();
    PayInvoiceRequestDto req = PayInvoiceRequestDto.builder().type(PaymentTransactionType.PAYMENT.getValue())
        .method(TransactionMethod.CASH.getValue()).price(100L).build();
    TransactionResponseDto transaction = TransactionResponseDto.builder().method(TransactionMethod.CASH.getValue())
        .amount(100L).type(PaymentTransactionType.PAYMENT.getValue()).build();

    when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
    when(transactionService.createTransaction(any(CreateTransactionRequestDto.class))).thenReturn(transaction);

    TransactionResponseDto result = invoiceServiceImpl.payInvoice(1L, req);

    assertEquals(TransactionMethod.CASH.getValue(), result.getMethod());
    assertEquals(InvoiceStatus.PAID_IN_FULL, invoice.getStatus());
    verify(invoiceRepo).save(invoice);
    verify(customerService).updateTotalSpending(eq(1L), eq(BigDecimal.valueOf(100)));
  }

  @Test
  void payInvoice_WhenPartialPayment_ShouldUpdateInvoiceStatusAndCustomerSpending() throws Exception {
    Customer customer = Customer.builder().customerId(1L).build();
    ServiceTicket ticket = ServiceTicket.builder().customer(customer).customerName("A").customerPhone("0123").build();
    Invoice invoice = Invoice.builder().id(1L).serviceTicket(ticket).finalAmount(BigDecimal.valueOf(200))
        .status(InvoiceStatus.UNDERPAID).depositReceived(BigDecimal.ZERO).build();
    PayInvoiceRequestDto req = PayInvoiceRequestDto.builder().type(PaymentTransactionType.PAYMENT.getValue())
        .method(TransactionMethod.CASH.getValue()).price(50L).build();
    TransactionResponseDto transaction = TransactionResponseDto.builder().method(TransactionMethod.CASH.getValue())
        .amount(50L).type(PaymentTransactionType.PAYMENT.getValue()).build();

    when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
    when(transactionService.createTransaction(any(CreateTransactionRequestDto.class))).thenReturn(transaction);

    TransactionResponseDto result = invoiceServiceImpl.payInvoice(1L, req);

    assertEquals(TransactionMethod.CASH.getValue(), result.getMethod());
    assertEquals(InvoiceStatus.UNDERPAID, invoice.getStatus());
    verify(invoiceRepo).save(invoice);
    verify(customerService).updateTotalSpending(eq(1L), eq(BigDecimal.valueOf(50)));
  }

  // --- More tests ---

  @Test
  void getInvoiceList_WhenSortParamInvalid_ShouldThrowException() {
    assertThrows(Exception.class, () -> invoiceServiceImpl.getInvoiceList(0, 10, "createdAt"));
  }

  @Test
  void createDebtFromInvoice_WhenNegativeDebt_ShouldForceZeroAndReturnNull() {
    Customer customer = Customer.builder().customerId(1L).build();
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(2L).customer(customer).build();
    Invoice invoice = Invoice.builder().id(3L).serviceTicket(ticket).finalAmount(BigDecimal.valueOf(100))
        .build();
    Transaction transaction = Transaction.builder().amount(200L).build();

    when(invoiceRepo.findById(3L)).thenReturn(Optional.of(invoice));
    when(transactionRepo.findByInvoiceAndIsActiveTrue(invoice)).thenReturn(List.of(transaction));

    DebtDetailResponseDto result = invoiceServiceImpl.createDebtFromInvoice(3L, null);

    assertNull(result);
  }

  @Test
  void payInvoice_WhenDepositAmountGreaterThanFinalAmount_ShouldSetFinalAmountZero() throws Exception {
    Customer customer = Customer.builder().customerId(1L).build();
    ServiceTicket ticket = ServiceTicket.builder().customer(customer).customerName("A").customerPhone("0123").build();
    Invoice invoice = Invoice.builder().id(1L).serviceTicket(ticket).depositReceived(BigDecimal.valueOf(100))
        .finalAmount(BigDecimal.valueOf(50)).build();
    PayInvoiceRequestDto req = PayInvoiceRequestDto.builder().type(PaymentTransactionType.DEPOSIT.getValue())
        .method(TransactionMethod.CASH.getValue()).price(100L).build();
    TransactionResponseDto transaction = TransactionResponseDto.builder().method(TransactionMethod.CASH.getValue())
        .amount(100L).type(PaymentTransactionType.DEPOSIT.getValue()).build();

    when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
    when(transactionService.createTransaction(any(CreateTransactionRequestDto.class))).thenReturn(transaction);

    TransactionResponseDto result = invoiceServiceImpl.payInvoice(1L, req);

    assertEquals(TransactionMethod.CASH.getValue(), result.getMethod());
    verify(invoiceRepo).save(invoice);
    verify(customerService).updateTotalSpending(eq(1L), eq(BigDecimal.valueOf(100)));
    assertTrue(invoice.getFinalAmount().compareTo(BigDecimal.ZERO) <= 0);
  }
}
