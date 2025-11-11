package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PartResDto;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.VehicleModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PartMapper {

    PartMapper INSTANCE = Mappers.getMapper(PartMapper.class);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "compatibleVehicles", target = "compatibleVehicleIds", qualifiedByName = "mapVehiclesToIds")
    PartResDto toDto(Part part);

    @Named("mapVehiclesToIds")
    default Set<Long> mapVehiclesToIds(Set<VehicleModel> models) {
        return models == null ? Collections.emptySet()
                : models.stream().map(VehicleModel::getVehicleModelId).collect(Collectors.toSet());
    }
}
