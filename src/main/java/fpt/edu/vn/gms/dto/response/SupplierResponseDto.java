package fpt.edu.vn.gms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "DTO phản hồi thông tin nhà cung cấp")
public class SupplierResponseDto {
    @Schema(description = "ID nhà cung cấp", example = "1")
    private Long id;

    @Schema(description = "Tên nhà cung cấp", example = "Công ty TNHH ABC")
    private String name;

    @Schema(description = "Số điện thoại nhà cung cấp", example = "0901234567")
    private String phone;

    @Schema(description = "Email nhà cung cấp", example = "abc@company.com")
    private String email;

    @Schema(description = "Địa chỉ nhà cung cấp", example = "123 Đường Lê Lợi, Quận 1, TP.HCM")
    private String address;

    @Schema(description = "Trạng thái hoạt động", example = "true")
    private Boolean isActive;
}

