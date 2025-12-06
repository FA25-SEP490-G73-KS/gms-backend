package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.*;
import fpt.edu.vn.gms.dto.request.ChangeQuotationStatusReqDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationItemRequestDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceQuotationServiceImplTest {

    @Mock
    PriceQuotationRepository priceQuotationRepository;
    @Mock
    ServiceTicketRepository serviceTicketRepository;
    @Mock
    PartRepository partRepository;
    @Mock
    PurchaseRequestRepository purchaseRequestRepository;
    @Mock
    NotificationService notificationService;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    HtmlTemplateService htmlTemplateService;
    @Mock
    PdfGeneratorService pdfGeneratorService;
    @Mock
    PriceQuotationMapper priceQuotationMapper;
    @Mock
    ServiceTicketMapper serviceTicketMapper;

    @InjectMocks
    PriceQuotationServiceImpl service;

    @Test
    void findAllQuotations_ShouldReturnPagedDtos() {
        Pageable pageable = PageRequest.of(0, 5);
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .code("QT-2025-00001")
                .build();
        Page<PriceQuotation> page = new PageImpl<>(List.of(quotation), pageable, 1);

        when(priceQuotationRepository.findAll(pageable)).thenReturn(page);

        PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder()
                .priceQuotationId(1L)
                .code("QT-2025-00001")
                .build();
        when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

        Page<PriceQuotationResponseDto> result = service.findAllQuotations(pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(priceQuotationRepository).findAll(pageable);
    }

    @Test
    void createQuotation_ShouldCreateAndLinkToServiceTicket() {
        DiscountPolicy discountPolicy = DiscountPolicy.builder()
                .discountPolicyId(1L)
                .discountRate(new BigDecimal("0.1"))
                .build();
        Customer customer = Customer.builder()
                .customerId(1L)
                .discountPolicy(discountPolicy)
                .build();
        ServiceTicket serviceTicket = ServiceTicket.builder()
                .serviceTicketId(100L)
                .serviceTicketCode("STK-2025-00001")
                .customer(customer)
                .status(ServiceTicketStatus.CREATED)
                .build();

        when(serviceTicketRepository.findById(100L)).thenReturn(Optional.of(serviceTicket));
        when(codeSequenceService.generateCode("QT")).thenReturn("QT-2025-00001");
        when(serviceTicketRepository.save(serviceTicket)).thenReturn(serviceTicket);

        ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder()
                .serviceTicketId(100L)
                .serviceTicketCode("STK-2025-00001")
                .build();
        when(serviceTicketMapper.toResponseDto(serviceTicket)).thenReturn(dto);

        ServiceTicketResponseDto result = service.createQuotation(100L);

        assertSame(dto, result);
        assertEquals(ServiceTicketStatus.WAITING_FOR_QUOTATION, serviceTicket.getStatus());
        assertNotNull(serviceTicket.getPriceQuotation());
        verify(serviceTicketRepository).findById(100L);
        verify(serviceTicketRepository).save(serviceTicket);
    }

    @Test
    void createQuotation_ShouldThrow_WhenServiceTicketNotFound() {
        when(serviceTicketRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.createQuotation(100L));
        verify(serviceTicketRepository).findById(100L);
        verify(serviceTicketRepository, never()).save(any());
    }

    @Test
    void recalculateEstimateAmount_ShouldRecalculateFromItems() {
        PriceQuotationItem item1 = PriceQuotationItem.builder()
                .priceQuotationItemId(1L)
                .totalPrice(new BigDecimal("100000"))
                .build();
        PriceQuotationItem item2 = PriceQuotationItem.builder()
                .priceQuotationItemId(2L)
                .totalPrice(new BigDecimal("200000"))
                .build();
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .items(List.of(item1, item2))
                .estimateAmount(BigDecimal.ZERO)
                .build();

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(priceQuotationRepository.save(quotation)).thenReturn(quotation);

        PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder()
                .priceQuotationId(1L)
                .estimateAmount(new BigDecimal("300000"))
                .build();
        when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

        PriceQuotationResponseDto result = service.recalculateEstimateAmount(1L);

        assertSame(dto, result);
        assertEquals(new BigDecimal("300000"), quotation.getEstimateAmount());
        verify(priceQuotationRepository).save(quotation);
    }

    @Test
    void recalculateEstimateAmount_ShouldThrow_WhenNotFound() {
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.recalculateEstimateAmount(1L));
        verify(priceQuotationRepository).findById(1L);
        verify(priceQuotationRepository, never()).save(any());
    }

    @Test
    void updateQuotationItems_ShouldUpdateExistingItems() {
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .items(new ArrayList<>())
                .build();

        PriceQuotationItem existingItem = PriceQuotationItem.builder()
                .priceQuotationItemId(10L)
                .itemName("Old Name")
                .itemType(PriceQuotationItemType.SERVICE)
                .quantity(1.0)
                .unitPrice(new BigDecimal("100000"))
                .totalPrice(new BigDecimal("100000"))
                .priceQuotation(quotation)
                .build();
        quotation.getItems().add(existingItem);

        ServiceTicket serviceTicket = ServiceTicket.builder()
                .serviceTicketId(100L)
                .build();
        quotation.setServiceTicket(serviceTicket);

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(priceQuotationRepository.save(quotation)).thenReturn(quotation);

        PriceQuotationItemRequestDto itemDto = PriceQuotationItemRequestDto.builder()
                .priceQuotationItemId(10L)
                .itemName("New Name")
                .type(PriceQuotationItemType.SERVICE)
                .quantity(2.0)
                .unitPrice(new BigDecimal("150000"))
                .build();

        PriceQuotationRequestDto requestDto = PriceQuotationRequestDto.builder()
                .items(List.of(itemDto))
                .estimateAmount(new BigDecimal("300000"))
                .build();

        ServiceTicketResponseDto ticketDto = ServiceTicketResponseDto.builder()
                .serviceTicketId(100L)
                .build();
        when(serviceTicketMapper.toResponseDto(serviceTicket)).thenReturn(ticketDto);

        ServiceTicketResponseDto result = service.updateQuotationItems(1L, requestDto);

        assertSame(ticketDto, result);
        assertEquals("New Name", existingItem.getItemName());
        assertEquals(2.0, existingItem.getQuantity());
        assertEquals(new BigDecimal("300000"), existingItem.getTotalPrice());
        verify(priceQuotationRepository).save(quotation);
    }

    @Test
    void updateQuotationItems_ShouldAddNewItems() {
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .items(new ArrayList<>())
                .build();

        ServiceTicket serviceTicket = ServiceTicket.builder()
                .serviceTicketId(100L)
                .build();
        quotation.setServiceTicket(serviceTicket);

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(priceQuotationRepository.save(quotation)).thenReturn(quotation);

        PriceQuotationItemRequestDto itemDto = PriceQuotationItemRequestDto.builder()
                .priceQuotationItemId(null)
                .itemName("New Item")
                .type(PriceQuotationItemType.SERVICE)
                .quantity(1.0)
                .unitPrice(new BigDecimal("50000"))
                .build();

        PriceQuotationRequestDto requestDto = PriceQuotationRequestDto.builder()
                .items(List.of(itemDto))
                .estimateAmount(new BigDecimal("50000"))
                .build();

        ServiceTicketResponseDto ticketDto = ServiceTicketResponseDto.builder()
                .serviceTicketId(100L)
                .build();
        when(serviceTicketMapper.toResponseDto(serviceTicket)).thenReturn(ticketDto);

        ServiceTicketResponseDto result = service.updateQuotationItems(1L, requestDto);

        assertSame(ticketDto, result);
        assertEquals(1, quotation.getItems().size());
        assertEquals("New Item", quotation.getItems().get(0).getItemName());
        verify(priceQuotationRepository).save(quotation);
    }

    @Test
    void updateQuotationItems_ShouldSetStatusToWarehouseConfirmed_WhenAllPartsReady() {
        Part part = Part.builder()
                .partId(1L)
                .quantityInStock(10.0)
                .reservedQuantity(2.0)
                .build();

        PriceQuotationItem partItem = PriceQuotationItem.builder()
                .priceQuotationItemId(10L)
                .itemType(PriceQuotationItemType.PART)
                .part(part)
                .inventoryStatus(PriceQuotationItemStatus.AVAILABLE)
                .warehouseReviewStatus(WarehouseReviewStatus.CONFIRMED)
                .build();

        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .items(new ArrayList<>(List.of(partItem)))
                .status(PriceQuotationStatus.DRAFT)
                .build();

        ServiceTicket serviceTicket = ServiceTicket.builder()
                .serviceTicketId(100L)
                .build();
        quotation.setServiceTicket(serviceTicket);

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(priceQuotationRepository.save(quotation)).thenReturn(quotation);

        PriceQuotationItemRequestDto itemDto = PriceQuotationItemRequestDto.builder()
                .priceQuotationItemId(10L)
                .itemName("Part Name")
                .type(PriceQuotationItemType.PART)
                .partId(1L)
                .quantity(5.0)
                .unitPrice(new BigDecimal("100000"))
                .build();

        PriceQuotationRequestDto requestDto = PriceQuotationRequestDto.builder()
                .items(List.of(itemDto))
                .estimateAmount(new BigDecimal("500000"))
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(part));
        when(serviceTicketMapper.toResponseDto(serviceTicket)).thenReturn(
                ServiceTicketResponseDto.builder().build());

        service.updateQuotationItems(1L, requestDto);

        assertEquals(PriceQuotationStatus.WAREHOUSE_CONFIRMED, quotation.getStatus());
        verify(priceQuotationRepository, atLeastOnce()).save(quotation);
    }

    @Test
    void getById_ShouldReturnDto_WhenFound() {
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .code("QT-2025-00001")
                .build();
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));

        PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder()
                .priceQuotationId(1L)
                .code("QT-2025-00001")
                .build();
        when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

        PriceQuotationResponseDto result = service.getById(1L);

        assertSame(dto, result);
        verify(priceQuotationRepository).findById(1L);
    }

    @Test
    void getById_ShouldReturnNull_WhenNotFound() {
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.empty());
        when(priceQuotationMapper.toResponseDto(null)).thenReturn(null);

        PriceQuotationResponseDto result = service.getById(1L);

        assertNull(result);
        verify(priceQuotationRepository).findById(1L);
    }

    @Test
    void updateQuotationStatusManual_ShouldUpdate_WhenStatusIsWaitingCustomerConfirm() {
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .status(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM)
                .build();
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(priceQuotationRepository.save(quotation)).thenReturn(quotation);

        ChangeQuotationStatusReqDto reqDto = ChangeQuotationStatusReqDto.builder()
                .status(PriceQuotationStatus.CUSTOMER_CONFIRMED)
                .build();

        PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder()
                .priceQuotationId(1L)
                .status(PriceQuotationStatus.CUSTOMER_CONFIRMED)
                .build();
        when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

        PriceQuotationResponseDto result = service.updateQuotationStatusManual(1L, reqDto);

        assertSame(dto, result);
        assertEquals(PriceQuotationStatus.CUSTOMER_CONFIRMED, quotation.getStatus());
        verify(priceQuotationRepository).save(quotation);
    }

    @Test
    void updateQuotationStatusManual_ShouldThrow_WhenStatusIsNotWaitingCustomerConfirm() {
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .status(PriceQuotationStatus.DRAFT)
                .build();
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));

        ChangeQuotationStatusReqDto reqDto = ChangeQuotationStatusReqDto.builder()
                .status(PriceQuotationStatus.CUSTOMER_CONFIRMED)
                .build();

        assertThrows(RuntimeException.class,
                () -> service.updateQuotationStatusManual(1L, reqDto));
        verify(priceQuotationRepository).findById(1L);
        verify(priceQuotationRepository, never()).save(any());
    }

    @Test
    void confirmQuotationByCustomer_ShouldReservePartsAndCreatePurchaseRequest() {
        Part availablePart = Part.builder()
                .partId(1L)
                .quantityInStock(10.0)
                .reservedQuantity(2.0)
                .purchasePrice(new BigDecimal("100000"))
                .build();
        Part outOfStockPart = Part.builder()
                .partId(2L)
                .quantityInStock(1.0)
                .reservedQuantity(0.0)
                .purchasePrice(new BigDecimal("200000"))
                .reorderLevel(5.0)
                .build();

        PriceQuotationItem availableItem = PriceQuotationItem.builder()
                .priceQuotationItemId(10L)
                .itemType(PriceQuotationItemType.PART)
                .part(availablePart)
                .quantity(3.0)
                .inventoryStatus(PriceQuotationItemStatus.AVAILABLE)
                .build();
        PriceQuotationItem outOfStockItem = PriceQuotationItem.builder()
                .priceQuotationItemId(11L)
                .itemType(PriceQuotationItemType.PART)
                .part(outOfStockPart)
                .quantity(5.0)
                .inventoryStatus(PriceQuotationItemStatus.OUT_OF_STOCK)
                .build();

        Employee advisor = Employee.builder()
                .employeeId(1L)
                .fullName("Advisor")
                .build();
        ServiceTicket serviceTicket = ServiceTicket.builder()
                .serviceTicketId(100L)
                .createdBy(advisor)
                .build();

        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .code("QT-2025-00001")
                .status(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM)
                .items(List.of(availableItem, outOfStockItem))
                .serviceTicket(serviceTicket)
                .build();

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(partRepository.save(availablePart)).thenReturn(availablePart);
        when(codeSequenceService.generateCode("PR")).thenReturn("PR-2025-00001");
        when(purchaseRequestRepository.save(any(PurchaseRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(priceQuotationRepository.save(quotation)).thenReturn(quotation);
        when(notificationService.createNotification(anyLong(), anyString(), anyString(),
                any(), anyString(), anyString())).thenReturn(null);

        PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder()
                .priceQuotationId(1L)
                .build();
        when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

        PriceQuotationResponseDto result = service.confirmQuotationByCustomer(1L);

        assertSame(dto, result);
        assertEquals(PriceQuotationStatus.CUSTOMER_CONFIRMED, quotation.getStatus());
        // Note: PriceQuotationItem does not have exportStatus field
        // The service implementation does not set exportStatus
        // assertEquals(ExportStatus.WAITING_TO_EXPORT, availableItem.getExportStatus());
        // assertEquals(ExportStatus.WAITING_PURCHASE, outOfStockItem.getExportStatus());
        assertEquals(5.0, availablePart.getReservedQuantity()); // 2.0 + 3.0
        verify(partRepository).save(availablePart);
        verify(purchaseRequestRepository).save(any(PurchaseRequest.class));
    }

    @Test
    void confirmQuotationByCustomer_ShouldThrow_WhenStatusIsNotWaitingCustomerConfirm() {
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .status(PriceQuotationStatus.DRAFT)
                .build();
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));

        assertThrows(RuntimeException.class,
                () -> service.confirmQuotationByCustomer(1L));
        verify(priceQuotationRepository).findById(1L);
        verify(priceQuotationRepository, never()).save(any());
    }

    @Test
    void rejectQuotationByCustomer_ShouldUpdateStatusAndNotify() {
        Employee advisor = Employee.builder()
                .employeeId(1L)
                .fullName("Advisor")
                .build();
        ServiceTicket serviceTicket = ServiceTicket.builder()
                .serviceTicketId(100L)
                .createdBy(advisor)
                .build();

        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .code("QT-2025-00001")
                .status(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM)
                .serviceTicket(serviceTicket)
                .build();

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(priceQuotationRepository.save(quotation)).thenReturn(quotation);
        when(notificationService.createNotification(anyLong(), anyString(), anyString(),
                any(), anyString(), anyString())).thenReturn(null);

        PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder()
                .priceQuotationId(1L)
                .status(PriceQuotationStatus.CUSTOMER_REJECTED)
                .build();
        when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

        PriceQuotationResponseDto result = service.rejectQuotationByCustomer(1L, "Too expensive");

        assertSame(dto, result);
        assertEquals(PriceQuotationStatus.CUSTOMER_REJECTED, quotation.getStatus());
        verify(priceQuotationRepository).save(quotation);
        verify(notificationService).createNotification(anyLong(), anyString(), anyString(),
                eq(NotificationType.QUOTATION_REJECTED), anyString(), anyString());
    }

    @Test
    void rejectQuotationByCustomer_ShouldThrow_WhenStatusIsNotWaitingCustomerConfirm() {
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .status(PriceQuotationStatus.DRAFT)
                .build();
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));

        assertThrows(RuntimeException.class,
                () -> service.rejectQuotationByCustomer(1L, "Reason"));
        verify(priceQuotationRepository).findById(1L);
        verify(priceQuotationRepository, never()).save(any());
    }

    @Test
    void sendQuotationToCustomer_ShouldUpdateStatus() {
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .status(PriceQuotationStatus.WAREHOUSE_CONFIRMED)
                .build();
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(priceQuotationRepository.save(quotation)).thenReturn(quotation);

        PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder()
                .priceQuotationId(1L)
                .status(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM)
                .build();
        when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

        PriceQuotationResponseDto result = service.sendQuotationToCustomer(1L);

        assertSame(dto, result);
        assertEquals(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM, quotation.getStatus());
        verify(priceQuotationRepository).save(quotation);
    }

    @Test
    void sendQuotationToCustomer_ShouldThrow_WhenStatusIsWaitingWarehouseConfirm() {
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .status(PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM)
                .build();
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));

        assertThrows(RuntimeException.class,
                () -> service.sendQuotationToCustomer(1L));
        verify(priceQuotationRepository).findById(1L);
        verify(priceQuotationRepository, never()).save(any());
    }

    @Test
    void countWaitingCustomerConfirm_ShouldReturnCount() {
        when(priceQuotationRepository.countByStatus(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM))
                .thenReturn(5L);

        long result = service.countWaitingCustomerConfirm();

        assertEquals(5L, result);
        verify(priceQuotationRepository).countByStatus(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM);
    }

    @Test
    void countVehicleInRepairingStatus_ShouldReturnCount() {
        when(priceQuotationRepository.countByStatus(PriceQuotationStatus.CUSTOMER_CONFIRMED))
                .thenReturn(3L);

        long result = service.countVehicleInRepairingStatus();

        assertEquals(3L, result);
        verify(priceQuotationRepository).countByStatus(PriceQuotationStatus.CUSTOMER_CONFIRMED);
    }

    @Test
    void updateLaborCost_ShouldRecalculateEstimateAmount() {
        PriceQuotationItem item1 = PriceQuotationItem.builder()
                .priceQuotationItemId(1L)
                .totalPrice(new BigDecimal("100000"))
                .build();
        PriceQuotationItem item2 = PriceQuotationItem.builder()
                .priceQuotationItemId(2L)
                .totalPrice(new BigDecimal("200000"))
                .build();
        PriceQuotation quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .items(List.of(item1, item2))
                .estimateAmount(BigDecimal.ZERO)
                .build();

        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.of(quotation));
        when(priceQuotationRepository.save(quotation)).thenReturn(quotation);

        PriceQuotationResponseDto dto = PriceQuotationResponseDto.builder()
                .priceQuotationId(1L)
                .estimateAmount(new BigDecimal("300000"))
                .build();
        when(priceQuotationMapper.toResponseDto(quotation)).thenReturn(dto);

        PriceQuotationResponseDto result = service.updateLaborCost(1L);

        assertSame(dto, result);
        assertEquals(new BigDecimal("300000"), quotation.getEstimateAmount());
        verify(priceQuotationRepository).save(quotation);
    }

    @Test
    void updateLaborCost_ShouldThrow_WhenNotFound() {
        when(priceQuotationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateLaborCost(1L));
        verify(priceQuotationRepository).findById(1L);
        verify(priceQuotationRepository, never()).save(any());
    }

    @Test
    void exportPdfQuotation_ShouldGeneratePdf() {
        ServiceTicket serviceTicket = ServiceTicket.builder()
                .serviceTicketId(100L)
                .serviceTicketCode("STK-2025-00001")
                .build();
        when(serviceTicketRepository.findById(100L)).thenReturn(Optional.of(serviceTicket));

        ServiceTicketResponseDto ticketDto = ServiceTicketResponseDto.builder()
                .serviceTicketId(100L)
                .serviceTicketCode("STK-2025-00001")
                .createdAt(LocalDateTime.now())
                .build();
        when(serviceTicketMapper.toResponseDto(serviceTicket)).thenReturn(ticketDto);

        when(htmlTemplateService.loadAndFillTemplate(anyString(), any())).thenReturn("<html>...</html>");
        byte[] pdfBytes = new byte[]{1, 2, 3};
        when(pdfGeneratorService.generateQuotationPdf(anyString())).thenReturn(pdfBytes);

        byte[] result = service.exportPdfQuotation(100L);

        assertSame(pdfBytes, result);
        verify(serviceTicketRepository).findById(100L);
        verify(htmlTemplateService).loadAndFillTemplate(anyString(), any());
        verify(pdfGeneratorService).generateQuotationPdf(anyString());
    }

    @Test
    void exportPdfQuotation_ShouldThrow_WhenServiceTicketNotFound() {
        when(serviceTicketRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.exportPdfQuotation(100L));
        verify(serviceTicketRepository).findById(100L);
        verify(htmlTemplateService, never()).loadAndFillTemplate(anyString(), any());
    }
}

