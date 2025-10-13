package fpt.edu.vn.gms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDto {
    private Long vehicleId;
    private Long customerId;
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private String vin;
}
