package fpt.edu.vn.gms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicensePlateCheckResponseDto {
    @Schema(description = "Biển số xe có tồn tại không", example = "true")
    private boolean isExists;

    @Schema(description = "Biển số xe thuộc về đúng khách hàng không", example = "false")
    private boolean isSameCustomer;

    @Schema(description = "Biển số xe", example = "30A-12345")
    private String licensePlate;

    @Schema(description = "Tên khách hàng", example = "Nguyen Van A")
    private String customerName;

    @Schema(description = "Số điện thoại khách hàng", example = "0912345678")
    private String customerPhone;
}