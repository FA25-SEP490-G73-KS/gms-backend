package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.dto.request.UnitRequestDto;
import fpt.edu.vn.gms.dto.response.UnitResponseDto;
import fpt.edu.vn.gms.service.UnitService;
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

@WebMvcTest(UnitController.class)
class UnitControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UnitService unitService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAll_ShouldReturnPagedDtos() throws Exception {
        UnitResponseDto dto = new UnitResponseDto(1L, "Cái");
        Page<UnitResponseDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 5), 1);
        when(unitService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content[0].id").value(1L))
                .andExpect(jsonPath("$.result.content[0].name").value("Cái"));
    }

    @Test
    void getById_ShouldReturnDto() throws Exception {
        UnitResponseDto dto = new UnitResponseDto(1L, "Cái");
        when(unitService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/units/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.name").value("Cái"));
    }

    @Test
    void create_ShouldCreateAndReturnDto() throws Exception {
        UnitRequestDto requestDto = new UnitRequestDto("Cái");
        UnitResponseDto responseDto = new UnitResponseDto(1L, "Cái");
        when(unitService.create(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.name").value("Cái"));
    }

    @Test
    void update_ShouldUpdateAndReturnDto() throws Exception {
        UnitRequestDto requestDto = new UnitRequestDto("Cái mới");
        UnitResponseDto responseDto = new UnitResponseDto(1L, "Cái mới");
        when(unitService.update(1L, requestDto)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/units/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.name").value("Cái mới"));
    }

    @Test
    void delete_ShouldDeleteUnit() throws Exception {
        doNothing().when(unitService).delete(1L);

        mockMvc.perform(delete("/api/units/1"))
                .andExpect(status().isNoContent());

        verify(unitService).delete(1L);
    }
}

