package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.dto.request.DiscountPolicyRequestDto;
import fpt.edu.vn.gms.dto.response.DiscountPolicyResponseDto;
import fpt.edu.vn.gms.entity.DiscountPolicy;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.DiscountPolicyMapper;
import fpt.edu.vn.gms.repository.DiscountPolicyRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DiscountPolicyServiceImplTest extends BaseServiceTest {

  @Mock
  private DiscountPolicyRepository discountPolicyRepository;
  @Mock
  private DiscountPolicyMapper discountPolicyMapper;

  @InjectMocks
  private DiscountPolicyServiceImpl discountPolicyServiceImpl;

  @Test
  void getAll_WhenPoliciesExist_ShouldReturnPagedResponseDtos() {
    DiscountPolicy policy = DiscountPolicy.builder().discountPolicyId(1L).build();
    DiscountPolicyResponseDto dto = DiscountPolicyResponseDto.builder().discountPolicyId(1L).build();
    Page<DiscountPolicy> page = new PageImpl<>(java.util.List.of(policy));
    when(discountPolicyRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(discountPolicyMapper.toResponseDto(policy)).thenReturn(dto);

    Page<DiscountPolicyResponseDto> result = discountPolicyServiceImpl.getAll(PageRequest.of(0, 10));

    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getDiscountPolicyId());
  }

  @Test
  void getById_WhenPolicyExists_ShouldReturnResponseDto() {
    DiscountPolicy policy = DiscountPolicy.builder().discountPolicyId(2L).build();
    DiscountPolicyResponseDto dto = DiscountPolicyResponseDto.builder().discountPolicyId(2L).build();
    when(discountPolicyRepository.findById(2L)).thenReturn(Optional.of(policy));
    when(discountPolicyMapper.toResponseDto(policy)).thenReturn(dto);

    DiscountPolicyResponseDto result = discountPolicyServiceImpl.getById(2L);

    assertEquals(2L, result.getDiscountPolicyId());
  }

  @Test
  void getById_WhenPolicyNotFound_ShouldThrowResourceNotFoundException() {
    when(discountPolicyRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> discountPolicyServiceImpl.getById(99L));
  }

  @Test
  void create_WhenValidRequest_ShouldSaveAndReturnResponseDto() {
    DiscountPolicyRequestDto req = DiscountPolicyRequestDto.builder()
        .loyaltyLevel(CustomerLoyaltyLevel.GOLD)
        .discountRate(BigDecimal.valueOf(0.1))
        .requiredSpending(BigDecimal.valueOf(1000000))
        .description("Gold policy")
        .build();
    DiscountPolicy saved = DiscountPolicy.builder()
        .discountPolicyId(3L)
        .loyaltyLevel(CustomerLoyaltyLevel.GOLD)
        .discountRate(BigDecimal.valueOf(0.1))
        .requiredSpending(BigDecimal.valueOf(1000000))
        .description("Gold policy")
        .build();
    DiscountPolicyResponseDto dto = DiscountPolicyResponseDto.builder().discountPolicyId(3L).build();

    when(discountPolicyRepository.save(any(DiscountPolicy.class))).thenReturn(saved);
    when(discountPolicyMapper.toResponseDto(saved)).thenReturn(dto);

    DiscountPolicyResponseDto result = discountPolicyServiceImpl.create(req);

    assertEquals(3L, result.getDiscountPolicyId());
    verify(discountPolicyRepository).save(any(DiscountPolicy.class));
  }

  @Test
  void update_WhenPolicyExists_ShouldUpdateAndReturnResponseDto() {
    DiscountPolicy existing = DiscountPolicy.builder()
        .discountPolicyId(4L)
        .loyaltyLevel(CustomerLoyaltyLevel.SLIVER)
        .discountRate(BigDecimal.valueOf(0.05))
        .requiredSpending(BigDecimal.valueOf(500000))
        .description("Silver policy")
        .build();
    DiscountPolicyRequestDto req = DiscountPolicyRequestDto.builder()
        .loyaltyLevel(CustomerLoyaltyLevel.GOLD)
        .discountRate(BigDecimal.valueOf(0.2))
        .requiredSpending(BigDecimal.valueOf(2000000))
        .description("Gold policy")
        .build();
    DiscountPolicy updated = DiscountPolicy.builder()
        .discountPolicyId(4L)
        .loyaltyLevel(CustomerLoyaltyLevel.GOLD)
        .discountRate(BigDecimal.valueOf(0.2))
        .requiredSpending(BigDecimal.valueOf(2000000))
        .description("Gold policy")
        .build();
    DiscountPolicyResponseDto dto = DiscountPolicyResponseDto.builder().discountPolicyId(4L).build();

    when(discountPolicyRepository.findById(4L)).thenReturn(Optional.of(existing));
    when(discountPolicyRepository.save(existing)).thenReturn(updated);
    when(discountPolicyMapper.toResponseDto(updated)).thenReturn(dto);

    DiscountPolicyResponseDto result = discountPolicyServiceImpl.update(4L, req);

    assertEquals(4L, result.getDiscountPolicyId());
    assertEquals(CustomerLoyaltyLevel.GOLD, existing.getLoyaltyLevel());
    verify(discountPolicyRepository).save(existing);
  }

  @Test
  void update_WhenPolicyNotFound_ShouldThrowResourceNotFoundException() {
    DiscountPolicyRequestDto req = DiscountPolicyRequestDto.builder().build();
    when(discountPolicyRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> discountPolicyServiceImpl.update(99L, req));
  }

  @Test
  void delete_WhenPolicyExists_ShouldDeleteById() {
    when(discountPolicyRepository.existsById(5L)).thenReturn(true);

    discountPolicyServiceImpl.delete(5L);

    verify(discountPolicyRepository).deleteById(5L);
  }

  @Test
  void delete_WhenPolicyNotFound_ShouldThrowResourceNotFoundException() {
    when(discountPolicyRepository.existsById(99L)).thenReturn(false);
    assertThrows(ResourceNotFoundException.class, () -> discountPolicyServiceImpl.delete(99L));
  }
}
