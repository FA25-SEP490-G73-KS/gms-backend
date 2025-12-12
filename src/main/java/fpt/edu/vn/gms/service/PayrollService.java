package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.PayrollDetailDto;
import fpt.edu.vn.gms.dto.response.PayrollExistsResponse;
import fpt.edu.vn.gms.dto.response.PayrollListItemDto;
import fpt.edu.vn.gms.dto.response.PayrollMonthlySummaryDto;
import fpt.edu.vn.gms.dto.response.PayrollSummaryDto;

import java.util.List;

public interface PayrollService {

    PayrollMonthlySummaryDto getPayrollPreview(Integer month, Integer year);

    void submitPayroll(Integer month, Integer year, Long accountantId);

    void approvePayroll(Long payrollId, Long managerId);

    void createSalaryPaymentVoucher(Long payrollId, Long accountantId);

    PayrollDetailDto getPayrollDetail(Long employeeId, Integer month, Integer year);

    PayrollSummaryDto getPayrollSummaryByMonthYear(Integer month, Integer year);

    List<PayrollListItemDto> getPayrollList(Integer month, Integer year);

    PayrollExistsResponse checkPayrollExists(Integer month, Integer year);
}
