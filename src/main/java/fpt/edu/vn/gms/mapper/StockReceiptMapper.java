package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.StockReceiptResponseDto;
import fpt.edu.vn.gms.entity.StockReceipt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockReceiptMapper {

    @Mapping(target = "receiptId", source = "receiptId")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "vehiclePlate",
            source = "purchaseRequest.relatedQuotation.serviceTicket.vehicle.licensePlate")
    @Mapping(target = "vehicleModelName",
            source = "purchaseRequest.relatedQuotation.serviceTicket.vehicle.vehicleModel.name")
    @Mapping(target = "createdByName", source = "createdBy.fullName")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "status", source = "status")
    StockReceiptResponseDto toDto(StockReceipt entity);
}
