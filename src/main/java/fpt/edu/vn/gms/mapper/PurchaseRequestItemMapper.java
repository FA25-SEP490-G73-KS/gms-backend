package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PurchaseRequestItemMapper {

    PurchaseRequestItemResponseDto toResponseDto(PurchaseRequestItem item);

}