package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.entity.Market;
import fpt.edu.vn.gms.service.MarketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarketController.class)
class MarketControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MarketService marketService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAllMarkets_ShouldReturnListOfMarkets() throws Exception {
        Market market1 = Market.builder()
                .id(1L)
                .name("OEM")
                .build();
        Market market2 = Market.builder()
                .id(2L)
                .name("Aftermarket")
                .build();
        when(marketService.getAll()).thenReturn(List.of(market1, market2));

        mockMvc.perform(get("/api/markets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].id").value(1L))
                .andExpect(jsonPath("$.result[0].name").value("OEM"));
    }

    @Test
    void getMarketById_ShouldReturnMarket() throws Exception {
        Market market = Market.builder()
                .id(1L)
                .name("OEM")
                .build();
        when(marketService.getById(1L)).thenReturn(market);

        mockMvc.perform(get("/api/markets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.name").value("OEM"));
    }
}

