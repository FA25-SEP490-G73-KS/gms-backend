# Test Case Matrix: Payroll Service Additional Functions / Ma Trận Test Case: Các Chức Năng Bổ Sung Payroll Service

## Test Case Matrix: Create Salary Payment Voucher

| Precondition | UTCID01 | UTCID02 | UTCID03 |
|--------------|---------|---------|---------|
| **Can connect with server** | O | O | O |
| **Payroll** | | | | |
| Existing {id: 1, status: APPROVED} | O | | |
| Existing {id: 1, status: PENDING_MANAGER_APPROVAL} | | O | |
| Does not exist {id: 999} | | | O |
| **Employee (Accountant)** | | | | |
| Existing {employeeId: 50} | O | O | |
| **Input Parameters** | | | | |
| **payrollId** | | | | |
| "1" (valid) | O | O | |
| "999" (not exist) | | | O |
| **accountantId** | | | | |
| "50" (valid) | O | O | |
| **Expected Outcome** | | | | |
| **Return** | | | | |
| Successfully | O | | |
| **Exception** | | | | |
| RuntimeException (Payroll not found) | | | O |
| RuntimeException (Status not APPROVED) | | O | |
| **Voucher** | | | | |
| Created with type SALARY | O | | |
| Amount = netSalary | O | | |
| Status: PENDING | O | | |
| **Payroll** | | | | |
| Status updated to PAID | O | | |
| paidBy and paidAt set | O | | |
| **Result** | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A |
| Passed/Failed | P | P | P |

---

## Test Case Matrix: Submit Payroll

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Payroll** | | | |
| Does not exist for month/year | O | |
| Already exists for month/year | | O |
| **Payroll Preview** | | | |
| Has preview data | O | |
| **Input Parameters** | | | |
| **month** | | | |
| 1 (valid) | O | O |
| **year** | | | |
| 2025 (valid) | O | O |
| **createdById** | | | |
| 1 (valid) | O | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully | O | |
| **Exception** | | | |
| RuntimeException (already exists) | | O |
| **Payroll Records** | | | |
| Created for each employee | O | |
| Status: PENDING_MANAGER_APPROVAL | O | |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Create Salary Payment Voucher - Normal Flow
**English**: Test creating salary payment voucher successfully when payroll is APPROVED.

**Tiếng Việt**: Kiểm thử tạo phiếu chi lương thành công khi lương đã được APPROVED.

**Preconditions**:
- Can connect with server: Yes
- Payroll exists: Yes (id: 1, status: APPROVED, netSalary: 10000000)
- Employee (Accountant) exists: Yes (employeeId: 50)

**Input**:
- payrollId: "1"
- accountantId: "50"

**Expected**: 
- Voucher created with type SALARY
- Amount = payroll.netSalary
- Status: PENDING
- Payroll status updated to PAID
- paidBy and paidAt set

**Result**: Normal, Passed

---

### UTCID02: Create Salary Payment Voucher - Status Not Approved
**English**: Test creating salary payment voucher when payroll status is not APPROVED should throw RuntimeException.

**Tiếng Việt**: Kiểm thử tạo phiếu chi lương khi trạng thái lương không phải APPROVED sẽ ném RuntimeException.

**Preconditions**:
- Can connect with server: Yes
- Payroll exists: Yes (id: 1, status: PENDING_MANAGER_APPROVAL)
- Employee (Accountant) exists: Yes (employeeId: 50)

**Input**:
- payrollId: "1"
- accountantId: "50"

**Expected**: RuntimeException (payroll status must be APPROVED)

**Result**: Abnormal, Passed

---

### UTCID01: Submit Payroll - Normal Flow
**English**: Test submitting payroll successfully. Payroll records created from preview data.

**Tiếng Việt**: Kiểm thử gửi lương thành công. Bản ghi lương được tạo từ dữ liệu xem trước.

**Preconditions**:
- Can connect with server: Yes
- Payroll does not exist for month/year: Yes
- Payroll Preview has data: Yes

**Input**:
- month: 1
- year: 2025
- createdById: 1

**Expected**: 
- Payroll records created for each employee in preview
- Status: PENDING_MANAGER_APPROVAL
- All fields from preview copied to payroll

**Result**: Normal, Passed

---

### UTCID02: Submit Payroll - Already Exists
**English**: Test submitting payroll when payroll already exists for month/year should throw RuntimeException.

**Tiếng Việt**: Kiểm thử gửi lương khi lương đã tồn tại cho tháng/năm sẽ ném RuntimeException.

**Preconditions**:
- Can connect with server: Yes
- Payroll already exists for month/year: Yes

**Input**:
- month: 1
- year: 2025
- createdById: 1

**Expected**: RuntimeException (payroll already exists for this month/year)

**Result**: Abnormal, Passed

