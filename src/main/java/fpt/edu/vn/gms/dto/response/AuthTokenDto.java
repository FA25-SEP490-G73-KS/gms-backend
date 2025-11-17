package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthTokenDto {

    private String accessToken;
    private String refreshToken;
}
