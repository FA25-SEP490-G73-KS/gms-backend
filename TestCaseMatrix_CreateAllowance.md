# Test Case Matrix: Create Allowance / Ma Trận Test Case: Tạo Phụ Cấp

## Test Case Matrix

| Precondition | UTCID01 | UTCID02 | UTCID03 | UTCID04 | UTCID05 | UTCID06 |
|--------------|---------|---------|---------|---------|---------|---------|
| **Can connect with server** | O | O | O | O | O | O |
| **Account** | | | | | | |
| Existing {employeeId: 1, fullName: "Nguyễn Văn A"} | O | O | O | O | O | O |
| Does not exist {employeeId: 999} | | | | | | O |
| **Creator (Accountant)** | | | | | | |
| Existing {employeeId: 99, fullName: "Kế toán viên"} | O | O | O | O | O | O |
| **Input Parameters** | | | | | | |
| **employeeId** | | | | | | |
| "1" (valid) | O | O | O | O | O | |
| "999" (not exist) | | | | | | O |
| null | | | | | O | |
| **type** | | | | | | |
| MEAL (valid) | O | O | O | O | O | O |
| OVERTIME (valid) | | O | | | | |
| null | | | O | | | |
| **amount** | | | | | | |
| "150000" (valid, > 0) | O | O | O | O | | O |
| "0" (boundary) | | | | O | | |
| "-10000" (invalid, < 0) | | | | | O | |
| null | | | | | | |
| **Expected Outcome** | | | | | | |
| **Return** | | | | | | |
| Successfully (AllowanceDto) | O | O | | | | |
| **Exception** | | | | | | |
| ResourceNotFoundException | | | | | | O |
| ValidationException | | | O | O | O | |
| **Log message** | | | | | | |
| | | | | | | |
| **Result** | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | A | B | A | A |
| Passed/Failed | P | P | P | P | P | P |
| Executed Date | | | | | | |
| Defect ID | | | | | | |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Create Allowance - Normal Flow with MEAL Type
**English**: Test creating an allowance successfully with valid employee ID, MEAL type, and valid amount.

**Tiếng Việt**: Kiểm thử tạo phụ cấp thành công với employee ID hợp lệ, loại MEAL và số tiền hợp lệ.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (employeeId: 1)
- Creator exists: Yes (employeeId: 99)

**Input**:
- employeeId: "1"
- type: MEAL
- amount: "150000"

**Expected**: Successfully return AllowanceDto

**Result**: Normal, Passed

---

### UTCID02: Create Allowance - Normal Flow with OVERTIME Type
**English**: Test creating an allowance successfully with OVERTIME type.

**Tiếng Việt**: Kiểm thử tạo phụ cấp thành công với loại OVERTIME.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (employeeId: 1)
- Creator exists: Yes (employeeId: 99)

**Input**:
- employeeId: "1"
- type: OVERTIME
- amount: "150000"

**Expected**: Successfully return AllowanceDto

**Result**: Normal, Passed

---

### UTCID03: Create Allowance - Invalid Type (null)
**English**: Test creating an allowance with null type should throw validation exception.

**Tiếng Việt**: Kiểm thử tạo phụ cấp với type null sẽ ném exception validation.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (employeeId: 1)
- Creator exists: Yes (employeeId: 99)

**Input**:
- employeeId: "1"
- type: null
- amount: "150000"

**Expected**: ValidationException

**Result**: Abnormal, Passed

---

### UTCID04: Create Allowance - Boundary Amount (0)
**English**: Test creating an allowance with amount = 0 (boundary value).

**Tiếng Việt**: Kiểm thử tạo phụ cấp với số tiền = 0 (giá trị biên).

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (employeeId: 1)
- Creator exists: Yes (employeeId: 99)

**Input**:
- employeeId: "1"
- type: MEAL
- amount: "0"

**Expected**: ValidationException (amount must be >= 0)

**Result**: Boundary, Passed

---

### UTCID05: Create Allowance - Invalid Amount (negative)
**English**: Test creating an allowance with negative amount should throw validation exception.

**Tiếng Việt**: Kiểm thử tạo phụ cấp với số tiền âm sẽ ném exception validation.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (employeeId: 1)
- Creator exists: Yes (employeeId: 99)

**Input**:
- employeeId: "1"
- type: MEAL
- amount: "-10000"

**Expected**: ValidationException

**Result**: Abnormal, Passed

---

### UTCID06: Create Allowance - Employee Not Found
**English**: Test creating an allowance for non-existent employee should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử tạo phụ cấp cho nhân viên không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: No (employeeId: 999)
- Creator exists: Yes (employeeId: 99)

**Input**:
- employeeId: "999"
- type: MEAL
- amount: "150000"

**Expected**: ResourceNotFoundException ("Không tìm thấy nhân viên")

**Result**: Abnormal, Passed

