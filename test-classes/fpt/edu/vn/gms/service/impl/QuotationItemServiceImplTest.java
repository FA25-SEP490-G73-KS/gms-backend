package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.repository.PriceQuotationItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotationItemServiceImplTest {

    @Mock
    PriceQuotationItemRepository itemRepo;
    @Mock
    PriceQuotationItemMapper itemMapper;

    @InjectMocks
    QuotationItemServiceImpl service;

    @Test
    void getQuotationItem_ShouldReturnDto_WhenFound() {
        PriceQuotationItem item = PriceQuotationItem.builder()
                .priceQuotationItemId(1L)
                .build();
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        PriceQuotationItemResponseDto dto = PriceQuotationItemResponseDto.builder()
                .priceQuotationItemId(1L)
                .build();
        when(itemMapper.toResponseDto(item)).thenReturn(dto);

        PriceQuotationItemResponseDto result = service.getQuotationItem(1L);

        assertSame(dto, result);
        verify(itemRepo).findById(1L);
        verify(itemMapper).toResponseDto(item);
    }

    @Test
    void getQuotationItem_ShouldThrow_WhenNotFound() {
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> service.getQuotationItem(1L));
        verify(itemRepo).findById(1L);
        verify(itemMapper, never()).toResponseDto(any());
    }
}

