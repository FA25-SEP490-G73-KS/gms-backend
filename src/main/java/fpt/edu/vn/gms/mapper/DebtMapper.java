package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.DebtDetailResponseDto;
import fpt.edu.vn.gms.entity.Debt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { TransactionMapper.class, ServiceTicketMapper.class })
public interface DebtMapper {

    @Mapping(source = "customer.fullName", target = "customerFullName")
    @Mapping(source = "serviceTicket", target = "serviceTicket", qualifiedByName = "toServiceTicketResponseDto")
    @Mapping(source = "transactions", target = "transactions")
    DebtDetailResponseDto toDto(Debt debt);
}
