package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.entity.Brand;
import fpt.edu.vn.gms.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BrandController.class)
class BrandControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BrandService brandService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAllBrands_ShouldReturnListOfBrands() throws Exception {
        Brand brand1 = Brand.builder()
                .brandId(1L)
                .name("Toyota")
                .build();
        Brand brand2 = Brand.builder()
                .brandId(2L)
                .name("Honda")
                .build();
        when(brandService.getAll()).thenReturn(List.of(brand1, brand2));

        mockMvc.perform(get("/api/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].brandId").value(1L))
                .andExpect(jsonPath("$.result[0].name").value("Toyota"));
    }

    @Test
    void getBrandById_ShouldReturnBrand_WhenFound() throws Exception {
        Brand brand = Brand.builder()
                .brandId(1L)
                .name("Toyota")
                .build();
        when(brandService.getById(1L)).thenReturn(brand);

        mockMvc.perform(get("/api/brands/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.brandId").value(1L))
                .andExpect(jsonPath("$.result.name").value("Toyota"));
    }

    @Test
    void getBrandById_ShouldReturnNotFound_WhenNotFound() throws Exception {
        when(brandService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/brands/999"))
                .andExpect(status().isNotFound());
    }
}

