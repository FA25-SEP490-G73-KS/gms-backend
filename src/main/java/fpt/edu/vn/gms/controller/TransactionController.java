package fpt.edu.vn.gms.controller;

import static fpt.edu.vn.gms.utils.AppRoutes.TRANSACTIONS_PREFIX;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fpt.edu.vn.gms.common.annotations.Public;
import fpt.edu.vn.gms.dto.request.TransactionCallbackDto;
import fpt.edu.vn.gms.dto.request.TransactionManualCallbackRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.TransactionResponseDto;
import fpt.edu.vn.gms.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Tag(name = "transactions", description = "Giao dịch")
@RestController
@RequestMapping(path = TRANSACTIONS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionController {

  TransactionService transactionService;

  @Public
  @PostMapping("/callback")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void handleTransactionCallback(
      @RequestBody @Valid TransactionCallbackDto callbackDto) {
    transactionService.handleCallback(callbackDto);
  }

  @PostMapping("/manual-callback")
  public ResponseEntity<ApiResponse<TransactionResponseDto>> manualCallback(
      @RequestBody @Valid TransactionManualCallbackRequestDto request) {
    TransactionResponseDto dto = transactionService.manualCallback(request);
    return ResponseEntity.ok(ApiResponse.success("Cập nhật giao dịch thành công", dto));
  }

  @GetMapping("/invoice/{invoiceId}")
  @Operation(summary = "Lấy danh sách giao dịch theo invoice ID", description = "Lấy tất cả các giao dịch liên quan đến một invoice cụ thể")
  public ResponseEntity<ApiResponse<List<TransactionResponseDto>>> getTransactionsByInvoiceId(
      @PathVariable Long invoiceId) {
    List<TransactionResponseDto> transactions = transactionService.getTransactionsByInvoiceId(invoiceId);
    return ResponseEntity.ok(ApiResponse.success("Lấy danh sách giao dịch thành công", transactions));
  }
}
