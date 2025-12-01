package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.DeductionType;
import fpt.edu.vn.gms.common.enums.ExportStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.dto.PartItemDto;
import fpt.edu.vn.gms.dto.request.StockExportCreateDto;
import fpt.edu.vn.gms.dto.response.StockExportItemResponse;
import fpt.edu.vn.gms.dto.response.StockExportResponse;
import fpt.edu.vn.gms.dto.response.StockExportResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
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
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockExportServiceImplTest {

    @Mock
    PriceQuotationRepository quotationRepository;
    @Mock
    PriceQuotationItemRepository itemRepository;
    @Mock
    PartRepository partRepository;
    @Mock
    StockExportRepository exportRepository;
    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    StockExportItemRepository stockExportItemRepository;
    @Mock
    DeductionRepository deductionRepository;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    PriceQuotationMapper priceQuotationMapper;
    @Mock
    PriceQuotationItemMapper itemMapper;

    @InjectMocks
    StockExportServiceImpl service;

    @Test
    void getExportingQuotations_ShouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("updatedAt").descending());
        PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(1L).build();
        Page<PriceQuotation> page = new PageImpl<>(List.of(quotation), pageable, 1);

        when(quotationRepository.findByExportStatus(ExportStatus.WAITING_TO_EXPORT, pageable))
                .thenReturn(page);

        StockExportResponse response = new StockExportResponse();
        when(priceQuotationMapper.toStockExportResponse(quotation)).thenReturn(response);

        Page<StockExportResponse> result = service.getExportingQuotations(0, 5);

        assertEquals(1, result.getTotalElements());
        assertSame(response, result.getContent().get(0));
        verify(quotationRepository).findByExportStatus(ExportStatus.WAITING_TO_EXPORT, pageable);
    }

    @Test
    void getExportingQuotationById_ShouldFilterPartItemsAndMap() {
        PriceQuotationItem partItem = PriceQuotationItem.builder()
                .priceQuotationItemId(1L)
                .itemType(PriceQuotationItemType.PART)
                .build();
        PriceQuotationItem otherItem = PriceQuotationItem.builder()
                .priceQuotationItemId(2L)
                .itemType(PriceQuotationItemType.SERVICE)
                .build();
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(10L)
                .items(List.of(partItem, otherItem))
                .build();

        when(quotationRepository.findById(10L)).thenReturn(Optional.of(quotation));

        StockExportItemResponse itemResponse = new StockExportItemResponse();
        when(itemMapper.toStockExportItemResponse(partItem)).thenReturn(itemResponse);

        List<StockExportItemResponse> result = service.getExportingQuotationById(10L);

        assertEquals(1, result.size());
        assertSame(itemResponse, result.get(0));
        verify(quotationRepository).findById(10L);
    }

    @Test
    void getExportingQuotationById_ShouldThrow_WhenQuotationNotFound() {
        when(quotationRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getExportingQuotationById(10L));
    }

    @Test
    void exportItem_ShouldExportPartAndCreateExportItem() {
        Part part = Part.builder()
                .partId(1L)
                .quantityInStock(10.0)
                .reservedQuantity(5.0)
                .unit(Unit.builder().name("Cái").build())
                .build();

        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(100L)
                .build();

        PriceQuotationItem item = PriceQuotationItem.builder()
                .priceQuotationItemId(5L)
                .itemType(PriceQuotationItemType.PART)
                .exportStatus(ExportStatus.WAITING_TO_EXPORT)
                .part(part)
                .quantity(10.0)
                .exportedQuantity(2.0)
                .priceQuotation(quotation)
                .unit("Cái")
                .build();

        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));

        StockExport export = StockExport.builder()
                .id(1L)
                .quotation(quotation)
                .code("XK001")
                .createdAt(LocalDateTime.now())
                .build();
        when(exportRepository.findByQuotationId(quotation.getPriceQuotationId()))
                .thenReturn(Optional.of(export));

        Employee receiver = Employee.builder()
                .employeeId(99L)
                .fullName("Receiver")
                .build();
        when(employeeRepository.findById(99L)).thenReturn(Optional.of(receiver));

        StockExportItemResponse response = new StockExportItemResponse();
        when(itemMapper.toStockExportItemResponse(item)).thenReturn(response);

        StockExportItemResponse result = service.exportItem(5L, 3.0, 99L);

        assertSame(response, result);

        // check part quantities
        assertEquals(7.0, part.getQuantityInStock());
        assertEquals(2.0, part.getReservedQuantity());

        // exported quantity
        assertEquals(5.0, item.getExportedQuantity());
        assertEquals(ExportStatus.EXPORTED, item.getExportStatus());

        verify(partRepository).save(part);
        verify(itemRepository).save(item);
        verify(stockExportItemRepository).save(any(StockExportItem.class));
    }

    @Test
    void exportItem_ShouldThrow_WhenItemNotFoundOrNotPartOrStatusInvalid() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.exportItem(1L, 1.0, 1L));

        PriceQuotationItem notPart = PriceQuotationItem.builder()
                .priceQuotationItemId(2L)
                .itemType(PriceQuotationItemType.SERVICE)
                .build();
        when(itemRepository.findById(2L)).thenReturn(Optional.of(notPart));
        assertThrows(RuntimeException.class,
                () -> service.exportItem(2L, 1.0, 1L));

        PriceQuotationItem wrongStatus = PriceQuotationItem.builder()
                .priceQuotationItemId(3L)
                .itemType(PriceQuotationItemType.PART)
                .exportStatus(ExportStatus.EXPORTED)
                .build();
        when(itemRepository.findById(3L)).thenReturn(Optional.of(wrongStatus));
        assertThrows(RuntimeException.class,
                () -> service.exportItem(3L, 1.0, 1L));
    }

    @Test
    void exportItem_ShouldThrow_WhenPartNullOrExportQtyExceeds() {
        PriceQuotationItem noPart = PriceQuotationItem.builder()
                .priceQuotationItemId(4L)
                .itemType(PriceQuotationItemType.PART)
                .exportStatus(ExportStatus.WAITING_TO_EXPORT)
                .quantity(5.0)
                .exportedQuantity(0.0)
                .build();
        when(itemRepository.findById(4L)).thenReturn(Optional.of(noPart));

        assertThrows(RuntimeException.class,
                () -> service.exportItem(4L, 1.0, 1L));

        Part part = Part.builder()
                .partId(1L)
                .quantityInStock(10.0)
                .reservedQuantity(5.0)
                .build();
        PriceQuotationItem overExport = PriceQuotationItem.builder()
                .priceQuotationItemId(5L)
                .itemType(PriceQuotationItemType.PART)
                .exportStatus(ExportStatus.WAITING_TO_EXPORT)
                .quantity(5.0)
                .exportedQuantity(4.0)
                .part(part)
                .build();
        when(itemRepository.findById(5L)).thenReturn(Optional.of(overExport));

        assertThrows(RuntimeException.class,
                () -> service.exportItem(5L, 2.0, 1L));
    }

    @Test
    void createExport_ShouldCreateExportAndDeduction_WhenDamageByEmployee() {
        Employee creator = Employee.builder()
                .employeeId(1L)
                .fullName("Creator")
                .build();
        Employee receiver = Employee.builder()
                .employeeId(2L)
                .fullName("Receiver")
                .build();
        Employee damagedBy = Employee.builder()
                .employeeId(3L)
                .fullName("Damager")
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(damagedBy));

        Part part1 = Part.builder()
                .partId(10L)
                .name("Part1")
                .quantityInStock(10.0)
                .unit(Unit.builder().name("Cái").build())
                .sellingPrice(new BigDecimal("100000"))
                .build();
        Part part2 = Part.builder()
                .partId(11L)
                .name("Part2")
                .quantityInStock(20.0)
                .unit(Unit.builder().name("Cái").build())
                .sellingPrice(new BigDecimal("200000"))
                .build();

        when(partRepository.findById(10L)).thenReturn(Optional.of(part1));
        when(partRepository.findById(11L)).thenReturn(Optional.of(part2));

        when(codeSequenceService.generateCode("EXP")).thenReturn("EXP001");

        StockExport export = StockExport.builder()
                .id(100L)
                .code("EXP001")
                .createdAt(LocalDateTime.now())
                .build();
        when(exportRepository.save(any(StockExport.class))).thenReturn(export);

        PartItemDto item1 = PartItemDto.builder()
                .partId(10L)
                .quantity(2.0)
                .build();
        PartItemDto item2 = PartItemDto.builder()
                .partId(11L)
                .quantity(1.0)
                .build();

        StockExportCreateDto dto = StockExportCreateDto.builder()
                .createdById(1L)
                .receiverId(2L)
                .damagedById(3L)
                .reason("Hỏng do nhân viên")
                .note("Lỗi dùng sai")
                .items(List.of(item1, item2))
                .build();

        StockExportResponseDto result = service.createExport(dto);

        assertNotNull(result);
        assertEquals(export.getId(), result.getId());
        assertEquals(export.getCode(), result.getCode());

        // quantity decreased
        assertEquals(8.0, part1.getQuantityInStock());
        assertEquals(19.0, part2.getQuantityInStock());

        // deduction created with correct amount
        verify(deductionRepository).save(argThat(d -> {
            BigDecimal expected = part1.getSellingPrice().multiply(BigDecimal.valueOf(2.0))
                    .add(part2.getSellingPrice().multiply(BigDecimal.valueOf(1.0)));
            assertEquals(DeductionType.DAMAGE, d.getType());
            assertEquals(expected, d.getAmount());
            assertEquals(damagedBy, d.getEmployee());
            assertEquals("Lỗi dùng sai", d.getReason());
            assertEquals(creator.getFullName(), d.getCreatedBy());
            assertEquals(LocalDate.now(), d.getDate());
            return true;
        }));

        verify(stockExportItemRepository, times(2)).save(any(StockExportItem.class));
    }

    @Test
    void createExport_ShouldThrow_WhenCreatorOrReceiverNotFound() {
        StockExportCreateDto dto = StockExportCreateDto.builder()
                .createdById(1L)
                .receiverId(2L)
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.createExport(dto));
    }
}


