package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.WarehouseReviewStatus;
import fpt.edu.vn.gms.dto.request.PriceQuotationItemRequestDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.repository.PartRepository;
import fpt.edu.vn.gms.repository.PriceQuotationItemRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.service.PriceQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceQuotationServiceImpl implements PriceQuotationService {

    private final PriceQuotationRepository priceQuotationRepository;
    private final PartRepository partRepository;
    private final PriceQuotationItemRepository priceQuotationItemRepository;
    private final PriceQuotationMapper priceQuotationMapper;


    @Override
    public PriceQuotationResponseDto updateQuotationItems(PriceQuotationRequestDto dto) {
        // 1️⃣ Luôn luôn tìm báo giá đã có
        PriceQuotation quotation = priceQuotationRepository.findById(dto.getPriceQuotationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy báo giá ID: " + dto.getPriceQuotationId()));

        quotation.setEstimateAmount(dto.getEstimateAmount());

        // 2️⃣ Duyệt từng item trong request
        if (dto.getItems() != null) {
            // Lấy danh sách ID item có trong request
            Set<Long> requestItemIds = dto.getItems().stream()
                    .map(PriceQuotationItemRequestDto::getPriceQuotationItemId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 3️⃣ Xóa các item cũ không còn trong request
            quotation.getItems().removeIf(existing ->
                    existing.getPriceQuotationItemId() != null &&
                            !requestItemIds.contains(existing.getPriceQuotationItemId())
            );

            // 4️⃣ Cập nhật hoặc thêm mới
            for (PriceQuotationItemRequestDto itemDto : dto.getItems()) {
                PriceQuotationItem item;

                if (itemDto.getPriceQuotationItemId() != null) {
                    // 🔁 Update item cũ
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
                    // 🆕 Thêm item mới
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
        return priceQuotationMapper.toResponseDto(saved);
    }

    @Override
    public PriceQuotationResponseDto getById(Long id) {
        return priceQuotationMapper.toResponseDto(priceQuotationRepository.findById(id).orElse(null));
    }
}
