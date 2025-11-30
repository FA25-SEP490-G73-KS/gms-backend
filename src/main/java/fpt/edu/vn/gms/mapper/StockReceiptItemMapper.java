package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.StockReceiptItemResponseDto;
import fpt.edu.vn.gms.entity.StockReceiptItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockReceiptItemMapper {

    @Mapping(target = "receiptItemId", source = "id")
    @Mapping(target = "receiptId", source = "stockReceipt.receiptId")
    @Mapping(target = "purchaseRequestItemId", source = "purchaseRequestItem.itemId")
    @Mapping(target = "purchaseRequestCode", source = "purchaseRequestItem.purchaseRequest.code")
    @Mapping(target = "partName", source = "purchaseRequestItem.partName")
    @Mapping(target = "unit", source = "purchaseRequestItem.unit")
    @Mapping(target = "requestedQuantity", source = "requestedQuantity")
    @Mapping(target = "quantityReceived", source = "quantityReceived")
    @Mapping(target = "actualUnitPrice", source = "actualUnitPrice")
    @Mapping(target = "actualTotalPrice", source = "actualTotalPrice")
    @Mapping(target = "totalQuantityReceived", source = "purchaseRequestItem.quantityReceived")
    StockReceiptItemResponseDto toDto(StockReceiptItem entity);

    List<StockReceiptItemResponseDto> toDtos(List<StockReceiptItem> items);
}
