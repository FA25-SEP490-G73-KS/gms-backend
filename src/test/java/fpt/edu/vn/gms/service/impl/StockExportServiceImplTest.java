package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.ExportStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.response.StockExportItemResponse;
import fpt.edu.vn.gms.dto.response.StockExportResponse;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class StockExportServiceImplTest extends BaseServiceTest {

  @Mock
  private PriceQuotationRepository quotationRepository;
  @Mock
  private PriceQuotationItemRepository itemRepository;
  @Mock
  private PartRepository partRepository;
  @Mock
  private StockExportRepository exportRepository;
  @Mock
  private EmployeeRepository employeeRepository;
  @Mock
  private StockExportItemRepository stockExportItemRepository;
  @Mock
  private CodeSequenceService codeSequenceService;
  @Mock
  private PriceQuotationMapper priceQuotationMapper;
  @Mock
  private PriceQuotationItemMapper itemMapper;

  @InjectMocks
  private StockExportServiceImpl stockExportServiceImpl;

  @Test
  void getExportingQuotations_WhenQuotationsExist_ShouldReturnPagedResponses() {
    PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(1L).build();
    StockExportResponse response = StockExportResponse.builder().priceQuotationId(1L).build();
    Page<PriceQuotation> page = new PageImpl<>(List.of(quotation));
    when(quotationRepository.findByExportStatus(eq(ExportStatus.WAITING_TO_EXPORT), any(Pageable.class)))
        .thenReturn(page);
    when(priceQuotationMapper.toStockExportResponse(quotation)).thenReturn(response);

    Page<StockExportResponse> result = stockExportServiceImpl.getExportingQuotations(0, 10);

    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getPriceQuotationId());
    verify(quotationRepository).findByExportStatus(eq(ExportStatus.WAITING_TO_EXPORT), any(Pageable.class));
  }

  @Test
  void getExportingQuotations_WhenNoQuotationsExist_ShouldReturnEmptyPage() {
    Page<PriceQuotation> page = new PageImpl<>(List.of());
    when(quotationRepository.findByExportStatus(eq(ExportStatus.WAITING_TO_EXPORT), any(Pageable.class)))
        .thenReturn(page);

    Page<StockExportResponse> result = stockExportServiceImpl.getExportingQuotations(0, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void getExportingQuotationById_WhenQuotationExists_ShouldReturnPartItems() {
    PriceQuotationItem item1 = PriceQuotationItem.builder().priceQuotationItemId(1L)
        .itemType(PriceQuotationItemType.PART).build();
    PriceQuotationItem item2 = PriceQuotationItem.builder().priceQuotationItemId(2L)
        .itemType(PriceQuotationItemType.SERVICE).build();
    PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(1L).items(List.of(item1, item2)).build();
    StockExportItemResponse resp1 = StockExportItemResponse.builder().itemId(1L).build();

    when(quotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    when(itemMapper.toStockExportItemResponse(item1)).thenReturn(resp1);

    List<StockExportItemResponse> result = stockExportServiceImpl.getExportingQuotationById(1L);

    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).getItemId());
    verify(quotationRepository).findById(1L);
  }

  @Test
  void getExportingQuotationById_WhenQuotationNotFound_ShouldThrowResourceNotFoundException() {
    when(quotationRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> stockExportServiceImpl.getExportingQuotationById(99L));
  }

  @Test
  void exportItem_WhenValidPartItem_ShouldUpdateExportedAndReturnResponse() {
    Part part = Part.builder().partId(1L).quantityInStock(10.0).reservedQuantity(5.0).build();
    PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(1L).build();
    PriceQuotationItem item = PriceQuotationItem.builder()
        .priceQuotationItemId(2L)
        .itemType(PriceQuotationItemType.PART)
        .quantity(5.0)
        .exportedQuantity(2.0)
        .part(part)
        .unit("Cái")
        .priceQuotation(quotation)
        .build();
    Employee receiver = getMockEmployee(Role.SERVICE_ADVISOR);
    StockExport export = StockExport.builder().id(3L).quotation(quotation).build();
    StockExportItemResponse response = StockExportItemResponse.builder().itemId(2L).build();

    when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
    when(partRepository.save(any(Part.class))).thenReturn(part);
    when(itemRepository.save(any(PriceQuotationItem.class))).thenReturn(item);
    when(exportRepository.findByQuotationId(1L)).thenReturn(Optional.of(export));
    when(employeeRepository.findById(receiver.getEmployeeId())).thenReturn(Optional.of(receiver));
    when(stockExportItemRepository.save(any(StockExportItem.class))).thenReturn(StockExportItem.builder().build());
    when(itemMapper.toStockExportItemResponse(item)).thenReturn(response);

    StockExportItemResponse result = stockExportServiceImpl.exportItem(2L, 2.0, receiver.getEmployeeId());

    assertNotNull(result);
    assertEquals(2L, result.getItemId());
    assertEquals(8.0, part.getQuantityInStock());
    assertEquals(3.0, part.getReservedQuantity());
    assertEquals(4.0, item.getExportedQuantity());
    verify(partRepository).save(part);
    verify(itemRepository).save(item);
    verify(stockExportItemRepository).save(any(StockExportItem.class));
  }

  @Test
  void exportItem_WhenItemNotFound_ShouldThrowResourceNotFoundException() {
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> stockExportServiceImpl.exportItem(99L, 1.0, 1L));
  }

  @Test
  void exportItem_WhenItemTypeNotPart_ShouldThrowRuntimeException() {
    PriceQuotationItem item = PriceQuotationItem.builder()
        .priceQuotationItemId(2L)
        .itemType(PriceQuotationItemType.SERVICE)
        .build();
    when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
    assertThrows(RuntimeException.class, () -> stockExportServiceImpl.exportItem(2L, 1.0, 1L));
  }

  @Test
  void exportItem_WhenPartIsNull_ShouldThrowRuntimeException() {
    PriceQuotationItem item = PriceQuotationItem.builder()
        .priceQuotationItemId(2L)
        .itemType(PriceQuotationItemType.PART)
        .part(null)
        .build();
    when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
    assertThrows(RuntimeException.class, () -> stockExportServiceImpl.exportItem(2L, 1.0, 1L));
  }

  @Test
  void exportItem_WhenExportQtyExceedsQuantity_ShouldThrowRuntimeException() {
    Part part = Part.builder().partId(1L).quantityInStock(10.0).reservedQuantity(5.0).build();
    PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(1L).build();
    PriceQuotationItem item = PriceQuotationItem.builder()
        .priceQuotationItemId(2L)
        .itemType(PriceQuotationItemType.PART)
        .quantity(5.0)
        .exportedQuantity(4.0)
        .part(part)
        .priceQuotation(quotation)
        .build();
    when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
    assertThrows(RuntimeException.class, () -> stockExportServiceImpl.exportItem(2L, 2.0, 1L));
  }

  @Test
  void exportItem_WhenExportIsFull_ShouldSetExportStatusExported() {
    // Arrange
    Part part = Part.builder().partId(1L).quantityInStock(10.0).reservedQuantity(5.0).build();
    PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(1L).build();
    PriceQuotationItem item = PriceQuotationItem.builder()
        .priceQuotationItemId(2L)
        .itemType(PriceQuotationItemType.PART)
        .quantity(5.0)
        .exportedQuantity(4.0)
        .part(part)
        .unit("Cái")
        .priceQuotation(quotation)
        .build();
    Employee receiver = getMockEmployee(Role.SERVICE_ADVISOR);
    StockExport export = StockExport.builder().id(3L).quotation(quotation).build();
    // Response mock sẽ trả về status đúng với logic service
    StockExportItemResponse response = StockExportItemResponse.builder().itemId(2L).exportStatus(ExportStatus.EXPORTED)
        .build();

    when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
    when(partRepository.save(any(Part.class))).thenReturn(part);
    when(itemRepository.save(any(PriceQuotationItem.class))).thenReturn(item);
    when(exportRepository.findByQuotationId(1L)).thenReturn(Optional.of(export));
    when(employeeRepository.findById(receiver.getEmployeeId())).thenReturn(Optional.of(receiver));
    when(stockExportItemRepository.save(any(StockExportItem.class))).thenReturn(StockExportItem.builder().build());
    when(itemMapper.toStockExportItemResponse(item)).thenReturn(response);

    // Act
    StockExportItemResponse result = stockExportServiceImpl.exportItem(2L, 1.0, receiver.getEmployeeId());

    // Assert
    assertEquals(ExportStatus.EXPORTED, result.getExportStatus());
    assertEquals(ExportStatus.EXPORTED, item.getExportStatus());
  }

  @Test
  void exportItem_WhenExportIsPartial_ShouldSetExportStatusWaitingToExport() {
    // Arrange
    Part part = Part.builder().partId(1L).quantityInStock(10.0).reservedQuantity(5.0).build();
    PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(1L).build();
    PriceQuotationItem item = PriceQuotationItem.builder()
        .priceQuotationItemId(2L)
        .itemType(PriceQuotationItemType.PART)
        .quantity(5.0)
        .exportedQuantity(2.0)
        .part(part)
        .unit("Cái")
        .priceQuotation(quotation)
        .build();
    Employee receiver = getMockEmployee(Role.SERVICE_ADVISOR);
    StockExport export = StockExport.builder().id(3L).quotation(quotation).build();
    StockExportItemResponse response = StockExportItemResponse.builder().itemId(2L)
        .exportStatus(ExportStatus.WAITING_TO_EXPORT).build();

    when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
    when(partRepository.save(any(Part.class))).thenReturn(part);
    when(itemRepository.save(any(PriceQuotationItem.class))).thenReturn(item);
    when(exportRepository.findByQuotationId(1L)).thenReturn(Optional.of(export));
    when(employeeRepository.findById(receiver.getEmployeeId())).thenReturn(Optional.of(receiver));
    when(stockExportItemRepository.save(any(StockExportItem.class))).thenReturn(StockExportItem.builder().build());
    when(itemMapper.toStockExportItemResponse(item)).thenReturn(response);

    // Act
    StockExportItemResponse result = stockExportServiceImpl.exportItem(2L, 1.0, receiver.getEmployeeId());

    // Assert
    assertEquals(ExportStatus.WAITING_TO_EXPORT, result.getExportStatus());
    assertEquals(ExportStatus.WAITING_TO_EXPORT, item.getExportStatus());
  }

  @Test
  void exportItem_WhenNoStockExportExists_ShouldCreateNewStockExport() {
    Part part = Part.builder().partId(1L).quantityInStock(10.0).reservedQuantity(5.0).build();
    PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(1L).build();
    PriceQuotationItem item = PriceQuotationItem.builder()
        .priceQuotationItemId(2L)
        .itemType(PriceQuotationItemType.PART)
        .quantity(5.0)
        .exportedQuantity(2.0)
        .part(part)
        .unit("Cái")
        .priceQuotation(quotation)
        .build();
    Employee receiver = getMockEmployee(Role.SERVICE_ADVISOR);
    StockExport export = StockExport.builder().id(3L).quotation(quotation).build();
    StockExportItemResponse response = StockExportItemResponse.builder().itemId(2L).build();

    when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
    when(partRepository.save(any(Part.class))).thenReturn(part);
    when(itemRepository.save(any(PriceQuotationItem.class))).thenReturn(item);
    when(exportRepository.findByQuotationId(1L)).thenReturn(Optional.empty());
    when(codeSequenceService.generateCode("EX")).thenReturn("EX-001");
    when(exportRepository.save(any(StockExport.class))).thenReturn(export);
    when(employeeRepository.findById(receiver.getEmployeeId())).thenReturn(Optional.of(receiver));
    when(stockExportItemRepository.save(any(StockExportItem.class))).thenReturn(StockExportItem.builder().build());
    when(itemMapper.toStockExportItemResponse(item)).thenReturn(response);

    StockExportItemResponse result = stockExportServiceImpl.exportItem(2L, 1.0, receiver.getEmployeeId());

    assertNotNull(result);
    verify(exportRepository).save(any(StockExport.class));
  }

  @Test
  void exportItem_WhenReceiverNotFound_ShouldThrowResourceNotFoundException() {
    Part part = Part.builder().partId(1L).quantityInStock(10.0).reservedQuantity(5.0).build();
    PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(1L).build();
    PriceQuotationItem item = PriceQuotationItem.builder()
        .priceQuotationItemId(2L)
        .itemType(PriceQuotationItemType.PART)
        .quantity(5.0)
        .exportedQuantity(2.0)
        .part(part)
        .unit("Cái")
        .priceQuotation(quotation)
        .build();

    when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
    when(partRepository.save(any(Part.class))).thenReturn(part);
    when(itemRepository.save(any(PriceQuotationItem.class))).thenReturn(item);
    when(exportRepository.findByQuotationId(1L))
        .thenReturn(Optional.of(StockExport.builder().id(3L).quotation(quotation).build()));
    when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> stockExportServiceImpl.exportItem(2L, 1.0, 99L));
  }
}
