# Test Case Matrix: Create Deduction / Ma Trận Test Case: Tạo Khấu Trừ

## Test Case Matrix

| Precondition | UTCID01 | UTCID02 | UTCID03 | UTCID04 | UTCID05 | UTCID06 | UTCID07 |
|--------------|---------|---------|---------|---------|---------|---------|---------|
| **Can connect with server** | O | O | O | O | O | O | O |
| **Employee** | | | | | | | |
| Existing {employeeId: 1, fullName: "Employee"} | O | O | O | O | O | | O |
| Does not exist {employeeId: 999} | | | | | | O | |
| **Creator** | | | | | | | |
| Existing {employeeId: 2, fullName: "Creator"} | O | O | O | O | O | O | |
| Does not exist {employeeId: 999} | | | | | | | O |
| **Input Parameters** | | | | | | | |
| **employeeId** | | | | | | | |
| "1" (valid) | O | O | O | O | O | | O |
| "999" (not exist) | | | | | | O | |
| null | | | | | | | |
| **type** | | | | | | | |
| DAMAGE (valid) | O | O | O | O | O | O | O |
| PENALTY (valid) | | O | | | | | |
| null | | | O | | | | |
| **content** | | | | | | | |
| "Hỏng do lỗi" (valid, not blank) | O | O | O | | O | O | O |
| "" (blank) | | | | O | | | |
| null | | | | | O | | |
| **amount** | | | | | | | |
| "500000" (valid, > 0) | O | O | O | O | | O | O |
| "0" (boundary) | | | | | O | | |
| null | | | | | | | |
| **Expected Outcome** | | | | | | | |
| **Return** | | | | | | | |
| Successfully (DeductionDto) | O | O | | | | | |
| **Exception** | | | | | | | |
| RuntimeException ("Không tìm thấy nhân viên") | | | | | | O | |
| RuntimeException ("Không tìm thấy người tạo") | | | | | | | O |
| ValidationException | | | O | O | O | | |
| **Log message** | | | | | | | |
| | | | | | | | |
| **Result** | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | A | A | B | A | A |
| Passed/Failed | P | P | P | P | P | P | P |
| Executed Date | | | | | | | |
| Defect ID | | | | | | | |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Create Deduction - Normal Flow with DAMAGE Type
**English**: Test creating a deduction successfully with valid employee ID, DAMAGE type, valid content, and valid amount.

**Tiếng Việt**: Kiểm thử tạo khấu trừ thành công với employee ID hợp lệ, loại DAMAGE, nội dung hợp lệ và số tiền hợp lệ.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (employeeId: 1)
- Creator exists: Yes (employeeId: 2)

**Input**:
- employeeId: "1"
- type: DAMAGE
- content: "Hỏng do lỗi"
- amount: "500000"

**Expected**: Successfully return DeductionDto

**Result**: Normal, Passed

---

### UTCID02: Create Deduction - Normal Flow with PENALTY Type
**English**: Test creating a deduction successfully with PENALTY type.

**Tiếng Việt**: Kiểm thử tạo khấu trừ thành công với loại PENALTY.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (employeeId: 1)
- Creator exists: Yes (employeeId: 2)

**Input**:
- employeeId: "1"
- type: PENALTY
- content: "Hỏng do lỗi"
- amount: "500000"

**Expected**: Successfully return DeductionDto

**Result**: Normal, Passed

---

### UTCID03: Create Deduction - Invalid Type (null)
**English**: Test creating a deduction with null type should throw validation exception.

**Tiếng Việt**: Kiểm thử tạo khấu trừ với type null sẽ ném exception validation.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (employeeId: 1)
- Creator exists: Yes (employeeId: 2)

**Input**:
- employeeId: "1"
- type: null
- content: "Hỏng do lỗi"
- amount: "500000"

**Expected**: ValidationException

**Result**: Abnormal, Passed

---

### UTCID04: Create Deduction - Invalid Content (blank)
**English**: Test creating a deduction with blank content should throw validation exception.

**Tiếng Việt**: Kiểm thử tạo khấu trừ với nội dung trống sẽ ném exception validation.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (employeeId: 1)
- Creator exists: Yes (employeeId: 2)

**Input**:
- employeeId: "1"
- type: DAMAGE
- content: "" (blank)
- amount: "500000"

**Expected**: ValidationException ("Content must not be blank")

**Result**: Abnormal, Passed

---

### UTCID05: Create Deduction - Boundary Amount (0)
**English**: Test creating a deduction with amount = 0 (boundary value).

**Tiếng Việt**: Kiểm thử tạo khấu trừ với số tiền = 0 (giá trị biên).

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (employeeId: 1)
- Creator exists: Yes (employeeId: 2)

**Input**:
- employeeId: "1"
- type: DAMAGE
- content: "Hỏng do lỗi"
- amount: "0"

**Expected**: ValidationException (amount must be >= 0)

**Result**: Boundary, Passed

---

### UTCID06: Create Deduction - Employee Not Found
**English**: Test creating a deduction for non-existent employee should throw RuntimeException.

**Tiếng Việt**: Kiểm thử tạo khấu trừ cho nhân viên không tồn tại sẽ ném RuntimeException.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: No (employeeId: 999)
- Creator exists: Yes (employeeId: 2)

**Input**:
- employeeId: "999"
- type: DAMAGE
- content: "Hỏng do lỗi"
- amount: "500000"

**Expected**: RuntimeException ("Không tìm thấy nhân viên")

**Result**: Abnormal, Passed

---

### UTCID07: Create Deduction - Creator Not Found
**English**: Test creating a deduction when creator does not exist should throw RuntimeException.

**Tiếng Việt**: Kiểm thử tạo khấu trừ khi người tạo không tồn tại sẽ ném RuntimeException.

**Preconditions**:
- Can connect with server: Yes
- Employee exists: Yes (employeeId: 1)
- Creator exists: No (employeeId: 999)

**Input**:
- employeeId: "1"
- type: DAMAGE
- content: "Hỏng do lỗi"
- amount: "500000"

**Expected**: RuntimeException ("Không tìm thấy người tạo")

**Result**: Abnormal, Passed

