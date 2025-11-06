package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseRequestItemMapper {
//
//    @Mapping(target = "partName", expression = "java(getPartName(item))")
//    @Mapping(source = "quantity", target = "quantity")
//    @Mapping(target = "vehicleModel", expression = "java(getVehicleModel(item))")
//    @Mapping(target = "origin", expression = "java(getOrigin(item))")
//    @Mapping(target = "status", expression = "java(item.getStatus() != null ? item.getStatus().name() : null)")
//    @Mapping(source = "note", target = "warehouseNote")
//    PurchaseRequestItemResponseDto toResponseDto(PurchaseRequestItem item);
//
//    List<PurchaseRequestItemResponseDto> toResponseDtoList(List<PurchaseRequestItem> items);
//
//    // -----------------------
//    // Helper methods
//    // -----------------------
//    default String getPartName(PurchaseRequestItem item) {
//        if (item.getPart() != null && item.getPart().getName() != null)
//            return item.getPart().getName();
//        if (item.getQuotationItem() != null && item.getQuotationItem().getItemName() != null)
//            return item.getQuotationItem().getItemName();
//        return "N/A";
//    }
//
//    default String getVehicleModel(PurchaseRequestItem item) {
//        if (item.getPart() != null && item.getPart().getCompatibleVehicles() != null && !item.getPart().getCompatibleVehicles().isEmpty())
//            return item.getPart().getCompatibleVehicles().iterator().next().getName();
//        return null;
//    }
//
//    default String getOrigin(PurchaseRequestItem item) {
//        if (item.getPart() != null && item.getPart().getMarket() != null)
//            return item.getPart().getMarket().toString();
//        return null;
//    }

}