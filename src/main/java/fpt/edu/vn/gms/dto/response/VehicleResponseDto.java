package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponseDto {

    private Long vehicleId;
    private String licensePlate;
    private String vin;
    private Integer year;
    private Long customerId;
    private Long vehicleModelId;
    private String vehicleModelName;
    private String brandName;
}
