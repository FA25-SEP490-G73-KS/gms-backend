package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.dto.response.StockExportItemResponse;
import fpt.edu.vn.gms.dto.response.StockExportResponse;
import fpt.edu.vn.gms.entity.PriceQuotation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PriceQuotationItemMapper.class})
public interface PriceQuotationMapper {

    @Mapping(target = "priceQuotationId", source = "priceQuotationId")
    @Mapping(target = "serviceTicketCode", source = "serviceTicket.serviceTicketCode")
    @Mapping(target = "licensePlate", source = "serviceTicket.vehicle.licensePlate")
    PriceQuotationResponseDto toResponseDto(PriceQuotation entity);

    StockExportResponse toStockExportResponse(PriceQuotation entity);


}
