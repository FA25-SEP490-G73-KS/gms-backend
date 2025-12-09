# Test Case Matrix: Employee Service Functions / Ma Trận Test Case: Các Chức Năng Employee Service

## Test Case Matrix: Find All Employee Is Technicians Active

| Precondition | UTCID01 |
|--------------|---------|
| **Can connect with server** | O |
| **Employees** | |
| Has active technicians | O |
| **Expected Outcome** | |
| **Return** | |
| Successfully (List<EmployeeDto>) | O |
| **Result** | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N |
| Passed/Failed | P |

---

## Test Case Matrix: Find Employee Info By Phone

| Precondition | UTCID01 |
|--------------|---------|
| **Can connect with server** | O |
| **Employee** | |
| Existing {phone: "0909"} | O |
| **Input Parameters** | |
| **phone** | |
| "0909" (valid) | O |
| **Expected Outcome** | |
| **Return** | |
| Successfully (EmployeeInfoResponseDto) | O |
| **Result** | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N |
| Passed/Failed | P |

---

## Test Case Matrix: Find All Employees

| Precondition | UTCID01 |
|--------------|---------|
| **Can connect with server** | O |
| **Employees** | |
| Has employees | O |
| **Attendance** | |
| Has today attendance data | O |
| **Input Parameters** | |
| **page** | |
| 0 (first page) | O |
| **size** | |
| 5 (valid) | O |
| **keyword** | |
| null | O |
| **Expected Outcome** | |
| **Return** | |
| Successfully (Page<EmployeeListResponse>) | O |
| Status computed correctly ("Nghỉ làm" if no attendance) | O |
| **Result** | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N |
| Passed/Failed | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Find All Employee Is Technicians Active
**English**: Test getting all active technicians successfully.

**Tiếng Việt**: Kiểm thử lấy tất cả kỹ thuật viên đang hoạt động thành công.

**Preconditions**:
- Can connect with server: Yes
- Employees exist: Yes (has active technicians)

**Expected**: Successfully return List<EmployeeDto> of active technicians

**Result**: Normal, Passed

---

### UTCID01: Find Employee Info By Phone
**English**: Test getting employee info by phone successfully.

**Tiếng Việt**: Kiểm thử lấy thông tin nhân viên theo số điện thoại thành công.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (phone: "0909")

**Input**:
- phone: "0909"

**Expected**: Successfully return EmployeeInfoResponseDto with employee details including role

**Result**: Normal, Passed

---

### UTCID01: Find All Employees
**English**: Test getting all employees with pagination and status computation.

**Tiếng Việt**: Kiểm thử lấy tất cả nhân viên với phân trang và tính toán trạng thái.

**Preconditions**:
- Can connect with server: Yes
- Employees exist: Yes
- Attendance data exists: Yes (for today)

**Input**:
- page: 0
- size: 5
- keyword: null

**Expected**: 
- Successfully return Page<EmployeeListResponse>
- Status computed: "Nghỉ làm" if no attendance today, otherwise appropriate status

**Result**: Normal, Passed

