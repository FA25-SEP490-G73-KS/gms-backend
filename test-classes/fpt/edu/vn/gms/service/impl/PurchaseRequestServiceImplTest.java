package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.common.enums.NotificationTemplate;
import fpt.edu.vn.gms.common.enums.NotificationType;
import fpt.edu.vn.gms.common.enums.PurchaseReqItemStatus;
import fpt.edu.vn.gms.common.enums.PurchaseRequestStatus;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.request.PartItemDto;
import fpt.edu.vn.gms.dto.request.PurchaseRequestCreateDto;
import fpt.edu.vn.gms.dto.response.PrDetailInfoReviewDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.dto.response.StockReceiptDetailResponse;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PurchaseRequestItemMapper;
import fpt.edu.vn.gms.mapper.PurchaseRequestMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.StockReceiptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseRequestServiceImplTest {

    @Mock
    PurchaseRequestRepository purchaseRequestRepo;
    @Mock
    PurchaseRequestItemRepository purchaseRequestItemRepo;
    @Mock
    AccountRepository accountRepository;
    @Mock
    NotificationService notificationService;
    @Mock
    EmployeeRepository employeeRepo;
    @Mock
    PartRepository partRepo;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    PurchaseRequestItemMapper purchaseRequestItemMapper;
    @Mock
    PurchaseRequestMapper purchaseRequestMapper;
    @Mock
    StockReceiptService stockReceiptService;

    @InjectMocks
    PurchaseRequestServiceImpl service;

    @Test
    void getPurchaseRequests_ShouldReturnPagedDtos() {
        Pageable pageable = PageRequest.of(0, 5);
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(1L)
                .code("PR-2025-00001")
                .build();
        Page<PurchaseRequest> page = new PageImpl<>(List.of(pr), pageable, 1);

        when(purchaseRequestRepo.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        PurchaseRequestResponseDto dto = PurchaseRequestResponseDto.builder()
                .id(1L)
                .code("PR-2025-00001")
                .build();
        when(purchaseRequestMapper.toListDto(pr)).thenReturn(dto);

        Page<PurchaseRequestResponseDto> result = service.getPurchaseRequests(null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(purchaseRequestRepo).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getPurchaseRequestDetail_ShouldReturnDto_WhenFound() {
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(1L)
                .code("PR-2025-00001")
                .build();
        when(purchaseRequestRepo.findById(1L)).thenReturn(Optional.of(pr));

        PurchaseRequestDetailDto dto = PurchaseRequestDetailDto.builder()
                .id(1L)
                .code("PR-2025-00001")
                .build();
        when(purchaseRequestMapper.toDetailDto(pr)).thenReturn(dto);

        PurchaseRequestDetailDto result = service.getPurchaseRequestDetail(1L);

        assertSame(dto, result);
        verify(purchaseRequestRepo).findById(1L);
    }

    @Test
    void getPurchaseRequestDetail_ShouldThrow_WhenNotFound() {
        when(purchaseRequestRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getPurchaseRequestDetail(1L));
        verify(purchaseRequestRepo).findById(1L);
    }

    // Note: getItem method no longer exists in current implementation
    // This test has been removed as the method is not in the current service interface

    @Test
    void createRequest_ShouldCreateRequestAndItems() {
        Employee creator = Employee.builder()
                .employeeId(1L)
                .fullName("Creator")
                .build();
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(creator));

        Unit unit = Unit.builder()
                .id(1L)
                .name("Cái")
                .build();
        Part part1 = Part.builder()
                .partId(10L)
                .name("Part 1")
                .purchasePrice(new BigDecimal("100000"))
                .unit(unit)
                .build();
        Part part2 = Part.builder()
                .partId(11L)
                .name("Part 2")
                .purchasePrice(new BigDecimal("200000"))
                .unit(unit)
                .build();

        when(partRepo.findById(10L)).thenReturn(Optional.of(part1));
        when(partRepo.findById(11L)).thenReturn(Optional.of(part2));

        when(codeSequenceService.generateCode("PR")).thenReturn("PR-2025-00001");

        PurchaseRequest savedPr = PurchaseRequest.builder()
                .id(100L)
                .code("PR-2025-00001")
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>())
                .build();
        when(purchaseRequestRepo.save(any(PurchaseRequest.class))).thenReturn(savedPr);

        PurchaseRequestDetailDto detailDto = PurchaseRequestDetailDto.builder()
                .id(100L)
                .code("PR-2025-00001")
                .items(new ArrayList<>())
                .build();
        when(purchaseRequestMapper.toDetailDto(any(PurchaseRequest.class))).thenReturn(detailDto);

        // Note: createRequest method no longer exists
        // The service now uses createPurchaseRequestFromQuotation(Long quotationId)
        // This test has been commented out as the method signature has changed
        /*
        PartItemDto item1 = new PartItemDto();
        item1.setPartId(10L);
        item1.setQuantity(2.0);

        PartItemDto item2 = new PartItemDto();
        item2.setPartId(11L);
        item2.setQuantity(3.0);

        PurchaseRequestCreateDto dto = new PurchaseRequestCreateDto();
        dto.setCreatedById(1L);
        dto.setReason("Test reason");
        dto.setNote("Test note");
        dto.setItems(List.of(item1, item2));

        PurchaseRequestDetailDto result = service.createRequest(dto);
        */

        // assertNotNull(result);
        // verify(employeeRepo).findById(1L);
        // verify(partRepo).findById(10L);
        // verify(partRepo).findById(11L);
        // verify(codeSequenceService).generateCode("PR");
        // verify(purchaseRequestItemRepo, times(2)).save(any(PurchaseRequestItem.class));
        // verify(purchaseRequestRepo, atLeastOnce()).save(any(PurchaseRequest.class));
    }

    // Note: createRequest method tests removed as the method no longer exists
    // The service now uses createPurchaseRequestFromQuotation(Long quotationId)

    @Test
    void approvePurchaseRequest_ShouldUpdateStatus() {
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(100L)
                .code("PR-2025-00001")
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(List.of(PurchaseRequestItem.builder()
                        .itemId(1L)
                        .build()))
                .build();

        when(purchaseRequestRepo.findById(100L)).thenReturn(Optional.of(pr));
        when(purchaseRequestRepo.save(pr)).thenReturn(pr);
        when(stockReceiptService.createReceiptFromPurchaseRequest(100L))
                .thenReturn(StockReceiptDetailResponse.builder().id(1L).build());

        PurchaseRequest result = service.approvePurchaseRequest(100L);

        assertEquals(ManagerReviewStatus.APPROVED, pr.getReviewStatus());
        verify(purchaseRequestRepo).save(pr);
    }

    @Test
    void approvePurchaseRequest_ShouldThrow_WhenNotFound() {
        when(purchaseRequestRepo.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.approvePurchaseRequest(100L));
    }

    @Test
    void approvePurchaseRequest_ShouldThrow_WhenNoItems() {
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(100L)
                .items(new ArrayList<>())
                .build();

        when(purchaseRequestRepo.findById(100L)).thenReturn(Optional.of(pr));

        assertThrows(RuntimeException.class,
                () -> service.approvePurchaseRequest(100L));
    }

    // Note: reviewItem method no longer exists in current implementation
    // The service now uses approvePurchaseRequest(Long requestId) to approve the entire request
    // These tests have been removed as the method is not in the current service interface
}

