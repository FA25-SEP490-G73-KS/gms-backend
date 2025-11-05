package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.*;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceQuotationServiceImpl implements PriceQuotationService {

    private final PriceQuotationRepository quotationRepository;
    private final PartRepository partRepository;
    private final PriceQuotationMapper priceQuotationMapper;

    @Override
    public PriceQuotationResponseDto updateQuotationItems(Long quotationId, PriceQuotationRequestDto dto) {

        PriceQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá ID: " + quotationId));

        quotation.setUpdatedAt(LocalDateTime.now());
        List<PriceQuotationItem> existingItems = quotation.getItems();

        Set<Long> requestItemIds = dto.getItems().stream()
                .map(PriceQuotationItemRequestDto::getPriceQuotationItemId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (var itemDto : dto.getItems()) {
            PriceQuotationItem item = existingItems.stream()
                    .filter(i -> i.getPriceQuotationItemId() != null &&
                            i.getPriceQuotationItemId().equals(itemDto.getPriceQuotationItemId()))
                    .findFirst()
                    .orElse(null);

            if (item != null) {
                applyItemUpdates(item, itemDto);
            } else {
                PriceQuotationItem newItem = new PriceQuotationItem();
                newItem.setPriceQuotation(quotation);
                applyItemUpdates(newItem, itemDto);
                existingItems.add(newItem);
            }
        }

        // Các item cũ không có trong request sẽ được giữ nguyên, không xóa
        quotation.setEstimateAmount(dto.getEstimateAmount());
        quotation.setStatus(PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM);

        quotationRepository.save(quotation);
        return priceQuotationMapper.toResponseDto(quotation);
    }


    @Override
    public PriceQuotationResponseDto getById(Long id) {
        return priceQuotationMapper.toResponseDto(quotationRepository.findById(id).orElse(null));
    }


    private void applyItemUpdates(PriceQuotationItem item, PriceQuotationItemRequestDto dto) {
        item.setItemName(dto.getItemName());
        item.setItemType(dto.getType());
        item.setQuantity(dto.getQuantity());
        item.setUnit(dto.getUnit());
        item.setUnitPrice(dto.getUnitPrice());
        item.setTotalPrice(dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        item.setWarehouseReviewStatus(WarehouseReviewStatus.PENDING);
        item.setWarehouseNote(null);

        // Xử lý riêng cho PART
        if (dto.getType() == PriceQuotationItemType.PART) {
            Part part = null;

            if (dto.getPartId() != null) {
                part = partRepository.findById(dto.getPartId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Không tìm thấy linh kiện ID: " + dto.getPartId()));
                item.setPart(part);
            }

            if (part == null) {
                item.setInventoryStatus(PriceQuotationItemStatus.UNKNOWN);
            } else {
                double availableQty = Optional.ofNullable(part.getQuantityInStock()).orElse(0.0)
                        - Optional.ofNullable(part.getReservedQuantity()).orElse(0.0);
                item.setInventoryStatus(
                        availableQty >= item.getQuantity()
                                ? PriceQuotationItemStatus.AVAILABLE
                                : PriceQuotationItemStatus.OUT_OF_STOCK
                );
            }
        } else {
            item.setPart(null);
            item.setInventoryStatus(null);
        }
    }

}
