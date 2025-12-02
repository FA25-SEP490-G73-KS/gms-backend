package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.LedgerVoucherCategory;
import fpt.edu.vn.gms.common.enums.ManualVoucherStatus;
import fpt.edu.vn.gms.common.enums.ManualVoucherType;
import fpt.edu.vn.gms.common.enums.PayrollStatus;
import fpt.edu.vn.gms.dto.request.DeductionDto;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.PayrollService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PayrollServiceImpl implements PayrollService {

    EmployeeRepository employeeRepository;
    AttendanceRepository attendanceRepository;
    AllowanceRepository allowanceRepository;
    DeductionRepository deductionRepository;
    ManualVoucherRepository voucherRepository;
    PayrollRepository payrollRepository;
    private final CodeSequenceService codeSequenceService;

    @Override
    public PayrollMonthlySummaryDto getPayrollPreview(Integer month, Integer year) {

        List<Employee> employees = employeeRepository.findAll();
        List<PayrollListItemDto> result = new ArrayList<>();

        BigDecimal totalNet = BigDecimal.ZERO;

        for (Employee emp : employees) {

            // 1. Working days
            Integer workingDays = attendanceRepository.countPresentDays(
                    emp.getEmployeeId(), month, year);

            // dailySalary ??? -> bạn phải thêm vào entity Employee
            BigDecimal baseSalary = emp.getDailySalary()
                    .multiply(BigDecimal.valueOf(workingDays));

            BigDecimal allowance = allowanceRepository.sumForMonth(
                    emp.getEmployeeId(), month, year);

            BigDecimal deduction = deductionRepository.sumForMonth(
                    emp.getEmployeeId(), month, year);

            BigDecimal advanceSalary = voucherRepository.sumAdvanceSalary(
                    emp.getEmployeeId(), month, year);

            BigDecimal netSalary = baseSalary
                    .add(allowance)
                    .subtract(deduction)
                    .subtract(advanceSalary);

            // Check payroll exists?
            Payroll payroll = payrollRepository
                    .findByEmployeeIdAndMonthAndYear(emp.getEmployeeId(), month, year)
                    .orElse(null);

            PayrollStatus status = payroll == null
                    ? PayrollStatus.PENDING_MANAGER_APPROVAL
                    : payroll.getStatus();

            PayrollListItemDto
                    item = PayrollListItemDto.builder()
                    .employeeId(emp.getEmployeeId())
                    .employeeName(emp.getFullName())
                    .phone(emp.getPhone())
                    .workingDays(workingDays)
                    .baseSalary(baseSalary)
                    .allowance(allowance)
                    .deduction(deduction)
                    .advanceSalary(advanceSalary)
                    .netSalary(netSalary)
                    .status(status)
                    .build();

            result.add(item);
            totalNet = totalNet.add(netSalary);
        }

        return PayrollMonthlySummaryDto.builder()
                .items(result)
                .totalNetSalary(totalNet)
                .build();
    }

    @Override
    @Transactional
    public void approvePayroll(Long payrollId, Long managerId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payroll"));

        payroll.setStatus(PayrollStatus.APPROVED);
        payroll.setApprovedBy(employeeRepository.getReferenceById(managerId));
        payroll.setApprovedAt(LocalDateTime.now());

        payrollRepository.save(payroll);
    }

    @Override
    @Transactional
    public void createSalaryPaymentVoucher(Long payrollId, Long accountantId) {

        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payroll"));

        if (payroll.getStatus() != PayrollStatus.APPROVED) {
            throw new RuntimeException("Payroll chưa được duyệt.");
        }

        LedgerVoucher voucher = LedgerVoucher.builder()
                .code(codeSequenceService.generateCode("CHI"))
                .type(ManualVoucherType.PAYMENT)
                .category(LedgerVoucherCategory.SALARY_PAYMENT)
                .relatedEmployeeId(payroll.getEmployee().getEmployeeId())
                .amount(payroll.getNetSalary())
                .description("Chi lương tháng " + payroll.getMonth() + "/" + payroll.getYear()
                        + " cho " + payroll.getEmployee().getFullName())
                .status(ManualVoucherStatus.PENDING)
                .createdBy(employeeRepository.getReferenceById(accountantId))
                .build();

        voucherRepository.save(voucher);

        payroll.setStatus(PayrollStatus.PAID);
        payroll.setPaidBy(employeeRepository.getReferenceById(accountantId));
        payroll.setPaidAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void submitPayroll(Integer month, Integer year, Long accountantId) {

        if (payrollRepository.existsByMonthAndYear(month, year)) {
            throw new RuntimeException("Payroll tháng này đã được tạo.");
        }

        PayrollMonthlySummaryDto preview = getPayrollPreview(month, year);

        for (PayrollListItemDto dto : preview.getItems()) {

            Payroll payroll = Payroll.builder()
                    .employee(employeeRepository.getReferenceById(dto.getEmployeeId()))
                    .month(month)
                    .year(year)
                    .workingDays(dto.getWorkingDays())
                    .baseSalary(dto.getBaseSalary())
                    .totalAllowance(dto.getAllowance())
                    .totalDeduction(dto.getDeduction())
                    .totalAdvanceSalary(dto.getAdvanceSalary())
                    .netSalary(dto.getNetSalary())
                    .status(PayrollStatus.PENDING_MANAGER_APPROVAL)
                    .build();

            payrollRepository.save(payroll);
        }
    }

    @Override
    public PayrollDetailDto getPayrollDetail(Long employeeId, Integer month, Integer year) {

        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        Integer workingDays = attendanceRepository.countPresentDays(employeeId, month, year);
        Integer leaveDays = attendanceRepository.countAbsentDays(employeeId, month, year);

        BigDecimal baseSalary = emp.getDailySalary()
                .multiply(BigDecimal.valueOf(workingDays));


        BigDecimal totalAllowance = allowanceRepository.sumForMonth(employeeId, month, year);
        List<Allowance> allowanceList =
                allowanceRepository.findByEmployeeEmployeeIdAndMonthAndYear(employeeId, month, year);

        List<AllowanceDto> allowanceDTOs = allowanceList.stream()
                .map(a -> new AllowanceDto(a.getType().getVietnamese(), a.getAmount(), a.getCreatedAt(), a.getCreatedBy()))
                .toList();


        BigDecimal totalDeduction = deductionRepository.sumForMonth(employeeId, month, year);
        List<Deduction> deductionList = deductionRepository.findByEmployeeEmployeeIdAndDateBetween(
                employeeId,
                LocalDate.of(year, month, 1),
                LocalDate.of(year, month, Month.of(month).length(Year.isLeap(year)))
        );

        List<DeductionDto> deductionDTOs = deductionList.stream()
                .map(d -> new DeductionDto(d.getType().getVietnamese(), d.getAmount(), d.getDate(), d.getCreatedBy()))
                .toList();


        BigDecimal totalAdvance =
                voucherRepository.sumAdvanceSalary(employeeId, month, year);

        BigDecimal netSalary = baseSalary
                .add(totalAllowance)
                .subtract(totalDeduction)
                .subtract(totalAdvance);


        Payroll payroll = payrollRepository
                .findByEmployeeIdAndMonthAndYear(employeeId, month, year)
                .orElse(null);

        PayrollStatus status = payroll == null
                ? PayrollStatus.PENDING_MANAGER_APPROVAL
                : payroll.getStatus();

        boolean canPaySalary = payroll != null && payroll.getStatus() == PayrollStatus.APPROVED;


        return PayrollDetailDto.builder()
                .employee(
                        EmployeeInfoDto.builder()
                                .fullName(emp.getFullName())
                                .phone(emp.getPhone())
                                .address(emp.getAddress())
                                .role(emp.getAccount() != null ? emp.getAccount().getRole().name() : null)
                                .build()
                )
                .overview(
                        PayrollOverviewDto.builder()
                                .baseSalary(baseSalary)
                                .totalWorkingDays(Double.valueOf(workingDays))
                                .leaveDays(leaveDays)
                                .totalAllowance(totalAllowance)
                                .totalDeduction(totalDeduction)
                                .netSalary(netSalary)
                                .build()
                )
                .allowances(allowanceDTOs)
                .deductions(deductionDTOs)
                .workingDays(workingDays)
                .leaveDays(leaveDays)
                .status(status)
                .canPaySalary(canPaySalary)
                .build();
    }

    @Override
    public PayrollSummaryDto getPayrollSummaryByMonthYear(Integer month, Integer year) {

        var payrolls = payrollRepository.findByMonthAndYear(month, year);

        BigDecimal totalPayroll = java.math.BigDecimal.ZERO;
        BigDecimal totalApproved = java.math.BigDecimal.ZERO;
        BigDecimal totalPending = java.math.BigDecimal.ZERO;
        BigDecimal totalAllowance = java.math.BigDecimal.ZERO;
        BigDecimal totalDeduction = java.math.BigDecimal.ZERO;

        for (var p : payrolls) {
            if (p.getNetSalary() != null) totalPayroll = totalPayroll.add(p.getNetSalary());
            if (p.getTotalAllowance() != null) totalAllowance = totalAllowance.add(p.getTotalAllowance());
            if (p.getTotalDeduction() != null) totalDeduction = totalDeduction.add(p.getTotalDeduction());
            if (p.getStatus() == fpt.edu.vn.gms.common.enums.PayrollStatus.APPROVED && p.getNetSalary() != null) {
                totalApproved = totalApproved.add(p.getNetSalary());
            }
            if (p.getStatus() == fpt.edu.vn.gms.common.enums.PayrollStatus.PENDING_MANAGER_APPROVAL && p.getNetSalary() != null) {
                totalPending = totalPending.add(p.getNetSalary());
            }
        }
        return PayrollSummaryDto.builder()
                .totalPayroll(totalPayroll)
                .totalApproved(totalApproved)
                .totalPending(totalPending)
                .totalAllowance(totalAllowance)
                .totalDeduction(totalDeduction)
                .build();
    }

}
