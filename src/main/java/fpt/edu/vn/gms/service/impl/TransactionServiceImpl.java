package fpt.edu.vn.gms.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.common.enums.InvoiceStatus;
import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import fpt.edu.vn.gms.common.enums.TransactionMethod;
import fpt.edu.vn.gms.dto.response.TransactionResponseDto;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.request.TransactionCallbackDto;
import fpt.edu.vn.gms.dto.request.TransactionManualCallbackRequestDto;
import fpt.edu.vn.gms.entity.Debt;
import fpt.edu.vn.gms.entity.Invoice;
import fpt.edu.vn.gms.entity.Transaction;
import fpt.edu.vn.gms.exception.TransactionNotFoundException;
import fpt.edu.vn.gms.mapper.TransactionMapper;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.repository.DebtRepository;
import fpt.edu.vn.gms.repository.InvoiceRepository;
import fpt.edu.vn.gms.repository.TransactionRepository;
import fpt.edu.vn.gms.service.CustomerService;
import fpt.edu.vn.gms.service.TransactionService;
import lombok.RequiredArgsConstructor;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  @Value("${app.frontend-url}")
  private String returnUrl;

  private final PayOS payOS;
  private final TransactionRepository transactionRepository;
  private final TransactionMapper transactionMapper;
  private final DebtRepository debtRepository;
  private final InvoiceRepository invoiceRepository;
  private final CustomerRepository customerRepository;
  private final CustomerService customerService;

  /**
   * Dùng chung core logic xử lý thanh toán thành công/failed dựa trên
   * paymentLinkId.
   * Trả về Transaction đã cập nhật để tái sử dụng.
   */
  private Transaction processPaymentByPaymentLinkId(String paymentLinkId) {
    var paymentInfo = payOS.paymentRequests().get(paymentLinkId);
    Transaction transaction = transactionRepository.findByPaymentLinkId(paymentLinkId)
        .orElseThrow(TransactionNotFoundException::new);

    if (paymentInfo.getStatus() == PaymentLinkStatus.PAID) {
      transaction.setIsActive(true);
      transactionRepository.save(transaction);

      Invoice invoice = transaction.getInvoice();
      Debt debt = transaction.getDebt();
      BigDecimal amount = new BigDecimal(transaction.getAmount());

      if (invoice != null) {
        Long customerId = invoice.getServiceTicket().getCustomer().getCustomerId();

        if (transaction.getType() == PaymentTransactionType.DEPOSIT) {
          invoice.setDepositReceived(invoice.getDepositReceived().add(amount));

          customerService.updateTotalSpending(customerId, amount);
        } else {
          BigDecimal finalAmount = invoice.getFinalAmount();
          InvoiceStatus status = amount.equals(finalAmount) ? InvoiceStatus.PAID_IN_FULL
              : InvoiceStatus.UNDERPAID;
          customerService.updateTotalSpending(customerId, amount.compareTo(finalAmount) < 0 ? amount : finalAmount);
          invoice.setStatus(status);
        }

        invoice.setFinalAmount(invoice.getFinalAmount().subtract(amount));
        invoiceRepository.save(invoice);
      } else if (debt != null) {
        Long customerId = debt.getCustomer().getCustomerId();
        BigDecimal paidAmountAfter = debt.getPaidAmount().add(amount);
        boolean isPaidAmountAfterGreaterThanOrEqualToDebtAmount = paidAmountAfter
            .compareTo(debt.getAmount()) >= 0;
        DebtStatus status = isPaidAmountAfterGreaterThanOrEqualToDebtAmount ? DebtStatus.PAID_IN_FULL
            : DebtStatus.OUTSTANDING;

        debt.setPaidAmount(
            isPaidAmountAfterGreaterThanOrEqualToDebtAmount ? debt.getAmount() : paidAmountAfter);
        debt.setStatus(status);

        customerService.updateTotalSpending(customerId, paidAmountAfter
            .compareTo(debt.getAmount()) > 0
                ? paidAmountAfter.subtract(
                    debt.getAmount())
                : amount);

        debtRepository.save(debt);
      }

      return transaction;
    }

    if (List.of(PaymentLinkStatus.CANCELLED, PaymentLinkStatus.EXPIRED, PaymentLinkStatus.FAILED)
        .contains(paymentInfo.getStatus())) {
      transactionRepository.delete(transaction);
    }

    return transaction;
  }

  @Override
  public TransactionResponseDto createTransaction(CreateTransactionRequestDto request)
      throws Exception {
    Invoice invoice = request.getInvoice();
    Debt debt = request.getDebt();

    String customerFullName = request.getCustomerFullName();
    String customerPhone = request.getCustomerPhone();
    Long price = request.getPrice();
    PaymentTransactionType type = request.getType();

    Transaction transaction = Transaction.builder().debt(debt)
        .invoice(invoice)
        .customerFullName(customerFullName)
        .customerPhone(customerPhone)
        .amount(price)
        .method(request.getMethod())
        .type(type)
        .isActive(request.getMethod() == TransactionMethod.CASH)
        .build();

    if (request.getMethod() == TransactionMethod.BANK_TRANSFER) {
      // Phân biệt: nếu có invoice thì mô tả theo hóa đơn, nếu không thì theo công nợ
      String description;
      if (invoice != null) {
        description = getDescriptionOfTransaction(type, invoice);
      } else if (debt != null) {
        String typeName = switch (type) {
          case PaymentTransactionType.DEPOSIT -> "Dat coc";
          case PaymentTransactionType.PAYMENT -> "Thanh toan cong no";
        };
        String ticketCode = (debt.getServiceTicket() != null)
            ? debt.getServiceTicket().getServiceTicketCode()
            : "";
        description = "%s-%s".formatted(typeName, ticketCode);
      } else {
        description = "Thanh toan";
      }

      // PayOS giới hạn mô tả tối đa 25 ký tự
      if (description.length() > 25) {
        description = description.substring(0, 25);
      }

      long orderCode = System.currentTimeMillis() / 1000;

      CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
          .orderCode(orderCode)
          .description(description)
          .amount(price)
          .buyerName(customerFullName)
          .buyerPhone(customerPhone)
          .returnUrl(returnUrl)
          .cancelUrl(returnUrl)
          .build();

      var payOSResponse = payOS.paymentRequests().create(paymentData);
      transaction.setPaymentLinkId(payOSResponse.getPaymentLinkId());
      TransactionResponseDto responseDto = transactionMapper.toResponseDto(transactionRepository.save(transaction));
      responseDto.setPaymentUrl(payOSResponse.getCheckoutUrl());
      return responseDto;
    }

    return transactionMapper.toResponseDto(transactionRepository.save(transaction));
  }

  @Override
  public void handleCallback(TransactionCallbackDto callbackDto) {
    String paymentLinkId = callbackDto.getPaymentLinkId();
    processPaymentByPaymentLinkId(paymentLinkId);
  }

  @Override
  public TransactionResponseDto manualCallback(TransactionManualCallbackRequestDto request) {
    Transaction tx = processPaymentByPaymentLinkId(request.getPaymentLinkId());
    return transactionMapper.toResponseDto(tx);
  }

  @Override
  public List<TransactionResponseDto> getTransactionsByInvoiceId(Long invoiceId) {
    List<Transaction> transactions = transactionRepository.findByInvoiceId(invoiceId).stream()
        .filter(t -> Boolean.TRUE.equals(t.getIsActive()))
        .toList();
    return transactions.stream()
        .map(transactionMapper::toResponseDto)
        .toList();
  }

  private String getDescriptionOfTransaction(PaymentTransactionType type, Invoice invoice) {
    String typeName = switch (type) {
      case PaymentTransactionType.DEPOSIT -> "Dat coc";
      case PaymentTransactionType.PAYMENT -> "Thanh toan";
    };

    return "%s-%s".formatted(typeName, invoice.getServiceTicket().getServiceTicketCode());
  }
}
