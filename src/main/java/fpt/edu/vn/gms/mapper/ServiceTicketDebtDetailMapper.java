package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.ServiceTicketDebtDetail;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ServiceTicketMapper.class, TransactionMapper.class })
public interface ServiceTicketDebtDetailMapper {

    @Mapping(target = "transactionResponseDto",
            source = "transactions")
    // Field invoice và debtId sẽ được set thêm trong service (DebtServiceImpl)
    ServiceTicketDebtDetail toDebtDetail(ServiceTicket serviceTicket, List<Transaction> transactions);
}
