package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import fpt.edu.vn.gms.mapper.PriceQuotationItemMapper;
import fpt.edu.vn.gms.repository.PriceQuotationItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuotationItemServiceImplTest extends BaseServiceTest {

  @Mock
  private PriceQuotationItemRepository itemRepo;

  @Mock
  private PriceQuotationItemMapper itemMapper;

  @InjectMocks
  private QuotationItemServiceImpl quotationItemServiceImpl;

  @Test
  void getQuotationItem_WhenItemExists_ShouldReturnResponseDto() {
    Long itemId = 1L;
    PriceQuotationItem item = PriceQuotationItem.builder().priceQuotationItemId(itemId).build();
    PriceQuotationItemResponseDto dto = PriceQuotationItemResponseDto.builder().priceQuotationItemId(itemId).build();

    when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));
    when(itemMapper.toResponseDto(item)).thenReturn(dto);

    PriceQuotationItemResponseDto result = quotationItemServiceImpl.getQuotationItem(itemId);

    assertNotNull(result);
    assertEquals(itemId, result.getPriceQuotationItemId());
    verify(itemRepo).findById(itemId);
    verify(itemMapper).toResponseDto(item);
  }

  @Test
  void getQuotationItem_WhenItemNotFound_ShouldThrowException() {
    Long itemId = 2L;
    when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

    assertThrows(Exception.class, () -> quotationItemServiceImpl.getQuotationItem(itemId));
    verify(itemRepo).findById(itemId);
    verify(itemMapper, never()).toResponseDto(any());
  }
}
