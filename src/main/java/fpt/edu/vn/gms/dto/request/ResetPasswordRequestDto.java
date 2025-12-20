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
    private String phone;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
