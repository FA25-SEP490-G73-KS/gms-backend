package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequestDto {

    private String phone;
}
