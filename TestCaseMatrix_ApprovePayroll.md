# Test Case Matrix: Approve Payroll / Ma Trận Test Case: Phê Duyệt Lương

## Test Case Matrix

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Payroll** | | |
| Existing {id: 1, status: PENDING_MANAGER_APPROVAL} | O | |
| Does not exist {id: 999} | | O |
| **Manager** | | |
| Existing {employeeId: 99, fullName: "Manager"} | O | |
| **Input Parameters** | | |
| **payrollId** | | |
| "1" (valid) | O | |
| "999" (not exist) | | O |
| **managerId** | | |
| "99" (valid) | O | |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (status updated to APPROVED) | O | |
| **Exception** | | |
| RuntimeException (Payroll not found) | | O |
| **Log message** | | |
| | | |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |
| Executed Date | | |
| Defect ID | | |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Approve Payroll - Normal Flow
**English**: Test approving payroll successfully. Payroll status should be updated to APPROVED, approvedBy and approvedAt should be set.

**Tiếng Việt**: Kiểm thử phê duyệt lương thành công. Trạng thái lương nên được cập nhật thành APPROVED, approvedBy và approvedAt nên được thiết lập.

**Preconditions**:
- Can connect with server: Yes
- Payroll exists: Yes (id: 1, status: PENDING_MANAGER_APPROVAL)
- Manager exists: Yes (employeeId: 99)

**Input**:
- payrollId: "1"
- managerId: "99"

**Expected**: 
- Payroll status updated to APPROVED
- approvedBy set to manager
- approvedAt set to current timestamp
- Payroll saved successfully

**Result**: Normal, Passed

---

### UTCID02: Approve Payroll - Not Found
**English**: Test approving payroll when payroll does not exist should throw RuntimeException.

**Tiếng Việt**: Kiểm thử phê duyệt lương khi lương không tồn tại sẽ ném RuntimeException.

**Preconditions**:
- Can connect with server: Yes
- Payroll exists: No (id: 999)

**Input**:
- payrollId: "999"
- managerId: "99"

**Expected**: RuntimeException (Payroll not found)

**Result**: Abnormal, Passed

