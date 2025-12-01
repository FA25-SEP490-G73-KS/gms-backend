package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.common.enums.NotificationTemplate;
import fpt.edu.vn.gms.common.enums.NotificationType;
import fpt.edu.vn.gms.common.enums.PurchaseReqItemStatus;
import fpt.edu.vn.gms.common.enums.PurchaseRequestStatus;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.PartItemDto;
import fpt.edu.vn.gms.dto.request.PurchaseRequestCreateDto;
import fpt.edu.vn.gms.dto.response.PrDetailInfoReviewDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PurchaseRequestDetailMapper;
import fpt.edu.vn.gms.mapper.PurchaseRequestItemMapper;
import fpt.edu.vn.gms.mapper.PurchaseRequestMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.NotificationService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    PurchaseRequestDetailMapper purchaseRequestDetailMapper;
    @Mock
    PurchaseRequestMapper purchaseRequestMapper;

    @InjectMocks
    PurchaseRequestServiceImpl service;

    @Test
    void getPurchaseRequests_ShouldReturnPagedDtos() {
        Pageable pageable = PageRequest.of(0, 5);
        PurchaseRequestResponseDto dto = PurchaseRequestResponseDto.builder()
                .id(1L)
                .code("PR-2025-00001")
                .build();
        Page<PurchaseRequestResponseDto> page = new PageImpl<>(List.of(dto), pageable, 1);

        when(purchaseRequestRepo.findAllCustom(pageable)).thenReturn(page);

        Page<PurchaseRequestResponseDto> result = service.getPurchaseRequests(0, 5);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(purchaseRequestRepo).findAllCustom(any(Pageable.class));
    }

    @Test
    void getPurchaseRequestItems_ShouldReturnDto_WhenFound() {
        PurchaseRequest pr = PurchaseRequest.builder()
                .id(1L)
                .code("PR-2025-00001")
                .build();
        when(purchaseRequestRepo.findById(1L)).thenReturn(Optional.of(pr));

        PrDetailInfoReviewDto dto = PrDetailInfoReviewDto.builder()
                .prCode("PR-2025-00001")
                .build();
        when(purchaseRequestMapper.toDto(pr)).thenReturn(dto);

        PrDetailInfoReviewDto result = service.getPurchaseRequestItems(1L);

        assertSame(dto, result);
        verify(purchaseRequestRepo).findById(1L);
    }

    @Test
    void getPurchaseRequestItems_ShouldThrow_WhenNotFound() {
        when(purchaseRequestRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getPurchaseRequestItems(1L));
        verify(purchaseRequestRepo).findById(1L);
    }

    @Test
    void getItem_ShouldReturnDto_WhenFound() {
        PurchaseRequestItem item = PurchaseRequestItem.builder()
                .itemId(1L)
                .partName("Part Name")
                .build();
        when(purchaseRequestItemRepo.findById(1L)).thenReturn(Optional.of(item));

        PurchaseRequestItemResponseDto dto = PurchaseRequestItemResponseDto.builder()
                .itemId(1L)
                .partName("Part Name")
                .build();
        when(purchaseRequestItemMapper.toResponseDto(item)).thenReturn(dto);

        PurchaseRequestItemResponseDto result = service.getItem(1L);

        assertSame(dto, result);
        verify(purchaseRequestItemRepo).findById(1L);
    }

    @Test
    void getItem_ShouldThrow_WhenNotFound() {
        when(purchaseRequestItemRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getItem(1L));
        verify(purchaseRequestItemRepo).findById(1L);
    }

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
                .status(PurchaseRequestStatus.PENDING)
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>())
                .build();
        when(purchaseRequestRepo.save(any(PurchaseRequest.class))).thenReturn(savedPr);

        PurchaseRequestDetailDto detailDto = PurchaseRequestDetailDto.builder()
                .purchaseRequest(PurchaseRequestResponseDto.builder()
                        .id(100L)
                        .code("PR-2025-00001")
                        .build())
                .items(new ArrayList<>())
                .build();
        when(purchaseRequestDetailMapper.toDetailDto(any(PurchaseRequest.class))).thenReturn(detailDto);

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

        assertNotNull(result);
        verify(employeeRepo).findById(1L);
        verify(partRepo).findById(10L);
        verify(partRepo).findById(11L);
        verify(codeSequenceService).generateCode("PR");
        verify(purchaseRequestItemRepo, times(2)).save(any(PurchaseRequestItem.class));
        verify(purchaseRequestRepo, atLeastOnce()).save(any(PurchaseRequest.class));
    }

    @Test
    void createRequest_ShouldThrow_WhenCreatorNotFound() {
        when(employeeRepo.findById(1L)).thenReturn(Optional.empty());

        PurchaseRequestCreateDto dto = new PurchaseRequestCreateDto();
        dto.setCreatedById(1L);

        assertThrows(RuntimeException.class,
                () -> service.createRequest(dto));
        verify(employeeRepo).findById(1L);
        verify(purchaseRequestRepo, never()).save(any());
    }

    @Test
    void createRequest_ShouldThrow_WhenPartNotFound() {
        Employee creator = Employee.builder().employeeId(1L).build();
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(creator));
        when(partRepo.findById(10L)).thenReturn(Optional.empty());

        PartItemDto item1 = new PartItemDto();
        item1.setPartId(10L);
        item1.setQuantity(2.0);

        PurchaseRequestCreateDto dto = new PurchaseRequestCreateDto();
        dto.setCreatedById(1L);
        dto.setItems(List.of(item1));

        assertThrows(RuntimeException.class,
                () -> service.createRequest(dto));
        verify(partRepo).findById(10L);
    }

    @Test
    void reviewItem_ShouldApproveAndUpdatePRStatus() {
        Employee reviewer = Employee.builder()
                .employeeId(2L)
                .fullName("Reviewer")
                .build();

        PurchaseRequest pr = PurchaseRequest.builder()
                .id(100L)
                .code("PR-2025-00001")
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        PurchaseRequestItem item = PurchaseRequestItem.builder()
                .itemId(1L)
                .purchaseRequest(pr)
                .reviewStatus(ManagerReviewStatus.PENDING)
                .build();
        pr.setItems(List.of(item));

        when(purchaseRequestItemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(purchaseRequestItemRepo.save(item)).thenReturn(item);
        when(purchaseRequestRepo.save(pr)).thenReturn(pr);

        PurchaseRequestItemResponseDto dto = PurchaseRequestItemResponseDto.builder()
                .itemId(1L)
                .reviewStatus(ManagerReviewStatus.APPROVED)
                .build();
        when(purchaseRequestItemMapper.toResponseDto(item)).thenReturn(dto);

        Account warehouseAccount = Account.builder()
                .accountId(1L)
                .employee(Employee.builder().employeeId(10L).build())
                .role(Role.WAREHOUSE)
                .build();
        when(accountRepository.findByRole(Role.WAREHOUSE)).thenReturn(List.of(warehouseAccount));

        PurchaseRequestItemResponseDto result = service.reviewItem(1L, true, "Approved", reviewer);

        assertEquals(ManagerReviewStatus.APPROVED, item.getReviewStatus());
        assertEquals(ManagerReviewStatus.APPROVED, pr.getReviewStatus());
        assertEquals("Approved", item.getNote());
        verify(purchaseRequestItemRepo).save(item);
        verify(purchaseRequestRepo).save(pr);
        verify(notificationService).createNotification(
                eq(10L), anyString(), anyString(), eq(NotificationType.PURCHASE_REQUEST),
                anyString(), anyString());
    }

    @Test
    void reviewItem_ShouldRejectAndUpdatePRStatus() {
        Employee reviewer = Employee.builder()
                .employeeId(2L)
                .fullName("Reviewer")
                .build();

        PurchaseRequest pr = PurchaseRequest.builder()
                .id(100L)
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        PurchaseRequestItem item = PurchaseRequestItem.builder()
                .itemId(1L)
                .purchaseRequest(pr)
                .reviewStatus(ManagerReviewStatus.PENDING)
                .build();
        pr.setItems(List.of(item));

        when(purchaseRequestItemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(purchaseRequestItemRepo.save(item)).thenReturn(item);
        when(purchaseRequestRepo.save(pr)).thenReturn(pr);

        PurchaseRequestItemResponseDto dto = PurchaseRequestItemResponseDto.builder()
                .itemId(1L)
                .reviewStatus(ManagerReviewStatus.REJECTED)
                .build();
        when(purchaseRequestItemMapper.toResponseDto(item)).thenReturn(dto);

        Account warehouseAccount = Account.builder()
                .accountId(1L)
                .employee(Employee.builder().employeeId(10L).build())
                .role(Role.WAREHOUSE)
                .build();
        when(accountRepository.findByRole(Role.WAREHOUSE)).thenReturn(List.of(warehouseAccount));

        PurchaseRequestItemResponseDto result = service.reviewItem(1L, false, "Rejected", reviewer);

        assertEquals(ManagerReviewStatus.REJECTED, item.getReviewStatus());
        assertEquals(ManagerReviewStatus.REJECTED, pr.getReviewStatus());
        assertEquals("Rejected", item.getNote());
        verify(purchaseRequestItemRepo).save(item);
        verify(purchaseRequestRepo).save(pr);
        verify(notificationService).createNotification(
                eq(10L), anyString(), anyString(), eq(NotificationType.PURCHASE_REQUEST),
                anyString(), anyString());
    }

    @Test
    void reviewItem_ShouldKeepPending_WhenNotAllItemsReviewed() {
        Employee reviewer = Employee.builder()
                .employeeId(2L)
                .build();

        PurchaseRequest pr = PurchaseRequest.builder()
                .id(100L)
                .reviewStatus(ManagerReviewStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        PurchaseRequestItem item1 = PurchaseRequestItem.builder()
                .itemId(1L)
                .purchaseRequest(pr)
                .reviewStatus(ManagerReviewStatus.APPROVED)
                .build();
        PurchaseRequestItem item2 = PurchaseRequestItem.builder()
                .itemId(2L)
                .purchaseRequest(pr)
                .reviewStatus(ManagerReviewStatus.PENDING)
                .build();
        pr.setItems(List.of(item1, item2));

        when(purchaseRequestItemRepo.findById(2L)).thenReturn(Optional.of(item2));
        when(purchaseRequestItemRepo.save(item2)).thenReturn(item2);
        when(purchaseRequestRepo.save(pr)).thenReturn(pr);

        PurchaseRequestItemResponseDto dto = PurchaseRequestItemResponseDto.builder()
                .itemId(2L)
                .build();
        when(purchaseRequestItemMapper.toResponseDto(item2)).thenReturn(dto);

        PurchaseRequestItemResponseDto result = service.reviewItem(2L, true, "Approved", reviewer);

        assertEquals(ManagerReviewStatus.PENDING, pr.getReviewStatus());
        verify(purchaseRequestRepo).save(pr);
        verify(notificationService, never()).createNotification(anyLong(), anyString(), anyString(),
                any(), anyString(), anyString());
    }

    @Test
    void reviewItem_ShouldThrow_WhenItemNotFound() {
        when(purchaseRequestItemRepo.findById(1L)).thenReturn(Optional.empty());

        Employee reviewer = Employee.builder().employeeId(2L).build();

        assertThrows(ResourceNotFoundException.class,
                () -> service.reviewItem(1L, true, "Note", reviewer));
        verify(purchaseRequestItemRepo).findById(1L);
        verify(purchaseRequestItemRepo, never()).save(any());
    }
}

