package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.dto.VehicleInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailDto {

    // Lay chi tiet khach hang (manager)
    private Long customerId;
    private String fullName;
    private String phone;
    private String address;
    private CustomerLoyaltyLevel loyaltyLevel;
    private BigDecimal totalSpending;
    private Long vehicleCount;
    private Long serviceCount;
    private Boolean isActive;

    private List<CustomerServiceHistoryDto> history;
    private List<VehicleInfoDto> vehicles;
}
