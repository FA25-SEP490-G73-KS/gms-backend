package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.DebtDetailResponseDto;
import fpt.edu.vn.gms.entity.Debt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { TransactionMapper.class, ServiceTicketMapper.class })
public interface DebtMapper {

    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(source = "customer.phone", target = "phone")
    @Mapping(source = "serviceTicket.vehicleLicensePlate", target = "licensePlate")
    @Mapping(source = "customer.address", target = "address")
    DebtDetailResponseDto toDto(Debt debt);
}
