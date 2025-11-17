package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.StockReceiptItemResponseDto;
import fpt.edu.vn.gms.entity.StockReceiptItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockReceiptItemMapper {

    @Mapping(target = "purchaseRequestItemId", source = "purchaseRequestItem.itemId")
    StockReceiptItemResponseDto toDto(StockReceiptItem stockReceiptItem);
}
