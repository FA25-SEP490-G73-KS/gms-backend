package fpt.edu.vn.gms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleInfoDto {
    private Long vehicleId;
    private String licensePlate;
    private String brandName;
    private String modelName;
    private String vin;
    private Integer year;
}
