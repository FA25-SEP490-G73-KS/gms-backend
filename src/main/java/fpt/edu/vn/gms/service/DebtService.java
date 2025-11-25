package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.dto.CreateDebtDto;
import fpt.edu.vn.gms.dto.response.DebtResDto;
import org.springframework.data.domain.Page;

public interface DebtService {
    Page<DebtResDto> getDebtsByCustomer(Long customerId, DebtStatus status, String keyword, int page, int size,
            String sort);

    DebtResDto createDebt(CreateDebtDto createDebtDto);
}
