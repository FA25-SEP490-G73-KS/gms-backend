package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.ManualTransactionItemResponse;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.StockExportItem;
import fpt.edu.vn.gms.entity.StockReceiptItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseManualTransactionMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "partSku", source = "part.sku")
    @Mapping(target = "partName", source = "part.name")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unit", source = "part.unit.name")
    @Mapping(target = "unitPrice", source = "part.purchasePrice")
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "quantityInStock", source = "part.quantityInStock")
    @Mapping(target = "reservedQuantity", source = "part.reservedQuantity")
    @Mapping(target = "note", source = "note")
    ManualTransactionItemResponse toResponseFromExportItem(StockExportItem item);

    // For manual receipt, we'll store Part directly in a dedicated field on StockReceiptItem or
    // map from Part passed explicitly; since existing entity binds to PurchaseRequestItem,
    // manual receipt service will set fields in response without relying on PurchaseRequestItem.

    default ManualTransactionItemResponse toResponseFromReceiptItem(StockReceiptItem item, Part part) {
        if (item == null || part == null) return null;
        ManualTransactionItemResponse res = new ManualTransactionItemResponse();
        res.setId(item.getId());
        res.setPartId(part.getPartId());
        res.setPartSku(part.getSku());
        res.setPartName(part.getName());
        res.setQuantity(item.getQuantityReceived());
        res.setUnit(part.getUnit() != null ? part.getUnit().getName() : null);
        res.setUnitPrice(item.getActualUnitPrice());
        res.setTotalPrice(item.getActualTotalPrice());
        res.setQuantityInStock(part.getQuantityInStock());
        res.setReservedQuantity(part.getReservedQuantity());
        res.setNote(item.getNote());
        return res;
    }
}
