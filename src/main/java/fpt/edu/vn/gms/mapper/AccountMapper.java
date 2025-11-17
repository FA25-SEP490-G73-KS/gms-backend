package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.AccountResponseDto;
import fpt.edu.vn.gms.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponseDto toDTO(Account account);
}
