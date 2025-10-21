package fpt.edu.vn.gms.dto;

import fpt.edu.vn.gms.common.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.CustomerType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private Long customerId;
    private String fullName;
    private String phone;
    private String zaloId;
    private String address;
    private CustomerType customerType;
    private CustomerLoyaltyLevel loyaltyLevel;
}
