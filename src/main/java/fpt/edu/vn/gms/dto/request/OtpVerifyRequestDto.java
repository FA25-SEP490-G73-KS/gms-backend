package fpt.edu.vn.gms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequestDto {
    private String phone;
    private String otpCode;
    private String purpose; // APPOINTMENT, RESET_PASSWORD, etc.
}

