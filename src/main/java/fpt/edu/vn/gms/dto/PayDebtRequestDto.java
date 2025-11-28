package fpt.edu.vn.gms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class PayDebtRequestDto {

  @NotNull
  private Long price;

  @Schema(implementation = TransactionMethod.class)
  @NotBlank
  private String method;
}