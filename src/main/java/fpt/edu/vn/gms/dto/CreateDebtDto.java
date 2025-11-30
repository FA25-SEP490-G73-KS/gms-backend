package fpt.edu.vn.gms.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateDebtDto {

  @NotNull
  private Long customerId;

  @NotNull
  private Long serviceTicketId;

  @NotNull
  @Min(value = 0)
  private BigDecimal amount;
}