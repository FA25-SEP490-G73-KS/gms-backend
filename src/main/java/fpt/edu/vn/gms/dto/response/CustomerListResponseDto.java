package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.dto.VehicleInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerListResponseDto {

    // Response cho màn hình danh sách khách hàng của manager
    private Long customerId;
    private String fullName;
    private String phone;
    private CustomerLoyaltyLevel loyaltyLevel;
    private BigDecimal totalSpending;
    private Long serviceCount;
    private Long vehicleCount;
    private Boolean isActive;
}
