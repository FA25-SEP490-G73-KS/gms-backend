package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.PayrollStatus;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PayrollServiceImplTest extends BaseServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;
  @Mock
  private AttendanceRepository attendanceRepository;
  @Mock
  private AllowanceRepository allowanceRepository;
  @Mock
  private DeductionRepository deductionRepository;
  @Mock
  private ManualVoucherRepository voucherRepository;
  @Mock
  private PayrollRepository payrollRepository;

  @InjectMocks
  private PayrollServiceImpl payrollServiceImpl;

  @Test
  void getPayrollPreview_WhenEmployeesExist_ShouldReturnSummaryDto() {
    Employee emp = getMockEmployee(Role.SERVICE_ADVISOR);
    emp.setEmployeeId(1L);
    emp.setFullName("John Doe");
    emp.setDailySalary(BigDecimal.valueOf(100000));
    when(employeeRepository.findAll()).thenReturn(List.of(emp));
    when(attendanceRepository.countPresentDays(1L, 5, 2024)).thenReturn(20);
    when(allowanceRepository.sumForMonth(1L, 5, 2024)).thenReturn(BigDecimal.valueOf(500000));
    when(deductionRepository.sumForMonth(1L, 5, 2024)).thenReturn(BigDecimal.valueOf(100000));
    when(voucherRepository.sumAdvanceSalary(1L, 5, 2024)).thenReturn(BigDecimal.valueOf(200000));
    when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, 5, 2024)).thenReturn(Optional.empty());

    PayrollMonthlySummaryDto result = payrollServiceImpl.getPayrollPreview(5, 2024);

    assertNotNull(result);
    assertEquals(1, result.getItems().size());
    PayrollListItemDto item = result.getItems().get(0);
    assertEquals("John Doe", item.getEmployeeName());
    assertEquals(BigDecimal.valueOf(100000 * 20), item.getBaseSalary());
    assertEquals(BigDecimal.valueOf(500000), item.getAllowance());
    assertEquals(BigDecimal.valueOf(100000), item.getDeduction());
    assertEquals(BigDecimal.valueOf(200000), item.getAdvanceSalary());
    assertEquals(PayrollStatus.PENDING_MANAGER_APPROVAL, item.getStatus());
    assertEquals(
        item.getBaseSalary().add(item.getAllowance()).subtract(item.getDeduction()).subtract(item.getAdvanceSalary()),
        item.getNetSalary());
    assertEquals(item.getNetSalary(), result.getTotalNetSalary());
  }

  @Test
  void approvePayroll_WhenPayrollExists_ShouldUpdateStatusAndSave() {
    Payroll payroll = Payroll.builder()
        .id(1L)
        .status(PayrollStatus.PENDING_MANAGER_APPROVAL)
        .build();
    Employee manager = getMockEmployee(Role.MANAGER);
    when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));
    when(employeeRepository.getReferenceById(2L)).thenReturn(manager);
    when(payrollRepository.save(payroll)).thenReturn(payroll);

    payrollServiceImpl.approvePayroll(1L, 2L);

    assertEquals(PayrollStatus.APPROVED, payroll.getStatus());
    assertEquals(manager, payroll.getApprovedBy());
    assertNotNull(payroll.getApprovedAt());
    verify(payrollRepository).save(payroll);
  }

  @Test
  void approvePayroll_WhenPayrollNotFound_ShouldThrowException() {
    when(payrollRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> payrollServiceImpl.approvePayroll(99L, 1L));
  }

  @Test
  void createSalaryPaymentVoucher_WhenPayrollApproved_ShouldCreateVoucherAndSetPaid() {
    Employee emp = getMockEmployee(Role.SERVICE_ADVISOR);
    emp.setEmployeeId(1L);
    Payroll payroll = Payroll.builder()
        .id(1L)
        .status(PayrollStatus.APPROVED)
        .employee(emp)
        .netSalary(BigDecimal.valueOf(1000000))
        .month(5)
        .year(2024)
        .build();
    Employee accountant = getMockEmployee(Role.ACCOUNTANT);
    when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));
    when(employeeRepository.getReferenceById(2L)).thenReturn(accountant);
    when(voucherRepository.save(any(LedgerVoucher.class))).thenReturn(LedgerVoucher.builder().build());

    payrollServiceImpl.createSalaryPaymentVoucher(1L, 2L);

    assertEquals(PayrollStatus.PAID, payroll.getStatus());
    assertEquals(accountant, payroll.getPaidBy());
    assertNotNull(payroll.getPaidAt());
    verify(voucherRepository).save(any(LedgerVoucher.class));
  }

  @Test
  void createSalaryPaymentVoucher_WhenPayrollNotApproved_ShouldThrowException() {
    Payroll payroll = Payroll.builder()
        .id(1L)
        .status(PayrollStatus.PENDING_MANAGER_APPROVAL)
        .employee(getMockEmployee(Role.SERVICE_ADVISOR))
        .build();
    when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));
    assertThrows(RuntimeException.class, () -> payrollServiceImpl.createSalaryPaymentVoucher(1L, 2L));
  }

  @Test
  void createSalaryPaymentVoucher_WhenPayrollNotFound_ShouldThrowException() {
    when(payrollRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> payrollServiceImpl.createSalaryPaymentVoucher(99L, 1L));
  }

  @Test
  void submitPayroll_WhenPayrollAlreadyExists_ShouldThrowException() {
    when(payrollRepository.existsByMonthAndYear(5, 2024)).thenReturn(true);
    assertThrows(RuntimeException.class, () -> payrollServiceImpl.submitPayroll(5, 2024, 1L));
  }

  @Test
  void submitPayroll_WhenValid_ShouldSavePayrollsForAllEmployees() {
    Employee emp = getMockEmployee(Role.SERVICE_ADVISOR);
    emp.setEmployeeId(1L);
    emp.setFullName("John Doe");
    emp.setDailySalary(BigDecimal.valueOf(100000));
    PayrollListItemDto item = PayrollListItemDto.builder()
        .employeeId(1L)
        .employeeName("John Doe")
        .workingDays(20)
        .baseSalary(BigDecimal.valueOf(2000000))
        .allowance(BigDecimal.valueOf(500000))
        .deduction(BigDecimal.valueOf(100000))
        .advanceSalary(BigDecimal.valueOf(200000))
        .netSalary(BigDecimal.valueOf(2200000))
        .status(PayrollStatus.PENDING_MANAGER_APPROVAL)
        .build();
    PayrollMonthlySummaryDto preview = PayrollMonthlySummaryDto.builder()
        .items(List.of(item))
        .totalNetSalary(BigDecimal.valueOf(2200000))
        .build();

    when(payrollRepository.existsByMonthAndYear(5, 2024)).thenReturn(false);
    // Mock getPayrollPreview to return our preview
    PayrollServiceImpl spyService = Mockito.spy(payrollServiceImpl);
    doReturn(preview).when(spyService).getPayrollPreview(5, 2024);
    Employee refEmp = getMockEmployee(Role.SERVICE_ADVISOR);
    refEmp.setEmployeeId(1L);

    when(employeeRepository.getReferenceById(1L)).thenReturn(refEmp);
    when(payrollRepository.save(any(Payroll.class))).thenReturn(Payroll.builder().build());

    spyService.submitPayroll(5, 2024, 1L);

    verify(payrollRepository).save(any(Payroll.class));
  }

  @Test
  void getPayrollDetail_WhenPayrollExists_ShouldReturnDetailDto() {
    Employee emp = getMockEmployee(Role.SERVICE_ADVISOR);
    emp.setEmployeeId(1L);
    emp.setDailySalary(BigDecimal.valueOf(100000));
    emp.setFullName("John Doe");
    emp.setPhone("0123456789");
    emp.setAddress("123 Street");
    Payroll payroll = Payroll.builder().status(PayrollStatus.APPROVED).build();

    when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));
    when(attendanceRepository.countPresentDays(1L, 5, 2024)).thenReturn(20);
    when(attendanceRepository.countAbsentDays(1L, 5, 2024)).thenReturn(2);
    when(allowanceRepository.sumForMonth(1L, 5, 2024)).thenReturn(BigDecimal.valueOf(500000));
    when(allowanceRepository.findByEmployeeEmployeeIdAndMonthAndYear(1L, 5, 2024)).thenReturn(List.of());
    when(deductionRepository.sumForMonth(1L, 5, 2024)).thenReturn(BigDecimal.valueOf(100000));
    when(deductionRepository.findByEmployeeEmployeeIdAndDateBetween(anyLong(), any(), any())).thenReturn(List.of());
    when(voucherRepository.sumAdvanceSalary(1L, 5, 2024)).thenReturn(BigDecimal.valueOf(200000));
    when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, 5, 2024)).thenReturn(Optional.of(payroll));

    PayrollDetailDto result = payrollServiceImpl.getPayrollDetail(1L, 5, 2024);

    assertNotNull(result);
    assertEquals("John Doe", result.getEmployee().getFullName());
    assertEquals(PayrollStatus.APPROVED, result.getStatus());
    assertTrue(result.isCanPaySalary());
    assertEquals(20, result.getWorkingDays());
    assertEquals(2, result.getLeaveDays());
    assertEquals(BigDecimal.valueOf(100000 * 20), result.getOverview().getBaseSalary());
  }

  @Test
  void getPayrollDetail_WhenEmployeeNotFound_ShouldThrowException() {
    when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> payrollServiceImpl.getPayrollDetail(99L, 5, 2024));
  }
}
