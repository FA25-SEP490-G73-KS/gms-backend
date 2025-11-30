package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.ServiceTicketDebtDetail;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ServiceTicketMapper.class, TransactionMapper.class })
public interface ServiceTicketDebtDetailMapper {

    @Named("toDebtDetail")
    @Mapping(target = "serviceTicketResponseDto",
            source = "serviceTicket",
            qualifiedByName = "toServiceTicketResponseDto")

    @Mapping(target = "transactionResponseDto",
            source = "transactions")
    ServiceTicketDebtDetail toDebtDetail(ServiceTicket serviceTicket, List<Transaction> transactions);
}
