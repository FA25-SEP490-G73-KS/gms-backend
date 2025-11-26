package fpt.edu.vn.gms.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CustomerDebtSummaryDto {

  private Long customerId;
  private String customerFullName;
  private String customerPhone;
  private int totalAmount;
  private int totalPaidAmount;
  private int totalRemaining;
  private String status;

  public CustomerDebtSummaryDto(Long customerId, String customerFullName, String customerPhone, int totalAmount, int totalPaidAmount, int totalRemaining, String status) {
    this.customerId = customerId;
    this.customerFullName = customerFullName;
    this.customerPhone = customerPhone;
    this.totalAmount = totalAmount;
    this.totalPaidAmount = totalPaidAmount;
    this.totalRemaining = totalRemaining;
    this.status = status;
  }
}