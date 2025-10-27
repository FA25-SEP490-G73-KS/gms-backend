package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.CustomerType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDto {

    private String fullName;
    private String phone;
    private String address;
    private CustomerType customerType;
    private CustomerLoyaltyLevel loyaltyLevel;
}