package fpt.edu.vn.gms.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDebtSummaryDto {

  private Long customerId;
  private String customerFullName;
  private String customerPhone;
  private BigDecimal totalAmount;
  private BigDecimal totalPaidAmount;
  private BigDecimal totalRemaining;
  private LocalDate dueDate;
  private String status;
}