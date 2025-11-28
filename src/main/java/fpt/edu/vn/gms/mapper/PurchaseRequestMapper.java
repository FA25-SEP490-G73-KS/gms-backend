package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = PurchaseRequestItemMapper.class
)
public interface PurchaseRequestMapper {

    @Mapping(target = "licensePlate", source = "relatedQuotation.serviceTicket.vehicle.licensePlate")
    @Mapping(target = "customerName", source = "relatedQuotation.serviceTicket.customer.fullName")
    @Mapping(target = "createdBy", source = "relatedQuotation.serviceTicket.createdBy.fullName")
    PurchaseRequestResponseDto toResponseDto(PurchaseRequest request);

}
