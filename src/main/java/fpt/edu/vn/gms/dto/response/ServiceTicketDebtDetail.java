package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTicketDebtDetail {

    private CustomerDebtResponseDto customerDebt;
    private InvoiceDetailResDto invoice;
    private List<TransactionResponseDto> transactionResponseDto;

}
