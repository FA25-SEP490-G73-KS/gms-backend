package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.dto.request.CreateDebtDto;
import fpt.edu.vn.gms.dto.response.CustomerDebtSummaryDto;
import fpt.edu.vn.gms.dto.request.PayDebtRequestDto;
import fpt.edu.vn.gms.dto.response.TransactionResponseDto;
import fpt.edu.vn.gms.dto.response.DebtDetailResponseDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketDebtDetail;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface DebtService {
    Page<CustomerDebtSummaryDto> getAllDebtsSummary(int page, int size);

    DebtDetailResponseDto getDebtsByCustomer(Long customerId, DebtStatus status, String keyword, int page,
            int size,
            String sort);

    DebtDetailResponseDto createDebt(CreateDebtDto createDebtDto);

    TransactionResponseDto payDebt(Long debtId, PayDebtRequestDto request) throws Exception;

    ServiceTicketDebtDetail getDebtDetailByServiceTicketId(Long serviceTicketId);

    Page<CustomerDebtSummaryDto> getAllDebtsSummary(int page, int size, DebtStatus status, LocalDate fromDate, LocalDate toDate);

    void updateDueDate(Long debtId, LocalDate dueDate);
}