package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.*;
import fpt.edu.vn.gms.dto.request.PartUpdateReqDto;
import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PartMapper;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.PartService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseQuotationServiceImplTest {

    @Mock
    NotificationService notificationService;
    @Mock
    PartService partService;
    @Mock
    PartRepository partRepository;
    @Mock
    PriceQuotationRepository quotationRepository;
    @Mock
    PriceQuotationItemRepository priceQuotationItemRepo;
    @Mock
    PartCategoryRepository partCategoryRepo;
    @Mock
    MarketRepository marketRepo;
    @Mock
    UnitRepository unitRepo;
    @Mock
    VehicleModelRepository vehicleModelRepo;
    @Mock
    SupplierRepository supplierRepo;
    @Mock
    PriceQuotationItemMapper priceQuotationItemMapper;
    @Mock
    PriceQuotationMapper priceQuotationMapper;
    @Mock
    PartMapper partMapper;

    @InjectMocks
    WarehouseQuotationServiceImpl service;

    @Test
    void getPendingQuotations_ShouldFilterPartItemsWithInventoryUnknownOrOutOfStock() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("updatedAt").descending());

        PriceQuotationItemResponseDto partAvailable = PriceQuotationItemResponseDto.builder()
                .itemType(PriceQuotationItemType.PART)
                .inventoryStatus(PriceQuotationItemStatus.AVAILABLE)
                .build();
        PriceQuotationItemResponseDto partUnknown = PriceQuotationItemResponseDto.builder()
                .itemType(PriceQuotationItemType.PART)
                .inventoryStatus(PriceQuotationItemStatus.UNKNOWN)
                .build();
        PriceQuotationItemResponseDto partOutOfStock = PriceQuotationItemResponseDto.builder()
                .itemType(PriceQuotationItemType.PART)
                .inventoryStatus(PriceQuotationItemStatus.OUT_OF_STOCK)
                .build();
        PriceQuotationItemResponseDto serviceItem = PriceQuotationItemResponseDto.builder()
                .itemType(PriceQuotationItemType.SERVICE)
                .build();

        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .build();
        Page<PriceQuotation> page = new PageImpl<>(List.of(quotation), pageable, 1);
        when(quotationRepository.findAll(pageable)).thenReturn(page);

        PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder()
                .items(List.of(partAvailable, partUnknown, partOutOfStock, serviceItem))
                .build();
        when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

        Page<PriceQuotationResponseDto> result = service.getPendingQuotations(0, 5);

        assertEquals(1, result.getTotalElements());
        List<PriceQuotationItemResponseDto> filtered = result.getContent().get(0).getItems();
        // chỉ giữ PART + (UNKNOWN hoặc OUT_OF_STOCK)
        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(i ->
                i.getItemType() == PriceQuotationItemType.PART &&
                        (i.getInventoryStatus() == PriceQuotationItemStatus.UNKNOWN
                                || i.getInventoryStatus() == PriceQuotationItemStatus.OUT_OF_STOCK)
        ));

        verify(quotationRepository).findAll(pageable);
    }

    @Test
    void confirmItemDuringWarehouseReview_ShouldUpdateStatusesAndRecalculateTotals() {
        Part part = Part.builder()
                .partId(1L)
                .quantityInStock(10.0)
                .reservedQuantity(2.0)
                .build();

        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(10L)
                .estimateAmount(BigDecimal.ZERO)
                .serviceTicket(ServiceTicket.builder()
                        .serviceTicketId(5L)
                        .createdBy(Employee.builder().employeeId(99L).build())
                        .build())
                .build();

        PriceQuotationItem item = PriceQuotationItem.builder()
                .priceQuotationItemId(1L)
                .itemType(PriceQuotationItemType.PART)
                .part(part)
                .quantity(5.0)
                .warehouseReviewStatus(WarehouseReviewStatus.PENDING)
                .inventoryStatus(PriceQuotationItemStatus.UNKNOWN)
                .priceQuotation(quotation)
                .totalPrice(new BigDecimal("500000"))
                .build();

        quotation.setItems(List.of(item));

        when(priceQuotationItemRepo.findById(1L)).thenReturn(Optional.of(item));

        PriceQuotationItemResponseDto responseDto = PriceQuotationItemResponseDto.builder()
                .itemId(1L)
                .build();
        when(priceQuotationItemMapper.toResponseDto(item)).thenReturn(responseDto);

        PriceQuotationItemResponseDto result =
                service.confirmItemDuringWarehouseReview(1L, "Kho xác nhận đủ");

        assertSame(responseDto, result);
        assertEquals(WarehouseReviewStatus.CONFIRMED, item.getWarehouseReviewStatus());
        assertEquals(PriceQuotationItemStatus.AVAILABLE, item.getInventoryStatus());
        assertEquals("Kho xác nhận đủ", item.getWarehouseNote());

        verify(priceQuotationItemRepo).save(item);
        verify(quotationRepository, atLeastOnce()).save(quotation);
    }

    @Test
    void confirmItemDuringWarehouseReview_ShouldThrow_WhenItemNotFoundOrPartNull() {
        when(priceQuotationItemRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.confirmItemDuringWarehouseReview(1L, null));

        PriceQuotationItem noPart = PriceQuotationItem.builder()
                .priceQuotationItemId(2L)
                .part(null)
                .build();
        when(priceQuotationItemRepo.findById(2L)).thenReturn(Optional.of(noPart));
        assertThrows(IllegalStateException.class,
                () -> service.confirmItemDuringWarehouseReview(2L, null));
    }

    @Test
    void rejectItemDuringWarehouseReview_ShouldUpdateStatusAndNote() {
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(10L)
                .items(List.of())
                .serviceTicket(ServiceTicket.builder()
                        .serviceTicketId(5L)
                        .createdBy(Employee.builder().employeeId(1L).build())
                        .build())
                .build();

        PriceQuotationItem item = PriceQuotationItem.builder()
                .priceQuotationItemId(1L)
                .priceQuotation(quotation)
                .build();

        when(priceQuotationItemRepo.findById(1L)).thenReturn(Optional.of(item));

        PriceQuotationItemResponseDto dto = PriceQuotationItemResponseDto.builder().itemId(1L).build();
        when(priceQuotationItemMapper.toResponseDto(item)).thenReturn(dto);

        PriceQuotationItemResponseDto result =
                service.rejectItemDuringWarehouseReview(1L, "Không đủ hàng");

        assertSame(dto, result);
        assertEquals(WarehouseReviewStatus.REJECTED, item.getWarehouseReviewStatus());
        assertEquals("Không đủ hàng", item.getWarehouseNote());

        verify(priceQuotationItemRepo).save(item);
    }

    @Test
    void updatePartDuringWarehouseReview_ShouldUpdatePartAndItem() {
        Part part = Part.builder()
                .partId(1L)
                .quantity(10.0)
                .reservedQuantity(2.0)
                .build();

        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(10L)
                .build();

        PriceQuotationItem item = PriceQuotationItem.builder()
                .priceQuotationItemId(1L)
                .part(part)
                .quantity(5.0)
                .priceQuotation(quotation)
                .build();

        when(priceQuotationItemRepo.findById(1L)).thenReturn(Optional.of(item));

        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .name("New Part")
                .purchasePrice(new BigDecimal("100000"))
                .sellingPrice(new BigDecimal("150000"))
                .note("Kho cập nhật")
                .build();

        PartReqDto savedPartDto = PartReqDto.builder()
                .partId(1L)
                .name("New Part")
                .sellingPrice(new BigDecimal("150000"))
                .quantity(10.0)
                .reservedQuantity(2.0)
                .build();

        when(partService.updatePart(part.getPartId(), dto)).thenReturn(savedPartDto);
        when(partRepository.findById(1L)).thenReturn(Optional.of(part));

        service.updatePartDuringWarehouseReview(1L, dto);

        assertEquals("New Part", item.getItemName());
        assertEquals(new BigDecimal("150000"), item.getUnitPrice());
        assertEquals(new BigDecimal("150000").multiply(BigDecimal.valueOf(5.0)), item.getTotalPrice());
        assertEquals(WarehouseReviewStatus.CONFIRMED, item.getWarehouseReviewStatus());
        assertEquals(PriceQuotationItemStatus.AVAILABLE, item.getInventoryStatus());

        verify(priceQuotationItemRepo).save(item);
        verify(quotationRepository, atLeastOnce()).save(quotation);
    }

    @Test
    void updatePartDuringWarehouseReview_ShouldThrow_WhenItemOrPartMissing() {
        when(priceQuotationItemRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.updatePartDuringWarehouseReview(1L, PartUpdateReqDto.builder().build()));

        PriceQuotationItem noPart = PriceQuotationItem.builder()
                .priceQuotationItemId(2L)
                .part(null)
                .build();
        when(priceQuotationItemRepo.findById(2L)).thenReturn(Optional.of(noPart));
        assertThrows(IllegalStateException.class,
                () -> service.updatePartDuringWarehouseReview(2L, PartUpdateReqDto.builder().build()));
    }

    @Test
    void createPartDuringWarehouseReview_ShouldCreateNewPartAndMergeIntoItem() {
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(10L)
                .estimateAmount(BigDecimal.ZERO)
                .serviceTicket(ServiceTicket.builder()
                        .serviceTicketId(5L)
                        .createdBy(Employee.builder().employeeId(1L).build())
                        .build())
                .build();

        Unit unit = Unit.builder().name("Cái").build();

        PriceQuotationItem item = PriceQuotationItem.builder()
                .priceQuotationItemId(1L)
                .priceQuotation(quotation)
                .quantity(3.0)
                .warehouseReviewStatus(WarehouseReviewStatus.PENDING)
                .inventoryStatus(PriceQuotationItemStatus.UNKNOWN)
                .build();

        when(priceQuotationItemRepo.findById(1L)).thenReturn(Optional.of(item));

        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .name("New Part")
                .note("Kho tạo mới")
                .build();

        PartReqDto savedPartDto = PartReqDto.builder()
                .partId(100L)
                .name("New Part")
                .sellingPrice(new BigDecimal("200000"))
                .build();

        when(partService.createPart(dto)).thenReturn(savedPartDto);

        Part part = Part.builder()
                .partId(100L)
                .unit(unit)
                .build();
        when(partRepository.findById(100L)).thenReturn(Optional.of(part));

        PriceQuotationItemResponseDto responseDto = PriceQuotationItemResponseDto.builder()
                .itemId(1L)
                .build();
        when(priceQuotationItemMapper.toResponseDto(item)).thenReturn(responseDto);

        PriceQuotationItemResponseDto result =
                service.createPartDuringWarehouseReview(1L, dto);

        assertSame(responseDto, result);
        assertEquals(part, item.getPart());
        assertEquals("New Part", item.getItemName());
        assertEquals(unit.getName(), item.getUnit());
        assertEquals(new BigDecimal("200000"), item.getUnitPrice());
        assertEquals(new BigDecimal("200000").multiply(BigDecimal.valueOf(3.0)), item.getTotalPrice());
        assertEquals(WarehouseReviewStatus.CONFIRMED, item.getWarehouseReviewStatus());
        assertEquals(PriceQuotationItemStatus.OUT_OF_STOCK, item.getInventoryStatus());

        verify(priceQuotationItemRepo).save(item);
        verify(quotationRepository, atLeastOnce()).save(quotation);
    }

    @Test
    void createPartDuringWarehouseReview_ShouldThrow_WhenItemNotFoundOrAlreadyHasPart() {
        when(priceQuotationItemRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.createPartDuringWarehouseReview(1L, PartUpdateReqDto.builder().build()));

        PriceQuotationItem hasPart = PriceQuotationItem.builder()
                .priceQuotationItemId(2L)
                .part(Part.builder().partId(1L).build())
                .build();
        when(priceQuotationItemRepo.findById(2L)).thenReturn(Optional.of(hasPart));
        assertThrows(IllegalStateException.class,
                () -> service.createPartDuringWarehouseReview(2L, PartUpdateReqDto.builder().build()));
    }
}


