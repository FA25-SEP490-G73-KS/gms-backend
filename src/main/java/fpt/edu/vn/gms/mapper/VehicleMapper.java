package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    // map vehicle -> vehicle info
    @Mapping(target = "brandName", source = "vehicleModel.brand.name")
    @Mapping(target = "modelName", source = "vehicleModel.name")
    VehicleInfoDto toVehicleInfoDto(Vehicle vehicle);

    // map danh sách vehicle
    List<VehicleInfoDto> toVehicleInfoDtos(List<Vehicle> vehicles);
}
