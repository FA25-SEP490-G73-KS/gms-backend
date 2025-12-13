package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ExportItemStatus;
import fpt.edu.vn.gms.common.enums.ExportStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.dto.response.StockExportDetailResponse;
import fpt.edu.vn.gms.dto.response.StockExportItemResponse;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.StockExportMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for StockExportServiceImpl
 * Matrix: SE-001
 * Total: 12 test cases (0 EXISTING, 12 NEW)
 */
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

    private PriceQuotation quotation;
    private Employee employee;
    private Part part1;
    private Part part2;
    private PriceQuotationItem item1;
    private PriceQuotationItem item2;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .employeeId(1L)
                .fullName("Employee 1")
                .build();

        part1 = Part.builder()
                .partId(1L)
                .name("Part 1")
                .quantityInStock(100.0)
                .reservedQuantity(10.0)
                .build();

        part2 = Part.builder()
                .partId(2L)
                .name("Part 2")
                .quantityInStock(50.0)
                .reservedQuantity(5.0)
                .build();

        item1 = PriceQuotationItem.builder()
                .priceQuotationItemId(1L)
                .itemType(PriceQuotationItemType.PART)
                .inventoryStatus(PriceQuotationItemStatus.AVAILABLE)
                .quantity(10.0)
                .part(part1)
                .build();

        item2 = PriceQuotationItem.builder()
                .priceQuotationItemId(2L)
                .itemType(PriceQuotationItemType.PART)
                .inventoryStatus(PriceQuotationItemStatus.OUT_OF_STOCK)
                .quantity(5.0)
                .part(part2)
                .build();

        quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .code("BG-000001")
                .items(new ArrayList<>(List.of(item1, item2)))
                .build();
    }

    // ========== MATRIX 7: createExportFromQuotation (UTCID82-UTCID93) ==========

    @Test
    void UTCID82_createExportFromQuotation_ShouldCreateExport_WhenValidQuotation() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(1L)).thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("XK")).thenReturn("XK-000001");
        when(stockExportRepository.save(any(StockExport.class))).thenAnswer(invocation -> {
            StockExport export = invocation.getArgument(0);
            export.setId(1L);
            return export;
        });

        StockExportDetailResponse detailResponse = StockExportDetailResponse.builder()
                .id(1L)
                .code("XK-000001")
                .build();
        when(stockExportMapper.toDetailDto(any(StockExport.class))).thenReturn(detailResponse);
        when(stockExportMapper.toItemDto(any(StockExportItem.class))).thenReturn(StockExportItemResponse.builder().build());

        // When
        StockExportDetailResponse result = service.createExportFromQuotation(1L, "Test reason", employee);

        // Then
        assertNotNull(result);
        verify(stockExportRepository).save(argThat(export -> 
            export.getCode().equals("XK-000001") &&
            export.getStatus() == ExportStatus.WAITING_TO_EXECUTE &&
            export.getExportItems().size() == 2
        ));
    }

    @Test
    void UTCID83_createExportFromQuotation_ShouldThrowException_WhenQuotationNotFound() {
        // Given
        when(priceQuotationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, 
                () -> service.createExportFromQuotation(999L, "Reason", employee));
    }

    @Test
    void UTCID84_createExportFromQuotation_ShouldThrowException_WhenExportAlreadyExists() {
        // Given
        StockExport existingExport = StockExport.builder()
                .id(1L)
                .code("XK-000001")
                .build();

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(1L)).thenReturn(Optional.of(existingExport));

        // When & Then
        assertThrows(RuntimeException.class, 
                () -> service.createExportFromQuotation(1L, "Reason", employee));
    }

    @Test
    void UTCID85_createExportFromQuotation_ShouldSetExportingStatus_WhenPartIsAvailable() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(1L)).thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("XK")).thenReturn("XK-000001");
        when(stockExportRepository.save(any(StockExport.class))).thenAnswer(invocation -> {
            StockExport export = invocation.getArgument(0);
            export.setId(1L);
            return export;
        });

        StockExportDetailResponse detailResponse = StockExportDetailResponse.builder().build();
        when(stockExportMapper.toDetailDto(any(StockExport.class))).thenReturn(detailResponse);
        when(stockExportMapper.toItemDto(any(StockExportItem.class))).thenReturn(StockExportItemResponse.builder().build());

        // When
        service.createExportFromQuotation(1L, "Reason", employee);

        // Then
        verify(stockExportRepository).save(argThat(export -> 
            export.getExportItems().stream()
                    .anyMatch(item -> item.getStatus() == ExportItemStatus.EXPORTING &&
                            item.getQuotationItem().getInventoryStatus() == PriceQuotationItemStatus.AVAILABLE)
        ));
    }

    @Test
    void UTCID86_createExportFromQuotation_ShouldSetWaitingToReceipt_WhenPartIsOutOfStock() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(1L)).thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("XK")).thenReturn("XK-000001");
        when(stockExportRepository.save(any(StockExport.class))).thenAnswer(invocation -> {
            StockExport export = invocation.getArgument(0);
            export.setId(1L);
            return export;
        });

        StockExportDetailResponse detailResponse = StockExportDetailResponse.builder().build();
        when(stockExportMapper.toDetailDto(any(StockExport.class))).thenReturn(detailResponse);
        when(stockExportMapper.toItemDto(any(StockExportItem.class))).thenReturn(StockExportItemResponse.builder().build());

        // When
        service.createExportFromQuotation(1L, "Reason", employee);

        // Then
        verify(stockExportRepository).save(argThat(export -> 
            export.getExportItems().stream()
                    .anyMatch(item -> item.getStatus() == ExportItemStatus.WAITING_TO_RECEIPT &&
                            item.getQuotationItem().getInventoryStatus() == PriceQuotationItemStatus.OUT_OF_STOCK)
        ));
    }

    @Test
    void UTCID87_createExportFromQuotation_ShouldSetZeroQuantityExported() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(1L)).thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("XK")).thenReturn("XK-000001");
        when(stockExportRepository.save(any(StockExport.class))).thenAnswer(invocation -> {
            StockExport export = invocation.getArgument(0);
            export.setId(1L);
            return export;
        });

        StockExportDetailResponse detailResponse = StockExportDetailResponse.builder().build();
        when(stockExportMapper.toDetailDto(any(StockExport.class))).thenReturn(detailResponse);
        when(stockExportMapper.toItemDto(any(StockExportItem.class))).thenReturn(StockExportItemResponse.builder().build());

        // When
        service.createExportFromQuotation(1L, "Reason", employee);

        // Then
        verify(stockExportRepository).save(argThat(export -> 
            export.getExportItems().stream()
                    .allMatch(item -> item.getQuantityExported() == 0.0)
        ));
    }

    @Test
    void UTCID88_createExportFromQuotation_ShouldGenerateExportCode() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(1L)).thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("XK")).thenReturn("XK-000001");
        when(stockExportRepository.save(any(StockExport.class))).thenAnswer(invocation -> {
            StockExport export = invocation.getArgument(0);
            export.setId(1L);
            return export;
        });

        StockExportDetailResponse detailResponse = StockExportDetailResponse.builder().build();
        when(stockExportMapper.toDetailDto(any(StockExport.class))).thenReturn(detailResponse);
        when(stockExportMapper.toItemDto(any(StockExportItem.class))).thenReturn(StockExportItemResponse.builder().build());

        // When
        service.createExportFromQuotation(1L, "Reason", employee);

        // Then
        verify(codeSequenceService).generateCode("XK");
        verify(stockExportRepository).save(argThat(export -> 
            export.getCode().equals("XK-000001")
        ));
    }

    @Test
    void UTCID89_createExportFromQuotation_ShouldAssignCreator() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(1L)).thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("XK")).thenReturn("XK-000001");
        when(stockExportRepository.save(any(StockExport.class))).thenAnswer(invocation -> {
            StockExport export = invocation.getArgument(0);
            export.setId(1L);
            return export;
        });

        StockExportDetailResponse detailResponse = StockExportDetailResponse.builder().build();
        when(stockExportMapper.toDetailDto(any(StockExport.class))).thenReturn(detailResponse);
        when(stockExportMapper.toItemDto(any(StockExportItem.class))).thenReturn(StockExportItemResponse.builder().build());

        // When
        service.createExportFromQuotation(1L, "Reason", employee);

        // Then
        verify(stockExportRepository).save(argThat(export -> 
            export.getCreatedBy().equals("Employee 1")
        ));
    }

    @Test
    void UTCID90_createExportFromQuotation_ShouldHandleNullCreator() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(1L)).thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("XK")).thenReturn("XK-000001");
        when(stockExportRepository.save(any(StockExport.class))).thenAnswer(invocation -> {
            StockExport export = invocation.getArgument(0);
            export.setId(1L);
            return export;
        });

        StockExportDetailResponse detailResponse = StockExportDetailResponse.builder().build();
        when(stockExportMapper.toDetailDto(any(StockExport.class))).thenReturn(detailResponse);
        when(stockExportMapper.toItemDto(any(StockExportItem.class))).thenReturn(StockExportItemResponse.builder().build());

        // When
        service.createExportFromQuotation(1L, "Reason", null);

        // Then
        verify(stockExportRepository).save(argThat(export -> 
            export.getCreatedBy() == null
        ));
    }

    @Test
    void UTCID91_createExportFromQuotation_ShouldHandleMultipleParts() {
        // Given
        PriceQuotationItem item3 = PriceQuotationItem.builder()
                .priceQuotationItemId(3L)
                .itemType(PriceQuotationItemType.PART)
                .inventoryStatus(PriceQuotationItemStatus.AVAILABLE)
                .quantity(20.0)
                .part(part1)
                .build();
        quotation.getItems().add(item3);

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(1L)).thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("XK")).thenReturn("XK-000001");
        when(stockExportRepository.save(any(StockExport.class))).thenAnswer(invocation -> {
            StockExport export = invocation.getArgument(0);
            export.setId(1L);
            return export;
        });

        StockExportDetailResponse detailResponse = StockExportDetailResponse.builder().build();
        when(stockExportMapper.toDetailDto(any(StockExport.class))).thenReturn(detailResponse);
        when(stockExportMapper.toItemDto(any(StockExportItem.class))).thenReturn(StockExportItemResponse.builder().build());

        // When
        service.createExportFromQuotation(1L, "Reason", employee);

        // Then
        verify(stockExportRepository).save(argThat(export -> 
            export.getExportItems().size() == 3
        ));
    }

    @Test
    void UTCID92_createExportFromQuotation_ShouldRollbackOnError() {
        // Given
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(1L)).thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("XK")).thenReturn("XK-000001");
        when(stockExportRepository.save(any(StockExport.class)))
                .thenThrow(new DataAccessException("Database error") {});

        // When & Then
        assertThrows(DataAccessException.class, 
                () -> service.createExportFromQuotation(1L, "Reason", employee));
    }

    @Test
    void UTCID93_createExportFromQuotation_ShouldHandleZeroQuantity() {
        // Given
        PriceQuotationItem zeroQtyItem = PriceQuotationItem.builder()
                .priceQuotationItemId(3L)
                .itemType(PriceQuotationItemType.PART)
                .inventoryStatus(PriceQuotationItemStatus.AVAILABLE)
                .quantity(0.0)
                .part(part1)
                .build();
        quotation.getItems().clear();
        quotation.getItems().add(zeroQtyItem);

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(stockExportRepository.findByQuotationId(1L)).thenReturn(Optional.empty());
        when(codeSequenceService.generateCode("XK")).thenReturn("XK-000001");
        when(stockExportRepository.save(any(StockExport.class))).thenAnswer(invocation -> {
            StockExport export = invocation.getArgument(0);
            export.setId(1L);
            return export;
        });

        StockExportDetailResponse detailResponse = StockExportDetailResponse.builder().build();
        when(stockExportMapper.toDetailDto(any(StockExport.class))).thenReturn(detailResponse);
        when(stockExportMapper.toItemDto(any(StockExportItem.class))).thenReturn(StockExportItemResponse.builder().build());

        // When & Then
        assertDoesNotThrow(() -> service.createExportFromQuotation(1L, "Reason", employee));
    }
}

