package fpt.edu.vn.gms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TransactionCallbackDto {
  @NotBlank
  private String paymentLinkId;
}
