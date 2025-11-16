package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.StockExportItemResponse;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PriceQuotationItemMapper {

    @Mapping(target = "priceQuotationItemId", source = "priceQuotationItemId")
    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "partName", source = "itemName")
    @Mapping(target = "itemType", source = "itemType")
    @Mapping(target = "unit", source = "unit")
    PriceQuotationItemResponseDto toResponseDto(PriceQuotationItem entity);

    StockExportItemResponse toStockExportItemResponse(PriceQuotationItem entity);
}