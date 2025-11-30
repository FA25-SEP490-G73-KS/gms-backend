package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.dto.response.StockExportResponse;
import fpt.edu.vn.gms.entity.PriceQuotation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PriceQuotationItemMapper.class})
public interface PriceQuotationMapper {

    @Mapping(target = "priceQuotationId", source = "priceQuotationId")
    @Mapping(target = "serviceTicketCode", source = "serviceTicket.serviceTicketCode")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "licensePlate", source = "serviceTicket.vehicle.licensePlate")
    @Mapping(target = "customerName", source = "serviceTicket.customerName")
    @Mapping(target = "customerPhone", source = "serviceTicket.customerPhone")
    @Mapping(target = "createdBy", source = "serviceTicket.createdBy.fullName")
    PriceQuotationResponseDto toResponseDto(PriceQuotation entity);

    @Mapping(target = "priceQuotationId", source = "priceQuotationId")
    @Mapping(target = "customerName", source = "serviceTicket.customer.fullName")
    @Mapping(target = "licensePlate", source = "serviceTicket.vehicle.licensePlate")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "exportStatus", source = "exportStatus")
    StockExportResponse toStockExportResponse(PriceQuotation entity);
}
