package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.ExpenseVoucherResponseDto;
import fpt.edu.vn.gms.entity.ExpenseVoucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseVoucherMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "target", source = "target")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "createdBy", source = "createdBy.fullName")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "attachmentUrl", source = "attachmentUrl")
    ExpenseVoucherResponseDto toDto(ExpenseVoucher entity);
}
