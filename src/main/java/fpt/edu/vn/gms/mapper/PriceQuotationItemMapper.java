package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PaymentItemDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { PartMapper.class})
public interface PriceQuotationItemMapper {

    @Mapping(target = "priceQuotationItemId", source = "priceQuotationItemId")
    @Mapping(target = "part", source = "part")
    @Mapping(target = "itemName", source = "itemName")
    @Mapping(target = "itemType", source = "itemType")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "exportedQuantity", source = "exportedQuantity")
    PriceQuotationItemResponseDto toResponseDto(PriceQuotationItem entity);

    @Mapping(target = "name", source = "itemName")
    @Mapping(target = "totalPrice", source = "totalPrice")
    PaymentItemDto toDto(PriceQuotationItem item);
}