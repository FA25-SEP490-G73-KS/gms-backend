package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.ManualVoucherListResponseDto;
import fpt.edu.vn.gms.dto.response.ManualVoucherResponseDto;
import fpt.edu.vn.gms.entity.ManualVoucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManualVoucherMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "targetName", ignore = true)
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "createdBy", source = "createdBy.fullName")
    @Mapping(target = "approvedBy", source = "approvedBy.fullName")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "attachmentUrl", source = "attachmentUrl")
    ManualVoucherResponseDto toDto(ManualVoucher entity);

    @Mapping(target = "targetName", ignore = true)
    ManualVoucherListResponseDto toListDto(ManualVoucher mv);
}
