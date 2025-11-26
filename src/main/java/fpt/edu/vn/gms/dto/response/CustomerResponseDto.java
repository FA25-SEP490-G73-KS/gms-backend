package fpt.edu.vn.gms.dto.response;

import lombok.*;

import java.math.BigDecimal;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.enums.CustomerType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDto {

    private Long customerId;
    private String fullName;
    private String phone;
    private String address;
    private CustomerType customerType;
    private BigDecimal totalSpending;
    private CustomerLoyaltyLevel loyaltyLevel;
}