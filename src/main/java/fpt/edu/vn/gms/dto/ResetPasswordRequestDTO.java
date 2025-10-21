package fpt.edu.vn.gms.dto;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {

    private String idToken;
    private String newPassword;
}
