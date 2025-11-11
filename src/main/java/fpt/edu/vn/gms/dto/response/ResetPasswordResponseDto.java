package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordResponseDto {

    private String phone;
    private String message;
}
