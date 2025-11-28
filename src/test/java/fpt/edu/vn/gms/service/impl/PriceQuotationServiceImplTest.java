package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.*;
import fpt.edu.vn.gms.dto.request.ChangeQuotationStatusReqDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationItemRequestDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.mapper.ServiceTicketMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.pdf.HtmlTemplateService;
import fpt.edu.vn.gms.service.pdf.PdfGeneratorService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PriceQuotationServiceImplTest extends BaseServiceTest {

  @Mock
  private PriceQuotationRepository priceQuotationRepository;

  @Mock
  private ServiceTicketRepository serviceTicketRepository;

  @Mock
  private PartRepository partRepository;

  @Mock
  private PurchaseRequestRepository purchaseRequestRepository;

  @Mock
  private NotificationService notificationService;

  @Mock
  private CodeSequenceService codeSequenceService;

  @Mock
  private HtmlTemplateService htmlTemplateService;

  @Mock
  private PdfGeneratorService pdfGeneratorService;

  @Mock
  private PriceQuotationMapper priceQuotationMapper;

  @Mock
  private ServiceTicketMapper serviceTicketMapper;

  @InjectMocks
  private PriceQuotationServiceImpl priceQuotationServiceImpl;

  @Test
  void findAllQuotations_WhenQuotationsExist_ShouldReturnPagedDtos() {
    PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(1L).build();
    PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder().priceQuotationId(1L).build();
    Page<PriceQuotation> page = new PageImpl<>(List.of(quotation));

    when(priceQuotationRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

    Page<PriceQuotationResponseDto> result = priceQuotationServiceImpl.findAllQuotations(PageRequest.of(0, 10));

    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getPriceQuotationId());
  }

  @Test
  void createQuotation_WhenServiceTicketExists_ShouldCreateAndReturnDto() {
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(1L).build();
    PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder().priceQuotationId(2L).build();

    when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
    when(codeSequenceService.generateCode("QT")).thenReturn("QT-2024-0001");
    when(priceQuotationMapper.toResponseDto(any(PriceQuotation.class))).thenReturn(dto);

    PriceQuotationResponseDto result = priceQuotationServiceImpl.createQuotation(1L);

    assertNotNull(result);
    assertEquals(2L, result.getPriceQuotationId());
    verify(serviceTicketRepository).save(ticket);
  }

  @Test
  void createQuotation_WhenServiceTicketNotFound_ShouldThrowResourceNotFoundException() {
    when(serviceTicketRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> priceQuotationServiceImpl.createQuotation(99L));
  }

  @Test
  void recalculateEstimateAmount_WhenQuotationExists_ShouldUpdateAndReturnDto() {
    PriceQuotationItem item1 = PriceQuotationItem.builder().totalPrice(BigDecimal.valueOf(100)).build();
    PriceQuotationItem item2 = PriceQuotationItem.builder().totalPrice(BigDecimal.valueOf(200)).build();
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(1L)
        .items(List.of(item1, item2))
        .build();
    PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder().priceQuotationId(1L).build();

    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    when(priceQuotationRepository.save(quotation)).thenReturn(quotation);
    when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

    PriceQuotationResponseDto result = priceQuotationServiceImpl.recalculateEstimateAmount(1L);

    assertNotNull(result);
    assertEquals(BigDecimal.valueOf(300), quotation.getEstimateAmount());
    verify(priceQuotationRepository).save(quotation);
  }

  @Test
  void recalculateEstimateAmount_WhenQuotationNotFound_ShouldThrowResourceNotFoundException() {
    when(priceQuotationRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> priceQuotationServiceImpl.recalculateEstimateAmount(99L));
  }

  @Test
  void updateQuotationItems_WhenQuotationExists_ShouldUpdateItemsAndReturnDto() {
    PriceQuotationItem existingItem = PriceQuotationItem.builder()
        .priceQuotationItemId(1L)
        .itemType(PriceQuotationItemType.PART)
        .quantity(1D)
        .unitPrice(BigDecimal.valueOf(100))
        .build();
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(1L)
        .items(new ArrayList<>(List.of(existingItem)))
        .build();
    PriceQuotationItemRequestDto itemDto = PriceQuotationItemRequestDto.builder()
        .priceQuotationItemId(1L)
        .itemName("Linh kiện A")
        .type(PriceQuotationItemType.PART)
        .quantity(2D)
        .unit("Cái")
        .unitPrice(BigDecimal.valueOf(150))
        .partId(10L)
        .build();
    PriceQuotationRequestDto reqDto = PriceQuotationRequestDto.builder()
        .items(List.of(itemDto))
        .estimateAmount(BigDecimal.valueOf(300))
        .build();
    Part part = Part.builder().partId(10L).quantityInStock(10.0).reservedQuantity(0.0).build();
    PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder().priceQuotationId(1L).build();

    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    when(partRepository.findById(10L)).thenReturn(Optional.of(part));
    when(priceQuotationRepository.save(any())).thenReturn(quotation);
    when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

    PriceQuotationResponseDto result = priceQuotationServiceImpl.updateQuotationItems(1L, reqDto);

    assertNotNull(result);
    assertEquals(BigDecimal.valueOf(300), quotation.getEstimateAmount());
    verify(priceQuotationRepository, atLeastOnce()).save(quotation);
  }

  @Test
  void updateQuotationItems_WhenQuotationNotFound_ShouldThrowResourceNotFoundException() {
    PriceQuotationRequestDto reqDto = PriceQuotationRequestDto.builder().build();
    when(priceQuotationRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> priceQuotationServiceImpl.updateQuotationItems(99L, reqDto));
  }

  @Test
  void getById_WhenQuotationExists_ShouldReturnDto() {
    PriceQuotation quotation = PriceQuotation.builder().priceQuotationId(1L).build();
    PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder().priceQuotationId(1L).build();
    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

    PriceQuotationResponseDto result = priceQuotationServiceImpl.getById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getPriceQuotationId());
  }

  @Test
  void updateQuotationStatusManual_WhenValid_ShouldUpdateStatusAndReturnDto() {
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(1L)
        .status(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM)
        .build();
    ChangeQuotationStatusReqDto reqDto = ChangeQuotationStatusReqDto.builder()
        .status(PriceQuotationStatus.CUSTOMER_CONFIRMED)
        .build();
    PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder().priceQuotationId(1L).build();

    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    when(priceQuotationRepository.save(quotation)).thenReturn(quotation);
    when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

    PriceQuotationResponseDto result = priceQuotationServiceImpl.updateQuotationStatusManual(1L, reqDto);

    assertEquals(PriceQuotationStatus.CUSTOMER_CONFIRMED, quotation.getStatus());
    assertNotNull(result);
  }

  @Test
  void updateQuotationStatusManual_WhenInvalidStatus_ShouldThrowRuntimeException() {
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(1L)
        .status(PriceQuotationStatus.DRAFT)
        .build();
    ChangeQuotationStatusReqDto reqDto = ChangeQuotationStatusReqDto.builder()
        .status(PriceQuotationStatus.CUSTOMER_CONFIRMED)
        .build();

    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));

    assertThrows(RuntimeException.class, () -> priceQuotationServiceImpl.updateQuotationStatusManual(1L, reqDto));
  }

  @Test
  void confirmQuotationByCustomer_WhenValid_ShouldUpdateStatusAndCreateNotification() {
    Employee advisor = getMockEmployee(Role.SERVICE_ADVISOR);
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(1L).createdBy(advisor).build();
    Part part = Part.builder().partId(10L).quantityInStock(10.0).reservedQuantity(0.0)
        .purchasePrice(BigDecimal.valueOf(100)).build();
    PriceQuotationItem item = PriceQuotationItem.builder()
        .itemType(PriceQuotationItemType.PART)
        .inventoryStatus(PriceQuotationItemStatus.AVAILABLE)
        .quantity(2D)
        .unit("Cái")
        .unitPrice(BigDecimal.valueOf(100))
        .part(part)
        .build();
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(1L)
        .status(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM)
        .items(new ArrayList<>(List.of(item)))
        .serviceTicket(ticket)
        .build();
    PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder().priceQuotationId(1L).build();
    NotificationResponseDto notiDto = NotificationResponseDto.builder().title("title").build();

    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    when(partRepository.save(any())).thenReturn(part);
    when(priceQuotationRepository.save(any())).thenReturn(quotation);
    when(notificationService.createNotification(anyLong(), anyString(), anyString(), any(), any(), any()))
        .thenReturn(notiDto);
    when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

    PriceQuotationResponseDto result = priceQuotationServiceImpl.confirmQuotationByCustomer(1L);

    assertEquals(PriceQuotationStatus.CUSTOMER_CONFIRMED, quotation.getStatus());
    assertNotNull(result);
    verify(notificationService).createNotification(anyLong(), anyString(), anyString(), any(), any(), any());
  }

  @Test
  void confirmQuotationByCustomer_WhenStatusInvalid_ShouldThrowRuntimeException() {
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(1L)
        .status(PriceQuotationStatus.DRAFT)
        .build();
    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    assertThrows(RuntimeException.class, () -> priceQuotationServiceImpl.confirmQuotationByCustomer(1L));
  }

  @Test
  void rejectQuotationByCustomer_WhenValid_ShouldUpdateStatusAndCreateNotification() {
    Employee advisor = getMockEmployee(Role.SERVICE_ADVISOR);
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(1L).createdBy(advisor).build();
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(1L)
        .status(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM)
        .serviceTicket(ticket)
        .build();
    PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder().priceQuotationId(1L).build();
    NotificationResponseDto notiDto = NotificationResponseDto.builder().title("title").build();

    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    when(priceQuotationRepository.save(quotation)).thenReturn(quotation);
    when(notificationService.createNotification(anyLong(), anyString(), anyString(), any(), any(), any()))
        .thenReturn(notiDto);
    when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

    PriceQuotationResponseDto result = priceQuotationServiceImpl.rejectQuotationByCustomer(1L, "Lý do");

    assertEquals(PriceQuotationStatus.CUSTOMER_REJECTED, quotation.getStatus());
    assertNotNull(result);
    verify(notificationService).createNotification(anyLong(), anyString(), anyString(), any(), any(), any());
  }

  @Test
  void rejectQuotationByCustomer_WhenStatusInvalid_ShouldThrowRuntimeException() {
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(1L)
        .status(PriceQuotationStatus.DRAFT)
        .build();
    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    assertThrows(RuntimeException.class, () -> priceQuotationServiceImpl.rejectQuotationByCustomer(1L, "Lý do"));
  }

  @Test
  void sendQuotationToCustomer_WhenStatusValid_ShouldUpdateStatusAndReturnDto() {
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(1L)
        .status(PriceQuotationStatus.WAREHOUSE_CONFIRMED)
        .build();
    PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder().priceQuotationId(1L).build();

    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    when(priceQuotationRepository.save(quotation)).thenReturn(quotation);
    when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

    PriceQuotationResponseDto result = priceQuotationServiceImpl.sendQuotationToCustomer(1L);

    assertEquals(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM, quotation.getStatus());
    assertNotNull(result);
  }

  @Test
  void sendQuotationToCustomer_WhenStatusInvalid_ShouldThrowRuntimeException() {
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(1L)
        .status(PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM)
        .build();
    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    assertThrows(RuntimeException.class, () -> priceQuotationServiceImpl.sendQuotationToCustomer(1L));
  }

  @Test
  void countWaitingCustomerConfirm_WhenCalled_ShouldReturnCount() {
    when(priceQuotationRepository.countByStatus(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM)).thenReturn(5L);
    long result = priceQuotationServiceImpl.countWaitingCustomerConfirm();
    assertEquals(5L, result);
  }

  @Test
  void countVehicleInRepairingStatus_WhenCalled_ShouldReturnCount() {
    when(priceQuotationRepository.countByStatus(PriceQuotationStatus.CUSTOMER_CONFIRMED)).thenReturn(3L);
    long result = priceQuotationServiceImpl.countVehicleInRepairingStatus();
    assertEquals(3L, result);
  }

  @Test
  void updateLaborCost_WhenQuotationExists_ShouldUpdateEstimateAmountAndReturnDto() {
    PriceQuotationItem item1 = PriceQuotationItem.builder().totalPrice(BigDecimal.valueOf(100)).build();
    PriceQuotationItem item2 = PriceQuotationItem.builder().totalPrice(BigDecimal.valueOf(200)).build();
    PriceQuotation quotation = PriceQuotation.builder()
        .priceQuotationId(1L)
        .items(List.of(item1, item2))
        .build();
    PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder().priceQuotationId(1L).build();

    when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
    when(priceQuotationRepository.save(quotation)).thenReturn(quotation);
    when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

    PriceQuotationResponseDto result = priceQuotationServiceImpl.updateLaborCost(1L);

    assertNotNull(result);
    assertEquals(BigDecimal.valueOf(300), quotation.getEstimateAmount());
    verify(priceQuotationRepository).save(quotation);
  }

  @Test
  void updateLaborCost_WhenQuotationNotFound_ShouldThrowResourceNotFoundException() {
    when(priceQuotationRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> priceQuotationServiceImpl.updateLaborCost(99L));
  }

  @Test
  void exportPdfQuotation_WhenServiceTicketExists_ShouldReturnPdfBytes() {
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(1L).build();
    ServiceTicketResponseDto ticketDto = ServiceTicketResponseDto.builder()
        .createdAt(LocalDateTime.now())
        .serviceTicketCode("ST-001")
        .customer(fpt.edu.vn.gms.dto.response.CustomerResponseDto.builder().fullName("A").address("B").build())
        .vehicle(fpt.edu.vn.gms.dto.response.VehicleResponseDto.builder().licensePlate("ABC").vehicleModelName("Model")
            .vin("VIN").build())
        .receiveCondition("Good")
        .priceQuotation(fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto.builder()
            .estimateAmount(BigDecimal.valueOf(1000))
            .items(List.of())
            .build())
        .build();

    when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
    when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(ticketDto);
    when(htmlTemplateService.loadAndFillTemplate(anyString(), anyMap())).thenReturn("<html></html>");
    when(pdfGeneratorService.generateQuotationPdf(anyString())).thenReturn(new byte[] { 1, 2, 3 });

    byte[] result = priceQuotationServiceImpl.exportPdfQuotation(1L);

    assertNotNull(result);
    assertEquals(3, result.length);
  }

  @Test
  void exportPdfQuotation_WhenServiceTicketNotFound_ShouldThrowResourceNotFoundException() {
    when(serviceTicketRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> priceQuotationServiceImpl.exportPdfQuotation(99L));
  }
}
