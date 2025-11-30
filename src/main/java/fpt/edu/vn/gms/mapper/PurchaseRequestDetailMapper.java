package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                PurchaseRequestMapper.class, PurchaseRequestItemMapper.class
        }
)
public interface PurchaseRequestDetailMapper {

    @Mapping(target = "purchaseRequest", source = "request")
    @Mapping(target = "items", source = "request.items")
    PurchaseRequestDetailDto toDetailDto(PurchaseRequest request);

    List<PurchaseRequestItemResponseDto> toItemDtoList(List<PurchaseRequestItem> items);
}
