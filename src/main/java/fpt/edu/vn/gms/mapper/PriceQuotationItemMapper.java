package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PriceQuotationItemMapper {

    PriceQuotationItemMapper INSTANCE = Mappers.getMapper(PriceQuotationItemMapper.class);

    @Mapping(source = "part.name", target = "partName")
    @Mapping(source = "status", target = "status")
    PriceQuotationItemResponseDto toResponseDto(PriceQuotationItem entity);
}