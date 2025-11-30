package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class DiscountPolicyResponseDto {
  private Long discountPolicyId;
  private String loyaltyLevel;
  private BigDecimal discountRate;
  private BigDecimal requiredSpending;
  private String description;
}
