package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.dto.request.DiscountPolicyRequestDto;
import fpt.edu.vn.gms.dto.response.DiscountPolicyResponseDto;
import fpt.edu.vn.gms.service.DiscountPolicyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiscountPolicyController.class)
class DiscountPolicyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DiscountPolicyService discountPolicyService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAll_ShouldReturnPagedDtos() throws Exception {
        DiscountPolicyResponseDto dto = DiscountPolicyResponseDto.builder()
                .discountPolicyId(1L)
                .discountRate(new BigDecimal("0.1"))
                .build();
        Page<DiscountPolicyResponseDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 5), 1);
        when(discountPolicyService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/discount-policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content[0].discountPolicyId").value(1L));
    }

    @Test
    void getById_ShouldReturnDto() throws Exception {
        DiscountPolicyResponseDto dto = DiscountPolicyResponseDto.builder()
                .discountPolicyId(1L)
                .discountRate(new BigDecimal("0.1"))
                .build();
        when(discountPolicyService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/discount-policies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.discountPolicyId").value(1L));
    }

    @Test
    void create_ShouldCreateAndReturnDto() throws Exception {
        DiscountPolicyRequestDto requestDto = DiscountPolicyRequestDto.builder()
                .discountRate(new BigDecimal("0.15"))
                .build();
        DiscountPolicyResponseDto responseDto = DiscountPolicyResponseDto.builder()
                .discountPolicyId(1L)
                .discountRate(new BigDecimal("0.15"))
                .build();
        when(discountPolicyService.create(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/discount-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.discountPolicyId").value(1L));
    }

    @Test
    void update_ShouldUpdateAndReturnDto() throws Exception {
        DiscountPolicyRequestDto requestDto = DiscountPolicyRequestDto.builder()
                .discountRate(new BigDecimal("0.2"))
                .build();
        DiscountPolicyResponseDto responseDto = DiscountPolicyResponseDto.builder()
                .discountPolicyId(1L)
                .discountRate(new BigDecimal("0.2"))
                .build();
        when(discountPolicyService.update(1L, requestDto)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/discount-policies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.discountPolicyId").value(1L));
    }

    @Test
    void delete_ShouldDeletePolicy() throws Exception {
        doNothing().when(discountPolicyService).delete(1L);

        mockMvc.perform(delete("/api/discount-policies/1"))
                .andExpect(status().isNoContent());

        verify(discountPolicyService).delete(1L);
    }
}

