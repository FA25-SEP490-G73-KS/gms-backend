package fpt.edu.vn.gms.dto;

import java.math.BigDecimal;

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
  private String status;

}