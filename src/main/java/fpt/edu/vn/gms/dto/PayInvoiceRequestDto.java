package fpt.edu.vn.gms.dto;

import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PayInvoiceRequestDto {

  @NotNull
  private Long price;

  @NotBlank
  private PaymentTransactionType type;

  @NotBlank
  private TransactionMethod method;
}