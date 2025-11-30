package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PaymentItemDto;
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
    @Mapping(target = "categoryName", source = "part.category.name")
    @Mapping(target = "marketName", source = "part.market.name")
    @Mapping(target = "supplierName", source = "part.supplier.name")
    @Mapping(target = "brandName", source = "part.vehicleModel.brand.name")
    @Mapping(target = "modelName", source = "part.vehicleModel.name")
    @Mapping(target = "purchasePrice", source = "part.purchasePrice")
    @Mapping(target = "sellingPrice", source = "part.sellingPrice")
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

    @Mapping(target = "name", source = "itemName")
    @Mapping(target = "totalPrice", source = "totalPrice")
    PaymentItemDto toDto(PriceQuotationItem item);
}