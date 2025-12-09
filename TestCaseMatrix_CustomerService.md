# Test Case Matrix: Customer Service Functions / Ma Trận Test Case: Các Chức Năng Customer Service

## Test Case Matrix: Search Customers By Phone

| Precondition | UTCID01 | UTCID02 | UTCID03 |
|--------------|---------|---------|---------|
| **Can connect with server** | O | O | O |
| **Customers** | | | |
| Has customers matching phone | O | | |
| **Input Parameters** | | | |
| **phone** | | | |
| "0909" (valid, not empty) | O | | |
| null | | O | |
| "" (empty) | | | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (List<CustomerDto>, top 10) | O | | |
| Empty list | | O | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N |
| Passed/Failed | P | P | P |

---

## Test Case Matrix: Get Customer Detail By ID

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Customer** | | |
| Existing {id: 1} | O | |
| Does not exist {id: 999} | | O |
| **Input Parameters** | | |
| **customerId** | | |
| "1" (valid) | O | |
| "999" (not exist) | | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (CustomerDetailResponseDto) | O | |
| **Exception** | | |
| ResourceNotFoundException | | O |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |

---

## Test Case Matrix: Get All Customers

| Precondition | UTCID01 |
|--------------|---------|
| **Can connect with server** | O |
| **Customers** | |
| Has customers | O |
| **Input Parameters** | | |
| **page** | | |
| 0 (first page) | O |
| **size** | | |
| 10 (valid) | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (Page<CustomerResponseDto>) | O |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N |
| Passed/Failed | P |

---

## Test Case Matrix: Get Customer By Phone

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Customer** | | |
| Existing {phone: "0909000000"} | O | |
| Does not exist {phone: "not-exist"} | | O |
| **Input Parameters** | | |
| **phone** | | |
| "0909000000" (valid) | O | |
| "not-exist" (not exist) | | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (CustomerDetailResponseDto) | O | |
| **Exception** | | |
| ResourceNotFoundException | | O |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Search Customers By Phone - Normal Flow
**English**: Test searching customers by phone successfully, returning top 10 matching customers.

**Tiếng Việt**: Kiểm thử tìm kiếm khách hàng theo số điện thoại thành công, trả về top 10 khách hàng khớp.

**Preconditions**:
- Can connect with server: Yes
- Customers exist: Yes (has customers matching phone)

**Input**:
- phone: "0909"

**Expected**: Successfully return List<CustomerDto> (max 10) ordered by phone ascending

**Result**: Normal, Passed

---

### UTCID02: Search Customers By Phone - Null Input
**English**: Test searching customers by phone with null input should return empty list.

**Tiếng Việt**: Kiểm thử tìm kiếm khách hàng theo số điện thoại với input null sẽ trả về danh sách rỗng.

**Preconditions**:
- Can connect with server: Yes

**Input**:
- phone: null

**Expected**: Return empty list (no repository interaction)

**Result**: Normal, Passed

---

### UTCID03: Search Customers By Phone - Empty Input
**English**: Test searching customers by phone with empty string should return empty list.

**Tiếng Việt**: Kiểm thử tìm kiếm khách hàng theo số điện thoại với chuỗi rỗng sẽ trả về danh sách rỗng.

**Preconditions**:
- Can connect with server: Yes

**Input**:
- phone: ""

**Expected**: Return empty list (no repository interaction)

**Result**: Normal, Passed

---

### UTCID01: Get Customer Detail By ID - Normal Flow
**English**: Test getting customer detail by ID successfully.

**Tiếng Việt**: Kiểm thử lấy chi tiết khách hàng theo ID thành công.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: Yes (id: 1)

**Input**:
- customerId: "1"

**Expected**: Successfully return CustomerDetailResponseDto with all customer details

**Result**: Normal, Passed

---

### UTCID02: Get Customer Detail By ID - Not Found
**English**: Test getting customer detail when customer does not exist should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử lấy chi tiết khách hàng khi khách hàng không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: No (id: 999)

**Input**:
- customerId: "999"

**Expected**: ResourceNotFoundException

**Result**: Abnormal, Passed

---

### UTCID01: Get All Customers - Normal Flow
**English**: Test getting all customers with pagination successfully.

**Tiếng Việt**: Kiểm thử lấy tất cả khách hàng với phân trang thành công.

**Preconditions**:
- Can connect with server: Yes
- Customers exist: Yes

**Input**:
- page: 0
- size: 10

**Expected**: Successfully return Page<CustomerResponseDto> with pagination

**Result**: Normal, Passed

---

### UTCID01: Get Customer By Phone - Normal Flow
**English**: Test getting customer detail by phone successfully.

**Tiếng Việt**: Kiểm thử lấy chi tiết khách hàng theo số điện thoại thành công.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: Yes (phone: "0909000000")

**Input**:
- phone: "0909000000"

**Expected**: Successfully return CustomerDetailResponseDto

**Result**: Normal, Passed

---

### UTCID02: Get Customer By Phone - Not Found
**English**: Test getting customer detail when phone does not exist should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử lấy chi tiết khách hàng khi số điện thoại không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: No (phone: "not-exist")

**Input**:
- phone: "not-exist"

**Expected**: ResourceNotFoundException

**Result**: Abnormal, Passed

