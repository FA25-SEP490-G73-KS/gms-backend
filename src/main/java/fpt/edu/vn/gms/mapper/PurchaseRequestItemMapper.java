package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = { PartMapper.class, StockReceiptItemMapper.class })
public interface PurchaseRequestItemMapper {

    @Mapping(target = "part", source = "part")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "stockReceipt", source = "purchaseRequest.stockReceipt.items")
    PurchaseRequestItemResponseDto toResponseDto(PurchaseRequestItem item);
}