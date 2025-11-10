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

    @Mapping(source = "id", target = "purchaseRequestId")
    @Mapping(source = "relatedServiceTicket.vehicle", target = "vehicle") // nếu lấy từ serviceTicket
    @Mapping(source = "createdBy.fullName", target = "createdBy", defaultValue = "N/A") // hoặc tuỳ cấu trúc entity
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(target = "status", expression = "java(request.getStatus() != null ? request.getStatus().name() : null)")
    @Mapping(source = "items", target = "items")
    PurchaseRequestResponseDto toResponseDto(PurchaseRequest request);

    List<PurchaseRequestResponseDto> toResponseDtoList(List<PurchaseRequest> requests);
}
