package fpt.edu.vn.gms.dto;

import java.math.BigDecimal;

import com.google.firebase.database.annotations.NotNull;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateDebtDto {

  @NotBlank
  private Long customerId;

  @NotBlank
  private Long serviceTicketId;

  @NotNull
  @Min(value = 0)
  private BigDecimal amount;
}