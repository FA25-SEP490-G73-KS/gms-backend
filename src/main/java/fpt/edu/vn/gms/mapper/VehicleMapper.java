package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.dto.response.VehicleResponseDto;
import fpt.edu.vn.gms.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    // map vehicle -> vehicle info
    @Mapping(target = "vehicleId", source = "vehicleId")
    @Mapping(target = "brandId", source = "vehicleModel.brand.brandId")
    @Mapping(target = "brandName", source = "vehicleModel.brand.name")
    @Mapping(target = "vehicleModelName", source = "vehicleModel.name")
    @Mapping(target = "vehicleModelId", source = "vehicleModel.vehicleModelId")
    VehicleResponseDto toDto(Vehicle vehicle);

    // map danh sách vehicle
    List<VehicleInfoDto> toVehicleInfoDtos(List<Vehicle> vehicles);
}
