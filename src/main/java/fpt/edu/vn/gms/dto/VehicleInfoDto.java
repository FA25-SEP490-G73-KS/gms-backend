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
    private String brandName;
    private String modelName;
    private String vin;
    private Integer year;

    public VehicleInfoDto(String licensePlate, String brandName, String modelName, String vin, Integer year) {
        this.licensePlate = licensePlate;
        this.brandName = brandName;
        this.modelName = modelName;
        this.vin = vin;
        this.year = year;
    }


}
