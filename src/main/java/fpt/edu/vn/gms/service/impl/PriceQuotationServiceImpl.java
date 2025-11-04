package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.PriceQuotationItemType;
import fpt.edu.vn.gms.common.PriceQuotationStatus;
import fpt.edu.vn.gms.common.PurchaseRequestStatus;
import fpt.edu.vn.gms.common.WarehouseReviewStatus;
import fpt.edu.vn.gms.dto.request.PriceQuotationItemRequestDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.repository.PartRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.repository.PurchaseRequestRepository;
import fpt.edu.vn.gms.service.PriceQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceQuotationServiceImpl implements PriceQuotationService {

    private final PriceQuotationRepository priceQuotationRepository;
    private final PartRepository partRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PriceQuotationMapper priceQuotationMapper;


    @Override
    public PriceQuotationResponseDto updateQuotationItems(PriceQuotationRequestDto dto) {

        // Luôn luôn tìm báo giá đã có
        PriceQuotation quotation = priceQuotationRepository.findById(dto.getPriceQuotationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy báo giá ID: " + dto.getPriceQuotationId()));

        // Khi update quotation status -> DRAFT
        quotation.setStatus(PriceQuotationStatus.DRAFT);
        quotation.setEstimateAmount(dto.getEstimateAmount());

        // Duyệt từng item trong request
        if (dto.getItems() != null) {
            // Lấy danh sách ID item có trong request
            Set<Long> requestItemIds = dto.getItems().stream()
                    .map(PriceQuotationItemRequestDto::getPriceQuotationItemId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Xóa các item cũ không còn trong request
            quotation.getItems().removeIf(existing ->
                    existing.getPriceQuotationItemId() != null &&
                            !requestItemIds.contains(existing.getPriceQuotationItemId())
            );

            // Cập nhật hoặc thêm mới
            for (PriceQuotationItemRequestDto itemDto : dto.getItems()) {
                PriceQuotationItem item;

                if (itemDto.getPriceQuotationItemId() != null) {
                    // Update item cũ
                    item = quotation.getItems().stream()
                            .filter(i -> i.getPriceQuotationItemId().equals(itemDto.getPriceQuotationItemId()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Không tìm thấy item ID: " + itemDto.getPriceQuotationItemId()));

                    boolean changed = !Objects.equals(item.getQuantity(), itemDto.getQuantity())
                            || !Objects.equals(item.getUnitPrice(), itemDto.getUnitPrice())
                            || !Objects.equals(item.getTotalPrice(), itemDto.getTotalPrice());

                    if (changed) {
                        item.setWarehouseReviewStatus(WarehouseReviewStatus.PENDING);
                    }

                } else {
                    // Thêm item mới
                    item = new PriceQuotationItem();
                    item.setPriceQuotation(quotation);
                    item.setWarehouseReviewStatus(WarehouseReviewStatus.PENDING);
                    quotation.getItems().add(item);
                }

                // --- Cập nhật dữ liệu chung ---
                if (itemDto.getPartId() != null) {
                    Part part = partRepository.findById(itemDto.getPartId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Part không tồn tại ID: " + itemDto.getPartId()));
                    item.setPart(part);
                } else {
                    item.setPart(null);
                }

                item.setItemName(itemDto.getItemName());
                item.setItemType(itemDto.getType());
                item.setQuantity(itemDto.getQuantity());
                item.setStatus(itemDto.getStatus());
                item.setItemType(itemDto.getType());
                item.setUnitPrice(itemDto.getUnitPrice());
                item.setTotalPrice(itemDto.getTotalPrice());
            }
        }

        PriceQuotation saved = priceQuotationRepository.save(quotation);

        // Tạo PurchaseRequest cho các item là PART
        Set<PurchaseRequestItem> requestItems = new HashSet<>();

        quotation.getItems().stream()
                .filter(item -> item.getItemType() == PriceQuotationItemType.PART)
                .forEach(item -> {
                    Part part = null;
                    String note = null;

                    // Nếu partId có sẵn thì tìm trong DB
                    if (item.getPart() != null && item.getPart().getPartId() != null) {
                        part = partRepository.findById(item.getPart().getPartId())
                                .orElseThrow(() -> new RuntimeException("Part not found with id: " + item.getPart().getPartId()));
                    }

                    // Nếu partId = null → linh kiện chưa có trong hệ thống (UNKNOWN)
                    else {
                        note = "[Chưa có trong kho] " + item.getItemName() + item.getQuantity() + item.getUnitPrice();
                    }

                    PurchaseRequestItem reqItem = PurchaseRequestItem.builder()
                            .quotationItem(item)
                            .part(part)
                            .quantity(item.getQuantity())
                            .status(WarehouseReviewStatus.PENDING)
                            .note(note)
                            .build();

                    requestItems.add(reqItem);
                });

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!requestItems.isEmpty()) {
            PurchaseRequest purchaseRequest = PurchaseRequest.builder()
                    .status(PurchaseRequestStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .createdBy(auth.getName())
                    .items(requestItems)
                    .build();

            // Gắn liên kết ngược
            requestItems.forEach(i -> i.setPurchaseRequest(purchaseRequest));

            purchaseRequestRepository.save(purchaseRequest);
        }

        return priceQuotationMapper.toResponseDto(saved);
    }

    @Override
    public PriceQuotationResponseDto getById(Long id) {
        return priceQuotationMapper.toResponseDto(priceQuotationRepository.findById(id).orElse(null));
    }
}
