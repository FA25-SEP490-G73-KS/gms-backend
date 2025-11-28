package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.PayrollDetailDto;
import fpt.edu.vn.gms.dto.response.PayrollMonthlySummaryDto;

public interface PayrollService {

    PayrollMonthlySummaryDto getPayrollPreview(Integer month, Integer year);

    void submitPayroll(Integer month, Integer year, Long accountantId);

    void approvePayroll(Long payrollId, Long managerId);

    void createSalaryPaymentVoucher(Long payrollId, Long accountantId);

    PayrollDetailDto getPayrollDetail(Long employeeId, Integer month, Integer year);
}
