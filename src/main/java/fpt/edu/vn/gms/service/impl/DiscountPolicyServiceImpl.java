package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.DiscountPolicyRequestDto;
import fpt.edu.vn.gms.dto.response.DiscountPolicyResponseDto;
import fpt.edu.vn.gms.entity.DiscountPolicy;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.DiscountPolicyMapper;
import fpt.edu.vn.gms.repository.DiscountPolicyRepository;
import fpt.edu.vn.gms.service.DiscountPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscountPolicyServiceImpl implements DiscountPolicyService {
  private final DiscountPolicyRepository discountPolicyRepository;
  private final DiscountPolicyMapper discountPolicyMapper;

  @Override
  public Page<DiscountPolicyResponseDto> getAll(Pageable pageable) {
    return discountPolicyRepository.findAll(pageable)
        .map(discountPolicyMapper::toResponseDto);
  }

  @Override
  public DiscountPolicyResponseDto getById(Long id) {
    DiscountPolicy entity = discountPolicyRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chính sách giảm giá với id: " + id));
    return discountPolicyMapper.toResponseDto(entity);
  }

  @Override
  public DiscountPolicyResponseDto create(DiscountPolicyRequestDto dto) {
    DiscountPolicy entity = DiscountPolicy.builder()
        .loyaltyLevel(dto.getLoyaltyLevel())
        .discountRate(dto.getDiscountRate())
        .requiredSpending(dto.getRequiredSpending())
        .description(dto.getDescription())
        .build();
    DiscountPolicy saved = discountPolicyRepository.save(entity);
    return discountPolicyMapper.toResponseDto(saved);
  }

  @Override
  public DiscountPolicyResponseDto update(Long id, DiscountPolicyRequestDto dto) {
    DiscountPolicy entity = discountPolicyRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chính sách giảm giá với id: " + id));

    entity.setLoyaltyLevel(dto.getLoyaltyLevel());
    entity.setDiscountRate(dto.getDiscountRate());
    entity.setRequiredSpending(dto.getRequiredSpending());
    entity.setDescription(dto.getDescription());
    DiscountPolicy updated = discountPolicyRepository.save(entity);
    return discountPolicyMapper.toResponseDto(updated);
  }

  @Override
  public void delete(Long id) {
    if (!discountPolicyRepository.existsById(id)) {
      throw new ResourceNotFoundException("Không tìm thấy chính sách giảm giá với id: " + id);
    }
    discountPolicyRepository.deleteById(id);
  }
}
