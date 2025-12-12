package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordRequest {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

}
