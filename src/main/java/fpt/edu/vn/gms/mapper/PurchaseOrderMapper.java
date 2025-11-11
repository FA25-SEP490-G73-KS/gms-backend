package fpt.edu.vn.gms.mapper;


import fpt.edu.vn.gms.dto.response.PurchaseOrderResponseDto;
import fpt.edu.vn.gms.entity.PurchaseOrder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {

    PurchaseOrderResponseDto toDto(PurchaseOrder po);
}
