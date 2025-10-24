package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleRequestDto {

    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private String vin;
}
