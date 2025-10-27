package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.entity.PriceQuotation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {PriceQuotationItemMapper.class})
public interface PriceQuotationMapper {

    PriceQuotationMapper INSTANCE = Mappers.getMapper(PriceQuotationMapper.class);

    @Mapping(source = "priceQuotationId", target = "id")
    @Mapping(source = "serviceTicket.serviceTicketId", target = "serviceTicketId")
    @Mapping(source = "items", target = "items")
    PriceQuotationResponseDto toResponseDto(PriceQuotation entity);
}
