package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.request.CreateVoucherRequest;
import fpt.edu.vn.gms.dto.request.UpdateVoucherRequest;
import fpt.edu.vn.gms.dto.response.LedgerVoucherDetailResponse;
import fpt.edu.vn.gms.dto.response.LedgerVoucherListResponse;
import fpt.edu.vn.gms.entity.LedgerVoucher;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.repository.SupplierRepository;

@Mapper(componentModel = "spring")
public abstract class LedgerVoucherMapper {

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected SupplierRepository supplierRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "receiptHistory", ignore = true)
    public abstract LedgerVoucher toEntity(CreateVoucherRequest request);

    public abstract void updateEntityFromRequest(UpdateVoucherRequest request, @MappingTarget LedgerVoucher entity);

    @Mapping(target = "createdByEmployeeId", source = "createdBy.employeeId")
    @Mapping(target = "approvedByEmployeeId", source = "approvedBy.employeeId")
    @Mapping(target = "createdByEmployeeName", expression = "java(entity.getCreatedBy() != null ? entity.getCreatedBy().getFullName() : null)")
    @Mapping(target = "approvedByEmployeeName", expression = "java(entity.getApprovedBy() != null ? entity.getApprovedBy().getFullName() : null)")
    @Mapping(target = "relatedEmployeeName", ignore = true)
    @Mapping(target = "relatedSupplierName", ignore = true)
    public abstract LedgerVoucherDetailResponse toDetailDto(LedgerVoucher entity);

    @AfterMapping
    protected void mapRelatedNames(LedgerVoucher entity, @MappingTarget LedgerVoucherDetailResponse dto) {
        // Map relatedEmployeeName
        if (entity.getRelatedEmployeeId() != null) {
            employeeRepository.findById(entity.getRelatedEmployeeId())
                    .ifPresent(employee -> dto.setRelatedEmployeeName(employee.getFullName()));
        }

        // Map relatedSupplierName
        if (entity.getRelatedSupplierId() != null) {
            supplierRepository.findById(entity.getRelatedSupplierId())
                    .ifPresent(supplier -> dto.setRelatedSupplierName(supplier.getName()));
        }
    }

    @Mapping(target = "relatedEmployeeName", ignore = true)
    @Mapping(target = "relatedSupplierName", ignore = true)
    public abstract LedgerVoucherListResponse toListDto(LedgerVoucher entity);

    @AfterMapping
    protected void mapRelatedNamesForList(LedgerVoucher entity, @MappingTarget LedgerVoucherListResponse dto) {
        // Map relatedEmployeeName
        if (entity.getRelatedEmployeeId() != null) {
            employeeRepository.findById(entity.getRelatedEmployeeId())
                    .ifPresent(employee -> dto.setRelatedEmployeeName(employee.getFullName()));
        }

        // Map relatedSupplierName
        if (entity.getRelatedSupplierId() != null) {
            supplierRepository.findById(entity.getRelatedSupplierId())
                    .ifPresent(supplier -> dto.setRelatedSupplierName(supplier.getName()));
        }
    }
}
