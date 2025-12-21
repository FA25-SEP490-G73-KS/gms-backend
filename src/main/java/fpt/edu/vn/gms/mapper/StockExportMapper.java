package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.StockExport;
import fpt.edu.vn.gms.entity.StockExportItem;
import fpt.edu.vn.gms.entity.StockExportItemHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface StockExportMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "customerName", source = "quotation.serviceTicket.customer.fullName")
    @Mapping(target = "quotationCode", source = "quotation.code")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "status", source = "status")
    StockExportListResponse toListDto(StockExport export);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "quotationId", source = "quotation.priceQuotationId")
    @Mapping(target = "quotationCode", source = "quotation.code")
    @Mapping(target = "serviceTicketId", source = "quotation.serviceTicket.serviceTicketId")
    @Mapping(target = "serviceTicketStatus", ignore = true)
    @Mapping(target = "customerName", source = "quotation.serviceTicket.customer.fullName")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "approvedBy", source = "approvedBy")
    @Mapping(target = "exportedBy", source = "exportedBy")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "approvedAt", source = "approvedAt")
    @Mapping(target = "exportedAt", source = "exportedAt")
    @Mapping(target = "items", ignore = true)
    StockExportDetailResponse toDetailDto(StockExport export);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "sku", expression = "java(getSku(item.getPart()))")
    @Mapping(target = "name", expression = "java(getName(item))")
    @Mapping(target = "quantityRequired", source = "quantity")
    @Mapping(target = "quantityExported", source = "quantityExported")
    @Mapping(target = "status", source = "status")
    StockExportItemResponse toItemDto(StockExportItem item);

    default String getSku(Part part) {
        return part != null ? part.getSku() : null;
    }

    default String getName(StockExportItem item) {
        if (item.getPart() != null) {
            return item.getPart().getName();
        }
        return Optional.ofNullable(item.getQuotationItem())
                .map(q -> q.getItemName())
                .orElse(null);
    }

    default ExportItemDetailResponse toItemDetailDto(StockExportItem item, List<StockExportItemHistory> histories) {
        double required = Optional.ofNullable(item.getQuantity()).orElse(0.0);
        double exported = Optional.ofNullable(item.getQuantityExported()).orElse(0.0);
        double remaining = required - exported;

        // Lấy số lượng tồn kho hiện tại
        double quantityInStock = Optional.ofNullable(item.getPart())
                .map(Part::getQuantityInStock)
                .orElse(0.0);

        List<ExportItemDetailResponse.ExportItemHistoryDto> historyDtos = histories.stream()
                .map(this::toHistoryDto)
                .toList();

        return ExportItemDetailResponse.builder()
                .id(item.getId())
                .sku(getSku(item.getPart()))
                .name(getName(item))
                .required(required)
                .exported(exported)
                .remaining(remaining)
                .status(item.getStatus().name())
                .quantityInStock(quantityInStock)
                .history(historyDtos)
                .build();
    }

    default ExportItemDetailResponse.ExportItemHistoryDto toHistoryDto(StockExportItemHistory history) {
        return ExportItemDetailResponse.ExportItemHistoryDto.builder()
                .id(history.getId())
                .quantity(history.getQuantity())
                .exportedAt(history.getExportedAt())
                .exportedById(history.getExportedBy() != null ? history.getExportedBy().getEmployeeId() : null)
                .exportedByName(history.getExportedBy() != null ? history.getExportedBy().getFullName() : null)
                .build();
    }
}
