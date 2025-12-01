package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.entity.PartCategory;
import fpt.edu.vn.gms.service.PartCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PartCategoryController.class)
class PartCategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PartCategoryService partCategoryService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAll_ShouldReturnListOfCategories() throws Exception {
        PartCategory category1 = PartCategory.builder()
                .id(1L)
                .name("Engine")
                .build();
        PartCategory category2 = PartCategory.builder()
                .id(2L)
                .name("Brake")
                .build();
        when(partCategoryService.getAll()).thenReturn(List.of(category1, category2));

        mockMvc.perform(get("/api/part-category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].id").value(1L))
                .andExpect(jsonPath("$.result[0].name").value("Engine"))
                .andExpect(jsonPath("$.result[1].id").value(2L))
                .andExpect(jsonPath("$.result[1].name").value("Brake"));
    }

    @Test
    void getCategoryById_ShouldReturnCategory() throws Exception {
        PartCategory category = PartCategory.builder()
                .id(1L)
                .name("Engine")
                .build();
        when(partCategoryService.getById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/part-category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.name").value("Engine"));
    }

    @Test
    void getCategoryById_ShouldReturnNull_WhenNotFound() throws Exception {
        when(partCategoryService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/part-category/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").isEmpty());
    }
}

