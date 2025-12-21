package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import fpt.edu.vn.gms.entity.PurchaseRequestQuotation;
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
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class PurchaseRequestMapper {

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatDate")
    @Mapping(target = "reviewStatus", source = "reviewStatus", qualifiedByName = "formatStatus")
    @Mapping(target = "quotationId", ignore = true)
    @Mapping(target = "quotationCode", ignore = true)
    public abstract PurchaseRequestResponseDto toListDto(PurchaseRequest entity);

    @AfterMapping
    protected void mapQuotationInfoToList(PurchaseRequest entity, @MappingTarget PurchaseRequestResponseDto dto) {
        if (entity.getQuotations() != null && !entity.getQuotations().isEmpty()) {
            PurchaseRequestQuotation firstPrq = entity.getQuotations().get(0);
            dto.setQuotationId(firstPrq.getPriceQuotation().getPriceQuotationId());
            dto.setQuotationCode(firstPrq.getPriceQuotation().getCode());
        }
        // Fallback cho backward compatibility
        else if (entity.getRelatedQuotation() != null) {
            dto.setQuotationId(entity.getRelatedQuotation().getPriceQuotationId());
            dto.setQuotationCode(entity.getRelatedQuotation().getCode());
        }
    }

    // --------- DETAIL DTO ---------
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "quotationCode", ignore = true) // Sẽ set trong @AfterMapping
    @Mapping(target = "customerName", ignore = true) // Sẽ set trong @AfterMapping
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

    @AfterMapping
    protected void mapQuotationInfo(PurchaseRequest entity, @MappingTarget PurchaseRequestDetailDto dto) {
        // Nếu có nhiều quotations, hiển thị danh sách mã báo giá
        if (entity.getQuotations() != null && !entity.getQuotations().isEmpty()) {
            String quotationCodes = entity.getQuotations().stream()
                    .map(prq -> prq.getPriceQuotation().getCode())
                    .collect(Collectors.joining(", "));
            dto.setQuotationCode(quotationCodes);

            // Lấy customer name từ quotation đầu tiên
            PurchaseRequestQuotation firstPrq = entity.getQuotations().get(0);
            if (firstPrq.getPriceQuotation().getServiceTicket() != null
                    && firstPrq.getPriceQuotation().getServiceTicket().getCustomer() != null) {
                dto.setCustomerName(firstPrq.getPriceQuotation().getServiceTicket().getCustomer().getFullName());
            }
        }
        // Fallback cho backward compatibility nếu vẫn có relatedQuotation cũ
        else if (entity.getRelatedQuotation() != null) {
            dto.setQuotationCode(entity.getRelatedQuotation().getCode());
            if (entity.getRelatedQuotation().getServiceTicket() != null
                    && entity.getRelatedQuotation().getServiceTicket().getCustomer() != null) {
                dto.setCustomerName(entity.getRelatedQuotation().getServiceTicket().getCustomer().getFullName());
            }
        }
    }

    // --------- ITEM DTO ---------
    @Mapping(target = "sku", source = "part", qualifiedByName = "mapSku")
    @Mapping(target = "partName", source = "partName")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "estimatedPurchasePrice", source = "estimatedPurchasePrice", qualifiedByName = "mapMoneyToLong")
    @Mapping(target = "total", expression = "java(calcTotal(item))")
    @Mapping(target = "quotationCode", ignore = true)
    public abstract PurchaseRequestItemDto toItemDto(PurchaseRequestItem item);

    @AfterMapping
    protected void mapQuotationCodeToItem(PurchaseRequestItem item, @MappingTarget PurchaseRequestItemDto dto) {
        if (item.getQuotationItem() != null
                && item.getQuotationItem().getPriceQuotation() != null) {
            dto.setQuotationCode(item.getQuotationItem().getPriceQuotation().getCode());
        }
    }

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
