package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.DeductionType;
import fpt.edu.vn.gms.common.enums.LedgerVoucherStatus;
import fpt.edu.vn.gms.common.enums.LedgerVoucherType;
import fpt.edu.vn.gms.common.enums.PayrollStatus;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceImplTest {

    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    AttendanceRepository attendanceRepository;
    @Mock
    AllowanceRepository allowanceRepository;
    @Mock
    DeductionRepository deductionRepository;
    @Mock
    LedgerVoucherRepository voucherRepository;
    @Mock
    PayrollRepository payrollRepository;

    @InjectMocks
    PayrollServiceImpl service;

    @Captor
    ArgumentCaptor<Payroll> payrollCaptor;

    private Employee emp1;
    private Employee emp2;

    @BeforeEach
    void setUp() {
        emp1 = Employee.builder()
                .employeeId(1L)
                .fullName("Emp 1")
                .phone("0909000001")
                .dailySalary(new BigDecimal("500000"))
                .address("HN")
                .build();

        emp2 = Employee.builder()
                .employeeId(2L)
                .fullName("Emp 2")
                .phone("0909000002")
                .dailySalary(new BigDecimal("400000"))
                .address("HCM")
                .build();
    }

    @Test
    void getPayrollPreview_ShouldCalculateSalariesAndStatus() {
        int month = 1;
        int year = 2025;

        when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2));

        // working days
        when(attendanceRepository.countPresentDays(1L, month, year)).thenReturn(20);
        when(attendanceRepository.countPresentDays(2L, month, year)).thenReturn(22);

        // allowance
        when(allowanceRepository.sumForMonth(1L, month, year)).thenReturn(new BigDecimal("1000000"));
        when(allowanceRepository.sumForMonth(2L, month, year)).thenReturn(new BigDecimal("500000"));

        // deduction
        when(deductionRepository.sumForMonth(1L, month, year)).thenReturn(new BigDecimal("200000"));
        when(deductionRepository.sumForMonth(2L, month, year)).thenReturn(new BigDecimal("100000"));

        // advance
        // Note: sumAdvanceSalary method no longer exists in LedgerVoucherRepository
        // when(voucherRepository.sumAdvanceSalary(1L, month, year)).thenReturn(new BigDecimal("300000"));
        // when(voucherRepository.sumAdvanceSalary(2L, month, year)).thenReturn(new BigDecimal("0"));

        // existing payroll only for emp2
        Payroll existingPayrollEmp2 = Payroll.builder()
                .id(10L)
                .employee(emp2)
                .status(PayrollStatus.APPROVED)
                .build();
        when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, month, year))
                .thenReturn(Optional.empty());
        when(payrollRepository.findByEmployeeIdAndMonthAndYear(2L, month, year))
                .thenReturn(Optional.of(existingPayrollEmp2));

        PayrollMonthlySummaryDto summary = service.getPayrollPreview(month, year);

        assertNotNull(summary);
        assertEquals(2, summary.getItems().size());

        PayrollListItemDto i1 = summary.getItems().stream()
                .filter(i -> i.getEmployeeId().equals(1L))
                .findFirst()
                .orElseThrow();
        PayrollListItemDto i2 = summary.getItems().stream()
                .filter(i -> i.getEmployeeId().equals(2L))
                .findFirst()
                .orElseThrow();

        // emp1: baseSalary = 500000 * 20 = 10,000,000
        assertEquals(new BigDecimal("10000000"), i1.getBaseSalary());
        // net = base + allowance - deduction - advance
        assertEquals(new BigDecimal("10000000")
                        .add(new BigDecimal("1000000"))
                        .subtract(new BigDecimal("200000"))
                        .subtract(new BigDecimal("300000")),
                i1.getNetSalary());
        assertEquals(PayrollStatus.PENDING_MANAGER_APPROVAL, i1.getStatus());

        // emp2: baseSalary = 400000 * 22 = 8,800,000
        assertEquals(new BigDecimal("8800000"), i2.getBaseSalary());
        assertEquals(PayrollStatus.APPROVED, i2.getStatus());

        // total net
        assertEquals(i1.getNetSalary().add(i2.getNetSalary()), summary.getTotalNetSalary());

        verify(employeeRepository).findAll();
    }

    @Test
    void approvePayroll_ShouldUpdateStatusAndApprover() {
        Payroll payroll = Payroll.builder()
                .id(1L)
                .status(PayrollStatus.PENDING_MANAGER_APPROVAL)
                .build();

        Employee manager = Employee.builder()
                .employeeId(99L)
                .fullName("Manager")
                .build();

        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));
        when(employeeRepository.getReferenceById(99L)).thenReturn(manager);

        service.approvePayroll(1L, 99L);

        assertEquals(PayrollStatus.APPROVED, payroll.getStatus());
        assertEquals(manager, payroll.getApprovedBy());
        assertNotNull(payroll.getApprovedAt());

        verify(payrollRepository).save(payroll);
    }

    @Test
    void approvePayroll_ShouldThrow_WhenPayrollNotFound() {
        when(payrollRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> service.approvePayroll(1L, 99L));
    }

    @Test
    void createSalaryPaymentVoucher_ShouldCreateVoucherAndMarkPaid_WhenApproved() {
        Payroll payroll = Payroll.builder()
                .id(1L)
                .employee(emp1)
                .month(1)
                .year(2025)
                .netSalary(new BigDecimal("10000000"))
                .status(PayrollStatus.APPROVED)
                .build();

        Employee accountant = Employee.builder()
                .employeeId(50L)
                .fullName("Accountant")
                .build();

        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));
        when(employeeRepository.getReferenceById(50L)).thenReturn(accountant);

        service.createSalaryPaymentVoucher(1L, 50L);

        verify(voucherRepository).save(argThat(v -> {
            assertEquals(LedgerVoucherType.SALARY, v.getType());
            assertEquals(payroll.getNetSalary(), v.getAmount());
            assertEquals(payroll.getEmployee().getEmployeeId(), v.getRelatedEmployeeId());
            assertEquals(LedgerVoucherStatus.PENDING, v.getStatus());
            assertEquals(accountant, v.getCreatedBy());
            assertTrue(v.getDescription().contains("Chi lương tháng"));
            return true;
        }));

        assertEquals(PayrollStatus.PAID, payroll.getStatus());
        assertEquals(accountant, payroll.getPaidBy());
        assertNotNull(payroll.getPaidAt());
    }

    @Test
    void createSalaryPaymentVoucher_ShouldThrow_WhenPayrollNotFound() {
        when(payrollRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> service.createSalaryPaymentVoucher(1L, 1L));
    }

    @Test
    void createSalaryPaymentVoucher_ShouldThrow_WhenStatusNotApproved() {
        Payroll payroll = Payroll.builder()
                .id(1L)
                .employee(emp1)
                .status(PayrollStatus.PENDING_MANAGER_APPROVAL)
                .build();
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));

        assertThrows(RuntimeException.class,
                () -> service.createSalaryPaymentVoucher(1L, 1L));
        verify(voucherRepository, never()).save(any());
    }

    @Test
    void submitPayroll_ShouldThrow_WhenAlreadyExistsForMonthYear() {
        when(payrollRepository.existsByMonthAndYear(1, 2025)).thenReturn(true);
        assertThrows(RuntimeException.class,
                () -> service.submitPayroll(1, 2025, 1L));
    }

    @Test
    void submitPayroll_ShouldCreatePayrollRecordsFromPreview() {
        int month = 1;
        int year = 2025;

        when(payrollRepository.existsByMonthAndYear(month, year)).thenReturn(false);

        PayrollListItemDto item = PayrollListItemDto.builder()
                .employeeId(1L)
                .employeeName("Emp 1")
                .phone("0909")
                .baseSalary(new BigDecimal("10000000"))
                .allowance(new BigDecimal("1000000"))
                .deduction(new BigDecimal("200000"))
                .advanceSalary(new BigDecimal("300000"))
                .netSalary(new BigDecimal("10500000"))
                .workingDays(20)
                .status(PayrollStatus.PENDING_MANAGER_APPROVAL)
                .build();
        PayrollMonthlySummaryDto preview = PayrollMonthlySummaryDto.builder()
                .items(Collections.singletonList(item))
                .totalNetSalary(item.getNetSalary())
                .build();

        // Spy service to stub getPayrollPreview
        PayrollServiceImpl spyService = org.mockito.Mockito.spy(service);
        doReturn(preview).when(spyService).getPayrollPreview(month, year);

        Employee refEmp = Employee.builder().employeeId(1L).build();
        when(employeeRepository.getReferenceById(1L)).thenReturn(refEmp);

        spyService.submitPayroll(month, year, 1L);

        verify(payrollRepository).save(payrollCaptor.capture());
        Payroll saved = payrollCaptor.getValue();
        assertEquals(refEmp, saved.getEmployee());
        assertEquals(month, saved.getMonth());
        assertEquals(year, saved.getYear());
        assertEquals(item.getBaseSalary(), saved.getBaseSalary());
        assertEquals(item.getAllowance(), saved.getTotalAllowance());
        assertEquals(item.getDeduction(), saved.getTotalDeduction());
        assertEquals(item.getAdvanceSalary(), saved.getTotalAdvanceSalary());
        assertEquals(item.getNetSalary(), saved.getNetSalary());
        assertEquals(PayrollStatus.PENDING_MANAGER_APPROVAL, saved.getStatus());
    }

    @Test
    void getPayrollDetail_ShouldBuildDetailWithStatusAndFlags() {
        int month = 1;
        int year = 2025;

        Account account = Account.builder()
                .role(fpt.edu.vn.gms.common.enums.Role.MANAGER)
                .build();

        Employee emp = Employee.builder()
                .employeeId(1L)
                .fullName("Emp 1")
                .phone("0909")
                .address("HN")
                .dailySalary(new BigDecimal("500000"))
                .account(account)
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        when(attendanceRepository.countPresentDays(1L, month, year)).thenReturn(20);
        when(attendanceRepository.countAbsentDays(1L, month, year)).thenReturn(2);

        when(allowanceRepository.sumForMonth(1L, month, year)).thenReturn(new BigDecimal("1000000"));
        Allowance allowance = Allowance.builder()
                .type(fpt.edu.vn.gms.common.enums.AllowanceType.MEAL)
                .amount(new BigDecimal("500000"))
                .createdAt(LocalDateTime.now())
                .createdBy("Accountant")
                .build();
        when(allowanceRepository.findByEmployeeEmployeeIdAndMonthAndYear(1L, month, year))
                .thenReturn(List.of(allowance));

        when(deductionRepository.sumForMonth(1L, month, year)).thenReturn(new BigDecimal("200000"));
        Deduction deduction = Deduction.builder()
                .type(DeductionType.PENALTY)
                .amount(new BigDecimal("100000"))
                .date(LocalDate.now())
                .createdBy("Accountant")
                .build();
        when(deductionRepository.findByEmployeeEmployeeIdAndDateBetween(
                eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(deduction));

        // Note: sumAdvanceSalary method no longer exists in LedgerVoucherRepository
        // when(voucherRepository.sumAdvanceSalary(1L, month, year))
        //         .thenReturn(new BigDecimal("300000"));

        Payroll payroll = Payroll.builder()
                .id(10L)
                .employee(emp)
                .status(PayrollStatus.APPROVED)
                .build();
        when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, month, year))
                .thenReturn(Optional.of(payroll));

        PayrollDetailDto detail = service.getPayrollDetail(1L, month, year);

        assertNotNull(detail);
        assertEquals("Emp 1", detail.getEmployee().getFullName());
        assertEquals("0909", detail.getEmployee().getPhone());
        assertEquals("HN", detail.getEmployee().getAddress());
        assertEquals("MANAGER", detail.getEmployee().getRole());

        PayrollOverviewDto overview = detail.getOverview();
        assertNotNull(overview);
        assertEquals(new BigDecimal("10000000"), overview.getBaseSalary()); // 500000 * 20
        assertEquals(20.0, overview.getTotalWorkingDays());
        assertEquals(2, overview.getLeaveDays());
        assertEquals(new BigDecimal("1000000"), overview.getTotalAllowance());
        assertEquals(new BigDecimal("200000"), overview.getTotalDeduction());

        assertEquals(1, detail.getAllowances().size());
        assertEquals(1, detail.getDeductions().size());

        assertEquals(PayrollStatus.APPROVED, detail.getStatus());
        assertTrue(detail.isCanPaySalary());
    }

    @Test
    void getPayrollDetail_ShouldHandleMissingPayroll_AsPendingAndCannotPay() {
        int month = 1;
        int year = 2025;

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp1));
        when(attendanceRepository.countPresentDays(1L, month, year)).thenReturn(0);
        when(attendanceRepository.countAbsentDays(1L, month, year)).thenReturn(0);
        when(allowanceRepository.sumForMonth(1L, month, year)).thenReturn(BigDecimal.ZERO);
        when(allowanceRepository.findByEmployeeEmployeeIdAndMonthAndYear(1L, month, year))
                .thenReturn(Collections.emptyList());
        when(deductionRepository.sumForMonth(1L, month, year)).thenReturn(BigDecimal.ZERO);
        when(deductionRepository.findByEmployeeEmployeeIdAndDateBetween(
                eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        // Note: sumAdvanceSalary method no longer exists in LedgerVoucherRepository
        // when(voucherRepository.sumAdvanceSalary(1L, month, year)).thenReturn(BigDecimal.ZERO);
        when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, month, year))
                .thenReturn(Optional.empty());

        PayrollDetailDto detail = service.getPayrollDetail(1L, month, year);

        assertEquals(PayrollStatus.PENDING_MANAGER_APPROVAL, detail.getStatus());
        assertFalse(detail.isCanPaySalary());
    }

    @Test
    void getPayrollDetail_ShouldThrow_WhenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> service.getPayrollDetail(1L, 1, 2025));
    }
}


