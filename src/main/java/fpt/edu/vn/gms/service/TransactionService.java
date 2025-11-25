package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.TransactionResponseDto;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.request.TransactionCallbackDto;

public interface TransactionService {
  TransactionResponseDto createTransaction(CreateTransactionRequestDto request) throws Exception;

  void handleCallback(TransactionCallbackDto callbackDto);
}
