package fpt.edu.vn.gms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fpt.edu.vn.gms.dto.response.TransactionResponseDto;
import fpt.edu.vn.gms.entity.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
  @Mapping(target = "paymentUrl", ignore = true)
  TransactionResponseDto toResponseDto(Transaction transaction);

}