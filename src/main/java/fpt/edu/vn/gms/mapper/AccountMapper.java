package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.AccountResponseDto;
import fpt.edu.vn.gms.dto.response.ResetPasswordResponseDTO;
import fpt.edu.vn.gms.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mapping(source = "role.roleName", target = "roleName")
    AccountResponseDto toDTO(Account account);

    // Map Account → ResetPasswordResponseDTO
    @Mapping(source = "role.roleName", target = "roleName")
    @Mapping(target = "message", ignore = true) // message sẽ set thủ công
    ResetPasswordResponseDTO toResetPasswordResponseDTO(Account account);
}
