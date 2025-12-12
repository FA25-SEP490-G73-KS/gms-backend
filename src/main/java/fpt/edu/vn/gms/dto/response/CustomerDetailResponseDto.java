package fpt.edu.vn.gms.dto.response;


import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.enums.CustomerType;
import fpt.edu.vn.gms.dto.VehicleInfoDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomerDetailResponseDto {

    private Long customerId;
    private String fullName;
    private String zaloId;
    private String phone;
    private String address;
    private CustomerType customerType;
    private CustomerLoyaltyLevel loyaltyLevel;
    private List<VehicleResponseDto> vehicles;
}

