package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.CustomerType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerRequestDto {

     private Long customerId;
     private String fullName;
     private String phone;
     private String address;
     private CustomerType customerType;
     private CustomerLoyaltyLevel loyaltyLevel;
}
