package fpt.edu.vn.gms.service;

import java.util.List;

import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.request.TransactionCallbackDto;
import fpt.edu.vn.gms.dto.request.TransactionManualCallbackRequestDto;
import fpt.edu.vn.gms.dto.response.TransactionResponseDto;
import org.springframework.stereotype.Service;

public interface TransactionService {

  TransactionResponseDto createTransaction(CreateTransactionRequestDto request) throws Exception;

  void handleCallback(TransactionCallbackDto callbackDto);

  /**
   * Cho phép FE chủ động gọi cập nhật trạng thái giao dịch (khi onSuccess PayOS),
   * sử dụng cùng logic với callback.
   */
  TransactionResponseDto manualCallback(TransactionManualCallbackRequestDto request);

  List<TransactionResponseDto> getTransactionsByInvoiceId(Long invoiceId);
}
