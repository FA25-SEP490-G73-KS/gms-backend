package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.CreatePaymentLinkRequestDto;
import fpt.edu.vn.gms.dto.request.TransactionCallbackDto;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

public interface TransactionService {
  CreatePaymentLinkResponse createPaymentLink(CreatePaymentLinkRequestDto request) throws Exception;

  void handleCallback(TransactionCallbackDto callbackDto);
}
