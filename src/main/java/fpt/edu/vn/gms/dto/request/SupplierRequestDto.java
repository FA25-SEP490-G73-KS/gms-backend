package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO yêu cầu tạo/cập nhật nhà cung cấp")
@Builder
public class SupplierRequestDto {
    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    @Size(max = 150, message = "Tên nhà cung cấp tối đa 150 ký tự")
    @Schema(description = "Tên nhà cung cấp", example = "Công ty TNHH ABC", required = true)
    private String name;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    @Schema(description = "Số điện thoại nhà cung cấp", example = "0901234567")
    private String phone;

    @Email(message = "Email không hợp lệ")
    @Size(max = 120, message = "Email tối đa 120 ký tự")
    @Schema(description = "Email nhà cung cấp", example = "abc@company.com")
    private String email;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    @Schema(description = "Địa chỉ nhà cung cấp", example = "123 Đường Lê Lợi, Quận 1, TP.HCM")
    private String address;
}
