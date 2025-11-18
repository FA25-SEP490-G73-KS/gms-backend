package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleRequestDto {

    private Long vehicleId;
    private String licensePlate;
    private Long brandId;
    private Long modelId;
    private String modelName;
    private Integer year;
    private String vin;
}
