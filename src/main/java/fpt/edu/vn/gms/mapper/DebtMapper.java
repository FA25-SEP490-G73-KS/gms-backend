package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.DebtResDto;
import fpt.edu.vn.gms.entity.Debt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DebtMapper {

    @Mapping(source = "customer.customerId", target = "customerId")
    @Mapping(source = "serviceTicket.serviceTicketId", target = "serviceTicketId")
    @Mapping(target = "statusLabel", expression = "java(debt.getStatus().getLabel())")
    DebtResDto toDto(Debt debt);
}
