package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = { PartMapper.class } )
public interface PurchaseRequestItemMapper {

    @Mapping(target = "part", source = "part")
    PurchaseRequestItemResponseDto toResponseDto(PurchaseRequestItem item);
}