package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.repository.PriceQuotationItemRepository;
import fpt.edu.vn.gms.service.QuotaitonItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuotationItemServiceImpl implements QuotaitonItemService {

    private final PriceQuotationItemRepository itemRepo;

    private final PriceQuotationItemMapper itemMapper;

    @Override
    public PriceQuotationItemResponseDto getQuotationItem(Long itemId) {

        PriceQuotationItem item = itemRepo.findById(itemId).orElseThrow();

        return itemMapper.toResponseDto(item);
    }
}
