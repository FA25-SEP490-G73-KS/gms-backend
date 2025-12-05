package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.checkerframework.checker.units.qual.N;

@Data
@Builder
public class ResetPasswordRequestDto {

    @NotBlank
    @Schema(name = "Số điện thoại nhân viên")
    private String phone;

    @NotBlank
    @Schema(name = "Mật khẩu mới")
    private String newPassword;

    @NotBlank
    @Schema(name = "Xác nhận mật khẩu")
    private String confirmPassword;
}
