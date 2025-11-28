package fpt.edu.vn.gms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PayInvoiceRequestDto {

  @NotNull
  private Long price;

  @NotBlank
  private String type;

  @NotBlank
  private String method;
}