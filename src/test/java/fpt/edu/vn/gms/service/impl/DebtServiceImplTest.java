package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.dto.CreateDebtDto;
import fpt.edu.vn.gms.dto.CustomerDebtSummaryDto;
import fpt.edu.vn.gms.dto.PayDebtRequestDto;
import fpt.edu.vn.gms.dto.TransactionMethod;
import fpt.edu.vn.gms.dto.TransactionResponseDto;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.response.DebtDetailResponseDto;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.Debt;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.exception.CustomerNotFoundException;
import fpt.edu.vn.gms.exception.DebtNotFoundException;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.exception.ServiceTicketNotFoundException;
import fpt.edu.vn.gms.mapper.DebtMapper;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.repository.DebtRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.CustomerService;
import fpt.edu.vn.gms.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DebtServiceImplTest extends BaseServiceTest {

  @Mock
  private DebtRepository debtRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private CustomerService customerService;
  @Mock
  private ServiceTicketRepository serviceTicketRepository;
  @Mock
  private DebtMapper debtMapper;
  @Mock
  private TransactionService transactionService;

  @InjectMocks
  private DebtServiceImpl debtServiceImpl;

  @Test
  void getAllDebtsSummary_WhenCalled_ShouldReturnPagedSummary() {
    Page<CustomerDebtSummaryDto> page = new PageImpl<>(
        List.of(CustomerDebtSummaryDto.builder().customerId(1L).build()));
    when(debtRepository.findTotalDebtGroupedByCustomer(any(Pageable.class))).thenReturn(page);

    Page<CustomerDebtSummaryDto> result = debtServiceImpl.getAllDebtsSummary(0, 10);

    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getCustomerId());
  }

  @Test
  void getDebtsByCustomer_WhenCustomerExists_ShouldReturnPagedDebts() {
    Customer customer = Customer.builder().customerId(1L).build();
    Debt debt = Debt.builder().id(1L).build();
    DebtDetailResponseDto dto = DebtDetailResponseDto.builder().id(1L).build();
    Page<Debt> page = new PageImpl<>(List.of(debt));

    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(debtRepository.findByCustomerAndFilter(eq(1L), any(), any(), any(Pageable.class))).thenReturn(page);
    when(debtMapper.toDto(debt)).thenReturn(dto);

    Page<DebtDetailResponseDto> result = debtServiceImpl.getDebtsByCustomer(1L, DebtStatus.OUTSTANDING, "abc", 0, 10,
        "createdAt,desc");

    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getId());
  }

  @Test
  void getDebtsByCustomer_WhenCustomerNotFound_ShouldThrowResourceNotFoundException() {
    when(customerRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class,
        () -> debtServiceImpl.getDebtsByCustomer(99L, DebtStatus.OUTSTANDING, null, 0, 10, null));
  }

  @Test
  void createDebt_WhenCustomerOrTicketNotFound_ShouldThrowException() {
    CreateDebtDto dto = CreateDebtDto.builder().customerId(1L).serviceTicketId(2L).amount(BigDecimal.TEN).build();
    when(customerRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(CustomerNotFoundException.class, () -> debtServiceImpl.createDebt(dto));

    Customer customer = Customer.builder().customerId(1L).build();
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(serviceTicketRepository.findById(2L)).thenReturn(Optional.empty());
    assertThrows(ServiceTicketNotFoundException.class, () -> debtServiceImpl.createDebt(dto));
  }

  @Test
  void createDebt_WhenValid_ShouldSaveAndReturnDto() {
    CreateDebtDto dto = CreateDebtDto.builder().customerId(1L).serviceTicketId(2L).amount(BigDecimal.TEN).build();
    Customer customer = Customer.builder().customerId(1L).build();
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(2L).build();
    Debt debt = Debt.builder().id(3L).customer(customer).serviceTicket(ticket).amount(BigDecimal.TEN).build();
    DebtDetailResponseDto responseDto = DebtDetailResponseDto.builder().id(3L).build();

    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(serviceTicketRepository.findById(2L)).thenReturn(Optional.of(ticket));
    when(debtRepository.save(any(Debt.class))).thenReturn(debt);
    when(debtMapper.toDto(debt)).thenReturn(responseDto);

    DebtDetailResponseDto result = debtServiceImpl.createDebt(dto);
    assertEquals(3L, result.getId());
  }

  @Test
  void payDebt_WhenDebtNotFound_ShouldThrowDebtNotFoundException() {
    when(debtRepository.findById(99L)).thenReturn(Optional.empty());
    PayDebtRequestDto req = PayDebtRequestDto.builder().method(TransactionMethod.CASH.getValue())
        .price(10L)
        .build();
    assertThrows(DebtNotFoundException.class, () -> debtServiceImpl.payDebt(99L, req));
  }

  @Test
  void payDebt_WhenBankTransfer_ShouldReturnTransactionWithoutUpdate() throws Exception {
    Debt debt = Debt.builder()
        .id(1L)
        .customer(Customer.builder().customerId(2L).fullName("A").phone("0123").build())
        .paidAmount(BigDecimal.ZERO)
        .amount(BigDecimal.valueOf(100))
        .build();
    PayDebtRequestDto req = PayDebtRequestDto.builder().method(TransactionMethod.BANK_TRANSFER.getValue())
        .price(50L).build();
    TransactionResponseDto transaction = TransactionResponseDto.builder()
        .method(TransactionMethod.BANK_TRANSFER.getValue())
        .amount(50L)
        .build();

    when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
    when(transactionService.createTransaction(any(CreateTransactionRequestDto.class))).thenReturn(transaction);

    TransactionResponseDto result = debtServiceImpl.payDebt(1L, req);

    assertEquals(TransactionMethod.BANK_TRANSFER.getValue(), result.getMethod());
    verify(debtRepository, never()).save(any());
    verify(customerService, never()).updateTotalSpending(anyLong(), any());
  }

  @Test
  void payDebt_WhenCashPaymentAndPaidInFull_ShouldUpdateDebtAndCustomerSpending() throws Exception {
    Debt debt = Debt.builder()
        .id(1L)
        .customer(Customer.builder().customerId(2L).fullName("A").phone("0123").build())
        .paidAmount(BigDecimal.valueOf(90))
        .amount(BigDecimal.valueOf(100))
        .status(DebtStatus.OUTSTANDING)
        .build();
    PayDebtRequestDto req = PayDebtRequestDto.builder().method(TransactionMethod.CASH.getValue())
        .price(10L).build();
    TransactionResponseDto transaction = TransactionResponseDto.builder()
        .method(TransactionMethod.CASH.getValue())
        .amount(10L)
        .build();

    when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
    when(transactionService.createTransaction(any(CreateTransactionRequestDto.class))).thenReturn(transaction);

    TransactionResponseDto result = debtServiceImpl.payDebt(1L, req);

    assertEquals(TransactionMethod.CASH.getValue(), result.getMethod());
    assertEquals(DebtStatus.PAID_IN_FULL, debt.getStatus());
    assertEquals(BigDecimal.valueOf(100), debt.getPaidAmount());
    verify(debtRepository).save(debt);
    verify(customerService).updateTotalSpending(eq(2L), eq(BigDecimal.valueOf(10)));
  }

  @Test
  void payDebt_WhenCashPaymentAndOverPaid_ShouldUpdateDebtAndCustomerSpendingWithExcess() throws Exception {
    Debt debt = Debt.builder()
        .id(1L)
        .customer(Customer.builder().customerId(2L).fullName("A").phone("0123").build())
        .paidAmount(BigDecimal.valueOf(90))
        .amount(BigDecimal.valueOf(100))
        .status(DebtStatus.OUTSTANDING)
        .build();
    PayDebtRequestDto req = PayDebtRequestDto.builder().method(TransactionMethod.CASH.getValue())
        .price(20L).build();
    TransactionResponseDto transaction = TransactionResponseDto.builder()
        .method(TransactionMethod.CASH.getValue())
        .amount(20L)
        .build();

    when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
    when(transactionService.createTransaction(any(CreateTransactionRequestDto.class))).thenReturn(transaction);

    TransactionResponseDto result = debtServiceImpl.payDebt(1L, req);

    assertEquals(TransactionMethod.CASH.getValue(), result.getMethod());
    assertEquals(DebtStatus.PAID_IN_FULL, debt.getStatus());
    assertEquals(BigDecimal.valueOf(100), debt.getPaidAmount());
    verify(debtRepository).save(debt);
    verify(customerService).updateTotalSpending(eq(2L), eq(BigDecimal.valueOf(10)));
  }

  @Test
  void payDebt_WhenCashPaymentAndStillOutstanding_ShouldUpdateDebtAndCustomerSpending() throws Exception {
    Debt debt = Debt.builder()
        .id(1L)
        .customer(Customer.builder().customerId(2L).fullName("A").phone("0123").build())
        .paidAmount(BigDecimal.valueOf(20))
        .amount(BigDecimal.valueOf(100))
        .status(DebtStatus.OUTSTANDING)
        .build();
    PayDebtRequestDto req = PayDebtRequestDto.builder().method(TransactionMethod.CASH.getValue())
        .price(30L).build();
    TransactionResponseDto transaction = TransactionResponseDto.builder()
        .method(TransactionMethod.CASH.getValue())
        .amount(30L)
        .build();

    when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
    when(transactionService.createTransaction(any(CreateTransactionRequestDto.class))).thenReturn(transaction);

    TransactionResponseDto result = debtServiceImpl.payDebt(1L, req);

    assertEquals(TransactionMethod.CASH.getValue(), result.getMethod());
    assertEquals(DebtStatus.OUTSTANDING, debt.getStatus());
    assertEquals(BigDecimal.valueOf(50), debt.getPaidAmount());
    verify(debtRepository).save(debt);
    verify(customerService).updateTotalSpending(eq(2L), eq(BigDecimal.valueOf(30)));
  }
}
