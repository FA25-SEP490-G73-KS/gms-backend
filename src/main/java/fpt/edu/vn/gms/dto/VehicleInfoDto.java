package fpt.edu.vn.gms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class VehicleInfoDto {

    private Long vehicleId;
    private String licensePlate;
    private Long brandId;
    private String brandName;
    private Long modelId;
    private String modelName;
    private String vin;
    private Integer year;
}
