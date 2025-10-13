package fpt.edu.vn.gms.dto;

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
    private String customerType;
    private String loyaltyLevel;
}
