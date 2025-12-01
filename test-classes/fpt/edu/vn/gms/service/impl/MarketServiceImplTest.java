package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.Market;
import fpt.edu.vn.gms.repository.MarketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketServiceImplTest {

    @Mock
    MarketRepository marketRepo;

    @InjectMocks
    MarketServiceImpl service;

    @Test
    void getAll_ShouldReturnAllMarkets() {
        Market m1 = Market.builder().id(1L).name("VN").build();
        Market m2 = Market.builder().id(2L).name("US").build();
        when(marketRepo.findAll()).thenReturn(List.of(m1, m2));

        List<Market> result = service.getAll();

        assertEquals(2, result.size());
        assertSame(m1, result.get(0));
        assertSame(m2, result.get(1));
        verify(marketRepo).findAll();
    }

    @Test
    void getById_ShouldReturnMarket_WhenFound() {
        Market m1 = Market.builder().id(1L).name("VN").build();
        when(marketRepo.findById(1L)).thenReturn(Optional.of(m1));

        Market result = service.getById(1L);

        assertSame(m1, result);
        verify(marketRepo).findById(1L);
    }

    @Test
    void getById_ShouldReturnNull_WhenNotFound() {
        when(marketRepo.findById(1L)).thenReturn(Optional.empty());

        Market result = service.getById(1L);

        assertNull(result);
        verify(marketRepo).findById(1L);
    }
}


