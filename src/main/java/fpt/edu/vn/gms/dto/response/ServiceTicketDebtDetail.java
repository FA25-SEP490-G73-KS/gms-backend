package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.dto.TransactionResponseDto;
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
