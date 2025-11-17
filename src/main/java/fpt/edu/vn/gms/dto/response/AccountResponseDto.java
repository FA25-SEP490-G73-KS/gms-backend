package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountResponseDto {

    private Long accountId;
    private String phone;
    private String role;
}
