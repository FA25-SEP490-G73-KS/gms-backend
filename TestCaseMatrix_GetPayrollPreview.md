# Test Case Matrix: Get Payroll Preview / Ma Trận Test Case: Xem Trước Lương

## Test Case Matrix

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Employees** | | |
| Has employees | O | |
| Empty list | | O |
| **Attendance** | | |
| Has attendance data | O | |
| **Allowances** | | |
| Has allowances | O | |
| **Deductions** | | |
| Has deductions | O | |
| **Payroll** | | |
| Some employees have existing payroll | O | |
| **Input Parameters** | | |
| **month** | | |
| 1 (valid) | O | O |
| **year** | | |
| 2025 (valid) | O | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (PayrollMonthlySummaryDto) | O | O |
| **Exception** | | |
| | | |
| **Log message** | | |
| | | |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N |
| Passed/Failed | P | P |
| Executed Date | | |
| Defect ID | | |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Get Payroll Preview - Normal Flow with Multiple Employees
**English**: Test getting payroll preview successfully with multiple employees, calculating base salary, allowances, deductions, and net salary.

**Tiếng Việt**: Kiểm thử xem trước lương thành công với nhiều nhân viên, tính toán lương cơ bản, phụ cấp, khấu trừ và lương thực lãnh.

**Preconditions**:
- Can connect with server: Yes
- Employees exist: Yes (multiple employees)
- Attendance data exists: Yes
- Allowances exist: Yes
- Deductions exist: Yes
- Some employees have existing payroll: Yes

**Input**:
- month: 1
- year: 2025

**Expected**: 
- Successfully return PayrollMonthlySummaryDto
- Calculate base salary = dailySalary × workingDays
- Calculate net salary = baseSalary + allowance - deduction - advance
- Set status: PENDING_MANAGER_APPROVAL for new payroll, APPROVED for existing payroll
- Calculate total net salary

**Result**: Normal, Passed

---

### UTCID02: Get Payroll Preview - Empty Employee List
**English**: Test getting payroll preview when there are no employees.

**Tiếng Việt**: Kiểm thử xem trước lương khi không có nhân viên nào.

**Preconditions**:
- Can connect with server: Yes
- Employees exist: No (empty list)

**Input**:
- month: 1
- year: 2025

**Expected**: Successfully return PayrollMonthlySummaryDto with empty items list

**Result**: Normal, Passed

