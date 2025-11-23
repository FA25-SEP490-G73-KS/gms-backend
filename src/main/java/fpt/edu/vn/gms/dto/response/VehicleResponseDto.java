package fpt.edu.vn.gms.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDto {

    private Long vehicleId;
    private String licensePlate;
    private String vin;
    private Integer year;
    private Long vehicleModelId;
    private String vehicleModelName;
    private String brandName;
}
