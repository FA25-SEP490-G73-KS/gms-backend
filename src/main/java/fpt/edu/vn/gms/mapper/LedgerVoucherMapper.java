package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.request.CreateVoucherRequest;
import fpt.edu.vn.gms.dto.request.UpdateVoucherRequest;
import fpt.edu.vn.gms.dto.response.LedgerVoucherDetailResponse;
import fpt.edu.vn.gms.dto.response.LedgerVoucherListResponse;
import fpt.edu.vn.gms.entity.LedgerVoucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LedgerVoucherMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "receiptHistory", ignore = true)
    LedgerVoucher toEntity(CreateVoucherRequest request);

    void updateEntityFromRequest(UpdateVoucherRequest request, @MappingTarget LedgerVoucher entity);

    @Mapping(target = "createdByEmployeeId", source = "createdBy.employeeId")
    @Mapping(target = "approvedByEmployeeId", source = "approvedBy.employeeId")
    LedgerVoucherDetailResponse toDetailDto(LedgerVoucher entity);

    LedgerVoucherListResponse toListDto(LedgerVoucher entity);
}
