package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.DiscountPolicyRequestDto;
import fpt.edu.vn.gms.dto.response.DiscountPolicyResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiscountPolicyService {
  Page<DiscountPolicyResponseDto> getAll(Pageable pageable);

  DiscountPolicyResponseDto getById(Long id);

  DiscountPolicyResponseDto create(DiscountPolicyRequestDto dto);

  DiscountPolicyResponseDto update(Long id, DiscountPolicyRequestDto dto);

  void delete(Long id);
}
