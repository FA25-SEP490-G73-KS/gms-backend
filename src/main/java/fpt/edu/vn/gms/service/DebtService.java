package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.dto.CreateDebtDto;
import fpt.edu.vn.gms.dto.CustomerDebtSummaryDto;
import fpt.edu.vn.gms.dto.PayDebtRequestDto;
import fpt.edu.vn.gms.dto.TransactionResponseDto;
import fpt.edu.vn.gms.dto.response.DebtDetailResponseDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketDebtDetail;
import org.springframework.data.domain.Page;

public interface DebtService {
    Page<CustomerDebtSummaryDto> getAllDebtsSummary(int page, int size);

    DebtDetailResponseDto getDebtsByCustomer(Long customerId, DebtStatus status, String keyword, int page,
            int size,
            String sort);

    DebtDetailResponseDto createDebt(CreateDebtDto createDebtDto);

    TransactionResponseDto payDebt(Long debtId, PayDebtRequestDto request) throws Exception;

    ServiceTicketDebtDetail getDebtDetailByServiceTicketId(Long serviceTicketId);
}
