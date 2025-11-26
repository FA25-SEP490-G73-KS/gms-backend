package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.DiscountPolicyResponseDto;
import fpt.edu.vn.gms.entity.DiscountPolicy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountPolicyMapper {
  DiscountPolicyResponseDto toResponseDto(DiscountPolicy entity);
}
