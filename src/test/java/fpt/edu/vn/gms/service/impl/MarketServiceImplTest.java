package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.entity.Market;
import fpt.edu.vn.gms.repository.MarketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MarketServiceImplTest extends BaseServiceTest {

  @Mock
  private MarketRepository marketRepo;

  @InjectMocks
  private MarketServiceImpl marketServiceImpl;

  @Test
  void getAll_WhenMarketsExist_ShouldReturnMarketList() {
    Market market1 = Market.builder().id(1L).name("Market 1").build();
    Market market2 = Market.builder().id(2L).name("Market 2").build();
    when(marketRepo.findAll()).thenReturn(List.of(market1, market2));

    List<Market> result = marketServiceImpl.getAll();

    assertEquals(2, result.size());
    assertEquals("Market 1", result.get(0).getName());
    assertEquals("Market 2", result.get(1).getName());
    verify(marketRepo).findAll();
  }

  @Test
  void getAll_WhenNoMarketsExist_ShouldReturnEmptyList() {
    when(marketRepo.findAll()).thenReturn(List.of());

    List<Market> result = marketServiceImpl.getAll();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(marketRepo).findAll();
  }
}
