package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LicensePlateCheckRequest {
    @Schema(description = "Biển số xe", example = "30A-12345", required = true)
    @NotBlank(message = "Biển số xe không được để trống")
    private String licensePlate;

    @Schema(description = "ID khách hàng", example = "1", required = true)
    @NotNull(message = "ID khách hàng không được để trống")
    private Long customerId;
}