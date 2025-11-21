package fpt.edu.vn.gms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fpt.edu.vn.gms.dto.request.CreatePaymentLinkRequestDto;
import fpt.edu.vn.gms.dto.request.TransactionCallbackDto;
import fpt.edu.vn.gms.entity.Transaction;
import fpt.edu.vn.gms.entity.Transaction.Method;
import fpt.edu.vn.gms.exception.TransactionNotFoundException;
import fpt.edu.vn.gms.repository.TransactionRepository;
import fpt.edu.vn.gms.service.TransactionService;
import lombok.RequiredArgsConstructor;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
  @Value("${app.frontend-url}")
  private String returnUrl;

  private final PayOS payOS;
  private final TransactionRepository transactionRepository;

  @Override
  public CreatePaymentLinkResponse createPaymentLink(CreatePaymentLinkRequestDto request) throws Exception {
    String customerFullName = request.getCustomerFullName();
    String customerPhone = request.getCustomerPhone();
    String customerAddress = request.getCustomerAddress();

    String description = request.getDescription();
    Long price = request.getPrice();
    long orderCode = System.currentTimeMillis() / 1000;

    CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
        .orderCode(orderCode)
        .description(description)
        .amount(price)
        .buyerName(customerFullName)
        .buyerAddress(customerAddress)
        .buyerPhone(customerPhone)
        .returnUrl(returnUrl)
        .cancelUrl(returnUrl)
        .build();

    var payOSResponse = payOS.paymentRequests().create(paymentData);
    transactionRepository.save(
        Transaction.builder()
            .paymentLinkId(payOSResponse.getPaymentLinkId())
            .customerFullName(customerFullName)
            .customerAddress(customerAddress)
            .customerPhone(customerPhone)
            .amount(price)
            .method(Method.BANK_TRANSFER).type(request.getType()).isActive(false)
            .build());

    return payOSResponse;
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
      return;
    }

    if (List.of(PaymentLinkStatus.CANCELLED, PaymentLinkStatus.EXPIRED, PaymentLinkStatus.FAILED)
        .contains(paymentInfo.getStatus())) {
      transactionRepository.delete(transaction);
      return;
    }
  }
}
