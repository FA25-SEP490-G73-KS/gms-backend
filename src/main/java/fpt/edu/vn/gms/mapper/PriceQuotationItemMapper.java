package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PriceQuotationItemMapper {

    PriceQuotationItemMapper INSTANCE = Mappers.getMapper(PriceQuotationItemMapper.class);

    @Mapping(target = "priceQuotationItemId", source = "priceQuotationItemId")
    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "partName", source = "itemName")
    @Mapping(target = "itemType", source = "itemType")
    PriceQuotationItemResponseDto toResponseDto(PriceQuotationItem entity);
}