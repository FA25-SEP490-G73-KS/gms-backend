package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.CustomerType;
import lombok.*;

import java.util.List;

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
    private List<String> licensePlates;
    private CustomerLoyaltyLevel loyaltyLevel;
}