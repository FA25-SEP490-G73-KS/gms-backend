package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.ExportStatus;
import fpt.edu.vn.gms.common.PriceQuotationItemType;
import fpt.edu.vn.gms.dto.response.StockExportItemResponse;
import fpt.edu.vn.gms.dto.response.StockExportResponse;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.StockExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockExportServiceImpl implements StockExportService {

    private final PriceQuotationRepository quotationRepository;
    private final PriceQuotationItemRepository itemRepository;
    private final PartRepository partRepository;
    private final StockExportRepository exportRepository;
    private final EmployeeRepository employeeRepository;
    private final StockExportItemRepository stockExportItemRepository;
    private final CodeSequenceService codeSequenceService;
    private final PriceQuotationMapper priceQuotationMapper;
    private final PriceQuotationItemMapper itemMapper;

    @Override
    public Page<StockExportResponse> getExportingQuotations(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());

        // Lấy tất cả báo giá có exportStatus = WAITING_TO_EXPORT
        Page<PriceQuotation> quotations = quotationRepository.findByExportStatus(
                ExportStatus.WAITING_TO_EXPORT,
                pageable
        );

        return quotations.map(priceQuotationMapper::toStockExportResponse);
    }

    @Override
    public List<StockExportItemResponse> getExportingQuotationById(Long quotationId) {

        PriceQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá ID: " + quotationId));

        return quotation.getItems().stream()
                .filter(item -> item.getItemType() == PriceQuotationItemType.PART)
                .map(itemMapper::toStockExportItemResponse)
                .toList();
    }

    @Transactional
    @Override
    public StockExportItemResponse exportItem(Long quotationItemId, Double exportQty, Long receiverId) {

        // Lấy item báo giá
        PriceQuotationItem item = itemRepository.findById(quotationItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy item báo giá"));


        if (item.getItemType() != PriceQuotationItemType.PART) {
            throw new RuntimeException("Chỉ xuất kho cho PART item");
        }

        Part part = item.getPart();
        if (part == null) {
            throw new RuntimeException("Item không có Part để xuất");
        }

        // Tính toán exported mới
        double exported = Optional.ofNullable(item.getExportedQuantity()).orElse(0.0);
        double newExported = exported + exportQty;

        // Không cho xuất quá số lượng item
        if (newExported > item.getQuantity()) {
            throw new RuntimeException("Số lượng xuất vượt quá số lượng cần xuất của item");
        }

        // Trừ tồn kho
        part.setQuantityInStock(
                Optional.ofNullable(part.getQuantityInStock()).orElse(0.0) - exportQty
        );

        // Trừ reserved
        part.setReservedQuantity(
                Optional.ofNullable(part.getReservedQuantity()).orElse(0.0) - exportQty
        );

        partRepository.save(part);

        // Cập nhật exported của item
        item.setExportedQuantity(newExported);

        if (newExported >= item.getQuantity()) {
            item.setExportStatus(ExportStatus.EXPORTED);
        } else {
            item.setExportStatus(ExportStatus.WAITING_TO_EXPORT);
        }

        itemRepository.save(item);

        StockExport export = exportRepository.findByQuotationId(
                item.getPriceQuotation().getPriceQuotationId()
        ).orElseGet(() -> {
            StockExport ex = StockExport.builder()
                    .quotation(item.getPriceQuotation())
                    .code(codeSequenceService.generateCode("EX"))
                    .createdAt(LocalDateTime.now())
                    .build();
            return exportRepository.save(ex);
        });


        Employee employee = employeeRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên ID: " + receiverId));

        // Tạo dòng xuất kho (StockExportItem)
        StockExportItem exportItem = StockExportItem.builder()
                .stockExport(export)
                .quotationItem(item)
                .quantity(exportQty)
                .unit(item.getUnit())
                .receiver(employee)
                .build();

        stockExportItemRepository.save(exportItem);

        return itemMapper.toStockExportItemResponse(item);
    }

}
