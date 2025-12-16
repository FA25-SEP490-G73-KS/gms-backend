package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.repository.PriceQuotationItemRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.service.QuotaitonItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuotationItemServiceImpl implements QuotaitonItemService {

    private final PriceQuotationItemRepository itemRepo;

    private final PriceQuotationItemMapper itemMapper;

    private final PriceQuotationRepository quotationRepository;

    @Override
    public PriceQuotationItemResponseDto getQuotationItem(Long itemId) {

        PriceQuotationItem item = itemRepo.findById(itemId).orElseThrow();

        return itemMapper.toResponseDto(item);
    }

    @Override
    @Transactional
    public void deleteQuotationItem(Long itemId) {
        PriceQuotationItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mục báo giá với ID: " + itemId));

        PriceQuotation quotation = item.getPriceQuotation();

        if (quotation == null) {
            throw new ResourceNotFoundException("Không tìm thấy báo giá cho mục này");
        }

        // Xóa item khỏi collection của quotation
        // Với orphanRemoval = true, Hibernate sẽ tự động xóa item khi save quotation
        quotation.getItems().remove(item);

        // Tính toán lại tổng dự kiến dựa trên items còn lại
        BigDecimal totalEstimate = quotation.getItems().stream()
                .map(i -> Optional.ofNullable(i.getTotalPrice()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        quotation.setEstimateAmount(totalEstimate);
        quotation.setUpdatedAt(LocalDateTime.now());

        // Save quotation - Hibernate sẽ tự động xóa item do orphanRemoval = true
        quotationRepository.save(quotation);
    }
}
