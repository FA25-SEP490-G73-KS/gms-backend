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

    @Mapping(target = "itemId", source = "priceQuotationItemId")
    @Mapping(target = "itemName", source = "itemName")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "quantityInStock", source = "part.quantityInStock")
    @Mapping(target = "exportedQuantity", source = "exportedQuantity")
    @Mapping(target = "exportStatus", source = "exportStatus")
    StockExportItemResponse toStockExportItemResponse(PriceQuotationItem entity);
}