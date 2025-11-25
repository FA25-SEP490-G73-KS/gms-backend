package fpt.edu.vn.gms.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.common.enums.InvoiceStatus;
import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import fpt.edu.vn.gms.dto.TransactionMethod;
import fpt.edu.vn.gms.dto.TransactionResponseDto;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.request.TransactionCallbackDto;
import fpt.edu.vn.gms.entity.Debt;
import fpt.edu.vn.gms.entity.Invoice;
import fpt.edu.vn.gms.entity.Transaction;
import fpt.edu.vn.gms.exception.TransactionNotFoundException;
import fpt.edu.vn.gms.mapper.TransactionMapper;
import fpt.edu.vn.gms.repository.DebtRepository;
import fpt.edu.vn.gms.repository.InvoiceRepository;
import fpt.edu.vn.gms.repository.TransactionRepository;
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

  @Override
  public TransactionResponseDto createTransaction(CreateTransactionRequestDto request)
      throws Exception {
    Invoice invoice = request.getInvoice();

    String customerFullName = request.getCustomerFullName();
    String customerPhone = request.getCustomerPhone();
    Long price = request.getPrice();
    PaymentTransactionType type = request.getType();

    Transaction transaction = Transaction.builder()
        .invoice(invoice)
        .customerFullName(customerFullName)
        .customerPhone(customerPhone)
        .amount(price)
        .method(request.getMethod())
        .type(type)
        .isActive(request.getMethod() == TransactionMethod.CASH)
        .build();

    if (request.getMethod() == TransactionMethod.BANK_TRANSFER) {
      String description = getDescriptionOfTransaction(type, invoice);
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
        if (transaction.getType() == PaymentTransactionType.DEPOSIT) {
          invoice.setDepositReceived(invoice.getDepositReceived().add(amount));
          invoice.setFinalAmount(invoice.getFinalAmount().subtract(invoice.getDepositReceived()));
          invoiceRepository.save(invoice);
        } else {
          InvoiceStatus status = amount.equals(invoice.getFinalAmount()) ? InvoiceStatus.PAID_IN_FULL
              : InvoiceStatus.UNDERPAID;
          invoice.setStatus(status);
          invoiceRepository.save(invoice);
        }
      } else if (debt != null) {
        DebtStatus status = debt.getAmount().subtract(debt.getPaidAmount()).equals(amount) ? DebtStatus.DA_TAT_TOAN
            : DebtStatus.CON_NO;
        debt.setPaidAmount(debt.getPaidAmount().add(amount));
        debt.setStatus(status);
        debtRepository.save(debt);
      }

      return;
    }

    if (List.of(PaymentLinkStatus.CANCELLED, PaymentLinkStatus.EXPIRED, PaymentLinkStatus.FAILED)
        .contains(paymentInfo.getStatus())) {
      transactionRepository.delete(transaction);
      return;
    }
  }

  private String getDescriptionOfTransaction(PaymentTransactionType type, Invoice invoice) {
    String typeName = switch (type) {
      case PaymentTransactionType.DEPOSIT -> "Dat coc";
      case PaymentTransactionType.PAYMENT -> "Thanh toan";
    };

    return "%s-%s".formatted(typeName, invoice.getServiceTicket().getServiceTicketCode());
  }
}
