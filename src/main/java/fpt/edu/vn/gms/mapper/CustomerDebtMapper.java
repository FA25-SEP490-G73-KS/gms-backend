package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.CustomerDebtResponseDto;
import fpt.edu.vn.gms.entity.Debt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ServiceTicketMapper.class })
public interface CustomerDebtMapper {

    @Mapping(source = "serviceTicket.serviceTicketId", target = "serviceTicketId")
    @Mapping(source = "serviceTicket.serviceTicketCode", target = "serviceTicketCode")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "amount", target = "totalAmount")
    @Mapping(source = "paidAmount", target = "paidAmount")
    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "status", target = "status")
    CustomerDebtResponseDto toDto(Debt debt);

    List<CustomerDebtResponseDto> toDto(List<Debt> debts);
}

