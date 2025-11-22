package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.entity.Part;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "marketName", source = "market.name")
    @Mapping(target = "quantity", source = "quantityInStock")
    @Mapping(target = "unitName", source = "unit.name")
    @Mapping(target = "reservedQuantity", source = "reservedQuantity")
    @Mapping(target = "reorderLevel", source = "reorderLevel")
    @Mapping(target = "modelName", source = "vehicleModel.name")
    PartReqDto toDto(Part part);

}
