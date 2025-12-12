package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DiscountPolicyResponseDto {
  private Long discountPolicyId;
  private String loyaltyLevel;
  private BigDecimal discountRate;
  private BigDecimal requiredSpending;
  private String description;
}
