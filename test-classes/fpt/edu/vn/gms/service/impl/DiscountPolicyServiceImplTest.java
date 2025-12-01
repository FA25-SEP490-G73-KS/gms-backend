package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.dto.request.DiscountPolicyRequestDto;
import fpt.edu.vn.gms.dto.response.DiscountPolicyResponseDto;
import fpt.edu.vn.gms.entity.DiscountPolicy;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.DiscountPolicyMapper;
import fpt.edu.vn.gms.repository.DiscountPolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountPolicyServiceImplTest {

    @Mock
    DiscountPolicyRepository discountPolicyRepository;
    @Mock
    DiscountPolicyMapper discountPolicyMapper;

    @InjectMocks
    DiscountPolicyServiceImpl service;

    @Test
    void getAll_ShouldReturnMappedPage() {
        Pageable pageable = Pageable.ofSize(5).withPage(0);
        DiscountPolicy entity = DiscountPolicy.builder().discountPolicyId(1L).build();
        DiscountPolicyResponseDto dto = DiscountPolicyResponseDto.builder()
                .discountPolicyId(1L)
                .build();

        Page<DiscountPolicy> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(discountPolicyRepository.findAll(pageable)).thenReturn(page);
        when(discountPolicyMapper.toResponseDto(entity)).thenReturn(dto);

        Page<DiscountPolicyResponseDto> result = service.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(discountPolicyRepository).findAll(pageable);
    }

    @Test
    void getById_ShouldReturnDto_WhenFound() {
        DiscountPolicy entity = DiscountPolicy.builder().discountPolicyId(1L).build();
        DiscountPolicyResponseDto dto = DiscountPolicyResponseDto.builder()
                .discountPolicyId(1L)
                .build();

        when(discountPolicyRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(discountPolicyMapper.toResponseDto(entity)).thenReturn(dto);

        DiscountPolicyResponseDto result = service.getById(1L);

        assertSame(dto, result);
        verify(discountPolicyRepository).findById(1L);
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        when(discountPolicyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getById(1L));
    }

    @Test
    void create_ShouldSaveAndReturnDto() {
        DiscountPolicyRequestDto req = DiscountPolicyRequestDto.builder()
                .loyaltyLevel(fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel.BRONZE)
                .discountRate(new BigDecimal("5"))
                .requiredSpending(new BigDecimal("1000000"))
                .description("Bronze level")
                .build();

        DiscountPolicy entityToSave = DiscountPolicy.builder()
                .loyaltyLevel(req.getLoyaltyLevel())
                .discountRate(req.getDiscountRate())
                .requiredSpending(req.getRequiredSpending())
                .description(req.getDescription())
                .build();

        DiscountPolicy saved = DiscountPolicy.builder()
                .discountPolicyId(1L)
                .loyaltyLevel(req.getLoyaltyLevel())
                .discountRate(req.getDiscountRate())
                .requiredSpending(req.getRequiredSpending())
                .description(req.getDescription())
                .build();

        when(discountPolicyRepository.save(any(DiscountPolicy.class))).thenReturn(saved);
        DiscountPolicyResponseDto dto = DiscountPolicyResponseDto.builder()
                .discountPolicyId(1L)
                .build();
        when(discountPolicyMapper.toResponseDto(saved)).thenReturn(dto);

        DiscountPolicyResponseDto result = service.create(req);

        assertSame(dto, result);
        verify(discountPolicyRepository).save(any(DiscountPolicy.class));
    }

    @Test
    void update_ShouldModifyEntityAndReturnDto_WhenFound() {
        DiscountPolicy existing = DiscountPolicy.builder()
                .discountPolicyId(1L)
                .discountRate(new BigDecimal("5"))
                .build();

        DiscountPolicyRequestDto req = DiscountPolicyRequestDto.builder()
                .loyaltyLevel(CustomerLoyaltyLevel.SLIVER)
                .discountRate(new BigDecimal("10"))
                .requiredSpending(new BigDecimal("2000000"))
                .description("Silver level")
                .build();

        when(discountPolicyRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(discountPolicyRepository.save(existing)).thenReturn(existing);
        DiscountPolicyResponseDto dto = DiscountPolicyResponseDto.builder()
                .discountPolicyId(1L)
                .build();
        when(discountPolicyMapper.toResponseDto(existing)).thenReturn(dto);

        DiscountPolicyResponseDto result = service.update(1L, req);

        assertSame(dto, result);
        assertEquals(req.getDiscountRate(), existing.getDiscountRate());
        assertEquals(req.getRequiredSpending(), existing.getRequiredSpending());
        assertEquals(req.getDescription(), existing.getDescription());
        verify(discountPolicyRepository).save(existing);
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        DiscountPolicyRequestDto req = DiscountPolicyRequestDto.builder().build();
        when(discountPolicyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, req));
    }

    @Test
    void delete_ShouldDelete_WhenExists() {
        when(discountPolicyRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(discountPolicyRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrow_WhenNotExists() {
        when(discountPolicyRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1L));
        verify(discountPolicyRepository, never()).deleteById(anyLong());
    }
}


