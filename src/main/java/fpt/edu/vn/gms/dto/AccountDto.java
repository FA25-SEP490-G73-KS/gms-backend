package fpt.edu.vn.gms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private Long accountId;
    private String phone;
    private Long roleId;
    private String password;
}
