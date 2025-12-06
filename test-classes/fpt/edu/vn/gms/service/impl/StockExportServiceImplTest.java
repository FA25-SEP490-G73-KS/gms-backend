package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ExportItemStatus;
import fpt.edu.vn.gms.common.enums.ExportStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.common.enums.StockLevelStatus;
import fpt.edu.vn.gms.dto.request.ExportItemRequest;
import fpt.edu.vn.gms.dto.response.StockExportDetailResponse;
import fpt.edu.vn.gms.dto.response.StockExportItemResponse;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.StockExportMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockExportServiceImplTest {

    @Mock
    StockExportRepository stockExportRepository;
    @Mock
    StockExportItemRepository stockExportItemRepository;
    @Mock
    StockExportItemHistoryRepository stockExportItemHistoryRepository;
    @Mock
    PriceQuotationRepository priceQuotationRepository;
    @Mock
    PartRepository partRepository;
    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    StockExportMapper stockExportMapper;

    @InjectMocks
    StockExportServiceImpl service;

    // Note: getExportingQuotations and getExportingQuotationById methods no longer exist
    // These tests have been removed as they are not in the current service interface

    @Test
    void exportItem_ShouldExportPartAndCreateExportItem() {
        Part part = Part.builder()
                .partId(1L)
                .quantityInStock(10.0)
                .reservedQuantity(5.0)
                .reorderLevel(2.0)
                .status(StockLevelStatus.IN_STOCK)
                .build();

        StockExport export = StockExport.builder()
                .id(1L)
                .code("XK001")
                .build();

        StockExportItem exportItem = StockExportItem.builder()
                .id(5L)
                .part(part)
                .quantity(10.0)
                .quantityExported(2.0)
                .status(ExportItemStatus.EXPORTING)
                .stockExport(export)
                .build();

        when(stockExportItemRepository.findById(5L)).thenReturn(Optional.of(exportItem));

        Employee receiver = Employee.builder()
                .employeeId(99L)
                .fullName("Receiver")
                .build();
        when(employeeRepository.findById(99L)).thenReturn(Optional.of(receiver));

        Employee exportedBy = Employee.builder()
                .employeeId(1L)
                .fullName("Exporter")
                .build();

        ExportItemRequest request = new ExportItemRequest();
        request.setQuantity(3.0);
        request.setReceiverId(99L);
        request.setNote("Test export");

        StockExportItemResponse response = StockExportItemResponse.builder().build();
        when(stockExportMapper.toItemDto(any(StockExportItem.class))).thenReturn(response);

        StockExportItemResponse result = service.exportItem(5L, request, exportedBy);

        assertSame(response, result);

        // check part quantities
        assertEquals(7.0, part.getQuantityInStock());
        assertEquals(2.0, part.getReservedQuantity());

        // exported quantity
        assertEquals(5.0, exportItem.getQuantityExported());
        assertEquals(ExportItemStatus.EXPORTING, exportItem.getStatus());

        verify(partRepository).save(part);
        verify(stockExportItemRepository).save(exportItem);
        verify(stockExportItemHistoryRepository).save(any(StockExportItemHistory.class));
    }

    @Test
    void exportItem_ShouldThrow_WhenItemNotFound() {
        when(stockExportItemRepository.findById(1L)).thenReturn(Optional.empty());
        
        ExportItemRequest request = new ExportItemRequest();
        request.setQuantity(1.0);
        request.setReceiverId(1L);
        
        Employee exportedBy = Employee.builder().employeeId(1L).build();
        
        assertThrows(ResourceNotFoundException.class,
                () -> service.exportItem(1L, request, exportedBy));
    }

    @Test
    void exportItem_ShouldThrow_WhenItemAlreadyFinished() {
        StockExportItem finishedItem = StockExportItem.builder()
                .id(2L)
                .status(ExportItemStatus.FINISHED)
                .build();
        when(stockExportItemRepository.findById(2L)).thenReturn(Optional.of(finishedItem));

        ExportItemRequest request = new ExportItemRequest();
        request.setQuantity(1.0);
        request.setReceiverId(1L);
        
        Employee exportedBy = Employee.builder().employeeId(1L).build();
        
        assertThrows(RuntimeException.class,
                () -> service.exportItem(2L, request, exportedBy));
    }

    @Test
    void exportItem_ShouldThrow_WhenQuantityExceedsRemaining() {
        StockExportItem item = StockExportItem.builder()
                .id(3L)
                .quantity(10.0)
                .quantityExported(8.0)
                .status(ExportItemStatus.EXPORTING)
                .build();
        when(stockExportItemRepository.findById(3L)).thenReturn(Optional.of(item));

        ExportItemRequest request = new ExportItemRequest();
        request.setQuantity(3.0); // 8.0 + 3.0 = 11.0 > 10.0
        request.setReceiverId(1L);
        
        Employee exportedBy = Employee.builder().employeeId(1L).build();
        
        assertThrows(IllegalArgumentException.class,
                () -> service.exportItem(3L, request, exportedBy));
    }

    @Test
    void createExportFromQuotation_ShouldCreateExport() {
        Employee creator = Employee.builder()
                .employeeId(1L)
                .fullName("Creator")
                .build();

        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(100L)
                .code("BG001")
                .items(List.of(
                        PriceQuotationItem.builder()
                                .priceQuotationItemId(1L)
                                .itemType(PriceQuotationItemType.PART)
                                .part(Part.builder().partId(10L).build())
                                .quantity(5.0)
                                .build()
                ))
                .build();

        when(priceQuotationRepository.findById(100L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(100L)).thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("XK")).thenReturn("XK001");

        StockExport savedExport = StockExport.builder()
                .id(100L)
                .code("XK001")
                .quotation(quotation)
                .build();
        when(stockExportRepository.save(any(StockExport.class))).thenReturn(savedExport);

        StockExportDetailResponse detailResponse = StockExportDetailResponse.builder()
                .id(100L)
                .code("XK001")
                .build();
        when(stockExportMapper.toDetailDto(any(StockExport.class))).thenReturn(detailResponse);
        when(stockExportMapper.toItemDto(any(StockExportItem.class))).thenReturn(StockExportItemResponse.builder().build());

        StockExportDetailResponse result = service.createExportFromQuotation(100L, "Test reason", creator);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("XK001", result.getCode());
        verify(stockExportRepository).save(any(StockExport.class));
    }

    @Test
    void createExportFromQuotation_ShouldThrow_WhenQuotationNotFound() {
        when(priceQuotationRepository.findById(100L)).thenReturn(Optional.empty());
        
        Employee creator = Employee.builder().employeeId(1L).build();
        
        assertThrows(ResourceNotFoundException.class,
                () -> service.createExportFromQuotation(100L, "Test", creator));
    }
}


