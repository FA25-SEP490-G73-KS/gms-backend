package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PrDetailInfoReviewDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = PurchaseRequestItemMapper.class
)
public interface PurchaseRequestMapper {


    @Mapping(target = "customerName", source = "relatedQuotation.serviceTicket.customerName")
    @Mapping(target = "customerPhone", source = "relatedQuotation.serviceTicket.customerPhone")
    @Mapping(target = "createdBy", source = "relatedQuotation.serviceTicket.createdBy.fullName")
    PurchaseRequestResponseDto toResponseDto(PurchaseRequest request);

    @Mapping(target = "prCode", source = "code")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "quotationCode", source = "relatedQuotation.code")
    @Mapping(target = "licensePlate", source = "relatedQuotation.serviceTicket.vehicleLicensePlate")
    @Mapping(target = "customerName", source = "relatedQuotation.serviceTicket.customerName")
    @Mapping(target = "customerPhone", source = "relatedQuotation.serviceTicket.customerPhone")
    @Mapping(target = "items", source = "items")
    PrDetailInfoReviewDto toDto(PurchaseRequest request);

}
