package fpt.edu.vn.gms.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {

    private String idToken;
    private String newPassword;
}
