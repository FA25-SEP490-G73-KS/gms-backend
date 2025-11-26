package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import fpt.edu.vn.gms.dto.TransactionMethod;
import fpt.edu.vn.gms.entity.Debt;
import fpt.edu.vn.gms.entity.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class CreateTransactionRequestDto {

  private Invoice invoice;
  private Debt debt;
  private String customerFullName;
  private String customerPhone;
  private PaymentTransactionType type;
  private TransactionMethod method;
  private Long price;
}
