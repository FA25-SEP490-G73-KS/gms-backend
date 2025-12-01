package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordRequest {

    @Schema(name = "Mật khẩu cũ")
    private String currentPassword;

    @Schema(name = "Mật khẩu mới")
    private String newPassword;

    @Schema(name = "Xác nhận mật khẩu")
    private String confirmPassword;

}
