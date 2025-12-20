package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import fpt.edu.vn.gms.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class PurchaseRequestMapper {

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatDate")
    @Mapping(target = "reviewStatus", source = "reviewStatus", qualifiedByName = "formatStatus")
    public abstract PurchaseRequestResponseDto toListDto(PurchaseRequest entity);

    // --------- DETAIL DTO ---------
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "quotationCode", source = "relatedQuotation.code")
    @Mapping(target = "customerName", source = "relatedQuotation.serviceTicket.customer.fullName")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatDate")
    @Mapping(target = "reviewStatus", source = "reviewStatus", qualifiedByName = "formatStatus")
    @Mapping(target = "items", source = "items")
    public abstract PurchaseRequestDetailDto toDetailDto(PurchaseRequest entity);

    @AfterMapping
    protected void mapCreatedBy(PurchaseRequest entity, @MappingTarget PurchaseRequestDetailDto dto) {
        if (entity.getCreatedBy() != null) {
            employeeRepository.findById(entity.getCreatedBy())
                    .ifPresent(employee -> dto.setCreatedBy(employee.getFullName()));
        }
    }

    // --------- ITEM DTO ---------
    @Mapping(target = "sku", source = "part", qualifiedByName = "mapSku")
    @Mapping(target = "partName", source = "partName")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "estimatedPurchasePrice", source = "estimatedPurchasePrice", qualifiedByName = "mapMoneyToLong")
    @Mapping(target = "total", expression = "java(calcTotal(item))")
    public abstract PurchaseRequestItemDto toItemDto(PurchaseRequestItem item);

    public abstract List<PurchaseRequestItemDto> toItemDtos(List<PurchaseRequestItem> items);

    // --------- HELPERS ---------
    @Named("formatDate")
    protected String formatDate(LocalDateTime time) {
        if (time == null)
            return null;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return time.format(fmt);
    }

    @Named("formatStatus")
    protected String formatStatus(ManagerReviewStatus status) {
        if (status == null)
            return null;
        return switch (status) {
            case PENDING -> "Chờ duyệt";
            case APPROVED -> "Đã duyệt";
            case REJECTED -> "Từ chối";
        };
    }

    @Named("mapSku")
    protected String mapSku(Part part) {
        return part != null ? part.getSku() : null;
    }

    @Named("mapMoneyToLong")
    protected Long mapMoneyToLong(BigDecimal money) {
        return Optional.ofNullable(money).map(BigDecimal::longValue).orElse(0L);
    }

    protected Long calcTotal(PurchaseRequestItem item) {
        BigDecimal unitPrice = Optional.ofNullable(item.getEstimatedPurchasePrice()).orElse(BigDecimal.ZERO);
        double qty = Optional.ofNullable(item.getQuantity()).orElse(0.0);
        return unitPrice.multiply(BigDecimal.valueOf(qty)).longValue();
    }
}
