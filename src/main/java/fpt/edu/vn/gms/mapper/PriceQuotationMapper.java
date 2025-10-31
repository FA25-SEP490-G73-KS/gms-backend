package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.entity.PriceQuotation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PriceQuotationItemMapper.class})
public interface PriceQuotationMapper {

    @Mapping(target = "priceQuotationId", source = "priceQuotationId")
    PriceQuotationResponseDto toResponseDto(PriceQuotation entity);
}
