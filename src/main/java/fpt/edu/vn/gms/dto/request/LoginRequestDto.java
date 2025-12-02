package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequestDto {
    @Schema(description = "Số điện thoại", example = "0909123456", required = true)
    private String phone;

    @Schema(description = "Mật khẩu", example = "123456", required = true)
    private String password;

    // Thêm cờ rememberMe để quyết định thời gian sống của refresh token
    @Schema(description = "Ghi nhớ đăng nhập trên thiết bị này", example = "true", required = false)
    private Boolean rememberMe;
}
