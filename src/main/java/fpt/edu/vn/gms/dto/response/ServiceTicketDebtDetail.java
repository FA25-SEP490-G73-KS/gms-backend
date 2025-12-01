package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class ServiceTicketDebtDetail {

    private ServiceTicketResponseDto serviceTicketResponseDto;

    private List<TransactionResponseDto> transactionResponseDto;
}
