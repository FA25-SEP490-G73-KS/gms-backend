package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ExportItemStatus;
import fpt.edu.vn.gms.common.enums.ExportStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.dto.request.ExportItemRequest;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.StockExportMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.StockExportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StockExportServiceImpl implements StockExportService {

    StockExportRepository stockExportRepository;
    StockExportItemRepository stockExportItemRepository;
    StockExportItemHistoryRepository stockExportItemHistoryRepository;
    PriceQuotationRepository priceQuotationRepository;
    PartRepository partRepository;
    EmployeeRepository employeeRepository;
    CodeSequenceService codeSequenceService;
    StockExportMapper stockExportMapper;

    @Transactional
    @Override
    public StockExportDetailResponse createExportFromQuotation(Long quotationId, String reason, Employee creator) {
        PriceQuotation quotation = priceQuotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá ID: " + quotationId));

        stockExportRepository.findByQuotationId(quotationId).ifPresent(se -> {
            throw new RuntimeException("Đã tồn tại phiếu xuất kho cho báo giá này");
        });

        StockExport export = StockExport.builder()
                .code(codeSequenceService.generateCode("XK"))
                .quotation(quotation)
                .reason(reason)
                .status(ExportStatus.WAITING_TO_CONFIRM)
                .createdBy(creator != null ? creator.getFullName() : null)
                .build();

        List<StockExportItem> items = quotation.getItems().stream()
                .filter(i -> i.getItemType() == PriceQuotationItemType.PART)
                .filter(i -> i.getInventoryStatus() == PriceQuotationItemStatus.AVAILABLE)
                .map(item -> StockExportItem.builder()
                        .stockExport(export)
                        .quotationItem(item)
                        .part(item.getPart())
                        .quantity(item.getQuantity())
                        .quantityExported(0.0)
                        .status(ExportItemStatus.EXPORTING)
                        .build())
                .collect(Collectors.toList());

        export.setExportItems(items);

        StockExport saved = stockExportRepository.save(export);

        StockExportDetailResponse detail = stockExportMapper.toDetailDto(saved);
        List<StockExportItemResponse> itemDtos = saved.getExportItems().stream()
                .map(stockExportMapper::toItemDto)
                .toList();
        detail.setItems(itemDtos);
        return detail;
    }

    @Override
    public Page<StockExportListResponse> getExports(String keyword, String status, String fromDate, String toDate, Pageable pageable) {
        Page<StockExport> page = stockExportRepository.findAll(pageable);

        List<StockExport> filtered = page.getContent().stream()
                .filter(se -> {
                    if (keyword == null || keyword.isBlank()) return true;
                    String lower = keyword.toLowerCase();
                    return (se.getCode() != null && se.getCode().toLowerCase().contains(lower))
                            || (se.getQuotation() != null && se.getQuotation().getCode() != null
                            && se.getQuotation().getCode().toLowerCase().contains(lower));
                })
                .filter(se -> {
                    if (status == null || status.isBlank()) return true;
                    return se.getStatus().name().equalsIgnoreCase(status);
                })
                .filter(se -> filterByDateRange(se, fromDate, toDate))
                .toList();

        List<StockExportListResponse> dtos = filtered.stream()
                .map(stockExportMapper::toListDto)
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    private boolean filterByDateRange(StockExport export, String fromDate, String toDate) {
        if ((fromDate == null || fromDate.isBlank()) && (toDate == null || toDate.isBlank())) {
            return true;
        }
        LocalDate createdDate = Optional.ofNullable(export.getCreatedAt())
                .map(LocalDateTime::toLocalDate)
                .orElse(null);
        if (createdDate == null) return false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (fromDate != null && !fromDate.isBlank()) {
            LocalDate from = LocalDate.parse(fromDate, formatter);
            if (createdDate.isBefore(from)) return false;
        }
        if (toDate != null && !toDate.isBlank()) {
            LocalDate to = LocalDate.parse(toDate, formatter);
            if (createdDate.isAfter(to)) return false;
        }
        return true;
    }

    @Override
    public StockExportDetailResponse getExportDetail(Long id) {
        StockExport export = stockExportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu xuất kho"));

        StockExportDetailResponse detail = stockExportMapper.toDetailDto(export);
        List<StockExportItemResponse> itemDtos = export.getExportItems().stream()
                .map(stockExportMapper::toItemDto)
                .toList();
        detail.setItems(itemDtos);
        return detail;
    }

    @Override
    public Page<StockExportItemResponse> getExportItems(Long exportId, Pageable pageable) {
        StockExport export = stockExportRepository.findById(exportId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu xuất kho"));

        List<StockExportItemResponse> items = export.getExportItems().stream()
                .map(stockExportMapper::toItemDto)
                .toList();

        return new PageImpl<>(items, pageable, items.size());
    }

    @Override
    public ExportItemDetailResponse getExportItemDetail(Long itemId) {
        StockExportItem item = stockExportItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng xuất kho"));

        List<StockExportItemHistory> histories = stockExportItemHistoryRepository.findByStockExportItem_Id(itemId);
        return stockExportMapper.toItemDetailDto(item, histories);
    }

    @Transactional
    @Override
    public StockExportItemResponse exportItem(Long itemId, ExportItemRequest request, Employee exportedBy) {
        StockExportItem item = stockExportItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng xuất kho"));

        if (item.getStatus() == ExportItemStatus.FINISHED) {
            throw new RuntimeException("Dòng xuất kho đã hoàn thành");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Số lượng xuất phải > 0");
        }

        double alreadyExported = Optional.ofNullable(item.getQuantityExported()).orElse(0.0);
        double remaining = item.getQuantity() - alreadyExported;

        if (request.getQuantity() > remaining) {
            throw new IllegalArgumentException("Số lượng xuất vượt quá số lượng còn lại");
        }

        Part part = item.getPart();
        double inStock = Optional.ofNullable(part.getQuantityInStock()).orElse(0.0);
        double reserved = Optional.ofNullable(part.getReservedQuantity()).orElse(0.0);

        if (inStock < request.getQuantity()) {
            throw new IllegalArgumentException("Không đủ tồn kho để xuất");
        }

        // Cập nhật tồn kho
        part.setQuantityInStock(inStock - request.getQuantity());
        part.setReservedQuantity(Math.max(0.0, reserved - request.getQuantity()));
        partRepository.save(part);

        // Lấy nhân viên nhận linh kiện
        Employee employee = employeeRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên nhận linh kiện"));

        // Cập nhật dòng xuất
        double newExported = alreadyExported + request.getQuantity();
        item.setQuantityExported(newExported);
        item.setReceiver(employee); // hoặc tìm theo request.getReceiverId()
        item.setExportedAt(LocalDateTime.now());
        item.setNote(request.getNote());

        if (newExported >= item.getQuantity()) {
            item.setStatus(ExportItemStatus.FINISHED);
        } else {
            item.setStatus(ExportItemStatus.EXPORTING);
        }

        stockExportItemRepository.save(item);

        // Lưu history
        StockExportItemHistory history = StockExportItemHistory.builder()
                .stockExportItem(item)
                .quantity(request.getQuantity())
                .exportedBy(exportedBy)
                .build();
        stockExportItemHistoryRepository.save(history);

        // Auto update status phiếu xuất
        updateExportStatus(item.getStockExport());

        return stockExportMapper.toItemDto(item);
    }

    @Override
    public ExportItemDetailResponse getExportItemHistory(Long itemId) {
        StockExportItem item = stockExportItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng xuất kho"));
        List<StockExportItemHistory> histories = stockExportItemHistoryRepository.findByStockExportItem_Id(itemId);
        return stockExportMapper.toItemDetailDto(item, histories);
    }

    @Transactional
    @Override
    public StockExportDetailResponse approveExport(Long id, Employee approver) {
        StockExport export = stockExportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu xuất kho"));

        if (export.getStatus() != ExportStatus.WAITING_TO_CONFIRM) {
            throw new RuntimeException("Chỉ có thể duyệt phiếu ở trạng thái chờ xác nhận");
        }

        export.setStatus(ExportStatus.WAITING_TO_EXECUTE);
        export.setApprovedBy(approver.getFullName());
        export.setApprovedAt(LocalDateTime.now());
        stockExportRepository.save(export);

        return getExportDetail(id);
    }

    @Transactional
    @Override
    public StockExportDetailResponse cancelExport(Long id, Employee canceller) {
        StockExport export = stockExportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu xuất kho"));

        if (export.getStatus() == ExportStatus.COMPLETED) {
            throw new RuntimeException("Không thể hủy phiếu đã hoàn thành");
        }

        export.setStatus(ExportStatus.WAITING_TO_CONFIRM);
        stockExportRepository.save(export);

        return getExportDetail(id);
    }

    @Transactional
    @Override
    public StockExportDetailResponse markCompleted(Long id, Employee employee) {
        StockExport export = stockExportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu xuất kho"));

        updateExportStatus(export);

        return getExportDetail(id);
    }

    private void updateExportStatus(StockExport export) {
        List<StockExportItem> items = export.getExportItems();

        boolean allFinished = items.stream()
                .allMatch(i -> i.getStatus() == ExportItemStatus.FINISHED);

        if (allFinished) {
            export.setStatus(ExportStatus.COMPLETED);
            export.setExportedAt(LocalDateTime.now());
        } else {
            export.setStatus(ExportStatus.WAITING_TO_EXECUTE);
        }

        stockExportRepository.save(export);
    }
}

