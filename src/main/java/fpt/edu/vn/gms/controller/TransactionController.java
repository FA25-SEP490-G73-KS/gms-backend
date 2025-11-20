package fpt.edu.vn.gms.controller;

import static fpt.edu.vn.gms.utils.AppRoutes.TRANSACTIONS_PREFIX;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fpt.edu.vn.gms.common.annotations.Public;
import fpt.edu.vn.gms.dto.request.CreatePaymentLinkRequestDto;
import fpt.edu.vn.gms.dto.request.TransactionCallbackDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

@Tag(name = "transactions", description = "Giao dịch")
@RestController
@RequestMapping(path = TRANSACTIONS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionController {
  TransactionService transactionService;

  @Public
  @PostMapping("/create")
  public ApiResponse<CreatePaymentLinkResponse> createPaymentLink(
      @RequestBody @Valid CreatePaymentLinkRequestDto request) throws Exception {
    return ApiResponse.success("Tạo link thanh toán thành công", transactionService.createPaymentLink(request));
  }

  @Public
  @PostMapping("/callback")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void handleTransactionCallback(
      @RequestBody @Valid TransactionCallbackDto callbackDto) throws Exception {
    transactionService.handleCallback(callbackDto);
  }
}
