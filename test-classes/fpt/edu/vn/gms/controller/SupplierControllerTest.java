package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.dto.request.SupplierRequestDto;
import fpt.edu.vn.gms.dto.response.SupplierResponseDto;
import fpt.edu.vn.gms.service.SupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplierController.class)
class SupplierControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SupplierService supplierService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAllSuppliers_ShouldReturnPagedDtos() throws Exception {
        SupplierResponseDto dto = SupplierResponseDto.builder()
                .id(1L)
                .name("Supplier 1")
                .build();
        Page<SupplierResponseDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 6), 1);
        when(supplierService.getAllSuppliers(0, 6)).thenReturn(page);

        mockMvc.perform(get("/api/suppliers")
                        .param("page", "0")
                        .param("size", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Supplier 1"));
    }

    @Test
    void getSupplierById_ShouldReturnDto() throws Exception {
        SupplierResponseDto dto = SupplierResponseDto.builder()
                .id(1L)
                .name("Supplier 1")
                .build();
        when(supplierService.getSupplierById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/suppliers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Supplier 1"));
    }

    @Test
    void createSupplier_ShouldCreateAndReturnDto() throws Exception {
        SupplierRequestDto requestDto = SupplierRequestDto.builder()
                .name("New Supplier")
                .phone("0912345678")
                .email("supplier@example.com")
                .build();
        SupplierResponseDto responseDto = SupplierResponseDto.builder()
                .id(1L)
                .name("New Supplier")
                .isActive(true)
                .build();
        when(supplierService.createSupplier(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Supplier"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void updateSupplier_ShouldUpdateAndReturnDto() throws Exception {
        SupplierRequestDto requestDto = SupplierRequestDto.builder()
                .name("Updated Supplier")
                .build();
        SupplierResponseDto responseDto = SupplierResponseDto.builder()
                .id(1L)
                .name("Updated Supplier")
                .build();
        when(supplierService.updateSupplier(1L, requestDto)).thenReturn(responseDto);

        mockMvc.perform(put("/api/suppliers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Supplier"));
    }

    @Test
    void deleteSupplier_ShouldSoftDelete() throws Exception {
        doNothing().when(supplierService).softDeleteSupplier(1L);

        mockMvc.perform(delete("/api/suppliers/1"))
                .andExpect(status().isNoContent());

        verify(supplierService).softDeleteSupplier(1L);
    }
}

