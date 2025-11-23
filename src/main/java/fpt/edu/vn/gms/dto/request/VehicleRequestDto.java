package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

@Schema(description = "Thông tin xe trong phiếu dịch vụ")
public class VehicleRequestDto {

    @Schema(description = "ID xe (nullable nếu tạo mới)", example = "1")
    private Long vehicleId;

    @Schema(description = "Biển số xe", example = "30A-1004", required = true)
    private String licensePlate;

    @Schema(description = "ID hãng xe", example = "1")
    private Long brandId;
    private String brandName;

    @Schema(description = "ID mẫu xe", example = "1")
    private Long modelId;
    private String modelName;

    @Schema(description = "Năm sản xuất", example = "2020")
    private Integer year;

    @Schema(description = "Số VIN", example = "VIN123456")
    private String vin;
}
