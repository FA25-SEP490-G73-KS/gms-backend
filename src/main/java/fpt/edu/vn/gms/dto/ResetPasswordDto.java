package fpt.edu.vn.gms.dto;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String phone;
    private String otp;
    private String newPassword;
}