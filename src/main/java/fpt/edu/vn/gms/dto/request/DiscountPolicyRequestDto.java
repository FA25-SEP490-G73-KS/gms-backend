package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DiscountPolicyRequestDto {
  @NotNull(message = "Cấp độ khách hàng không được để trống")
  private CustomerLoyaltyLevel loyaltyLevel;

  @NotNull(message = "Tỉ lệ giảm giá không được để trống")
  @DecimalMin(value = "0.0", inclusive = false, message = "Tỉ lệ giảm giá phải lớn hơn 0")
  private BigDecimal discountRate;

  @NotNull(message = "Tổng chi tiêu yêu cầu không được để trống")
  @DecimalMin(value = "0.0", message = "Tổng chi tiêu yêu cầu phải không âm")
  private BigDecimal requiredSpending;

  private String description;
}
