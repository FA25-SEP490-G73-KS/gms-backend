# Test Case Matrix: Get Purchase Requests / Ma Trận Test Case: Lấy Danh Sách Yêu Cầu Mua Hàng

## Test Case Matrix

| Precondition | UTCID01 | UTCID02 | UTCID03 | UTCID04 | UTCID05 |
|--------------|---------|---------|---------|---------|---------|
| **Can connect with server** | O | O | O | O | O |
| **Purchase Request Repository** | | | | | |
| Has data | O | O | O | O | |
| Empty | | | | | O |
| **Input Parameters** | | | | | |
| **keyword** | | | | | |
| null | O | | | | O |
| "PR-2025" (valid) | | O | | | |
| "BG-2025" (valid) | | | O | | |
| "" (empty) | | | | O | |
| **status** | | | | | |
| null | O | O | O | O | O |
| "Chờ duyệt" (PENDING) | | | | | |
| "Đã duyệt" (APPROVED) | | | | | |
| **fromDate** | | | | | |
| null | O | O | O | O | O |
| "2025-01-01" (valid) | | | | | |
| **toDate** | | | | | |
| null | O | O | O | O | O |
| "2025-12-31" (valid) | | | | | |
| **page** | | | | | |
| 0 (first page) | O | O | O | O | O |
| **size** | | | | | |
| 5 (valid) | O | O | O | O | O |
| **Expected Outcome** | | | | | |
| **Return** | | | | | |
| Successfully (Page<PurchaseRequestResponseDto>) | O | O | O | O | O |
| Empty page | | | | | O |
| **Exception** | | | | | |
| | | | | | |
| **Log message** | | | | | |
| | | | | | |
| **Result** | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N |
| Passed/Failed | P | P | P | P | P |
| Executed Date | | | | | |
| Defect ID | | | | | |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Get Purchase Requests - Normal Flow without Filters
**English**: Test getting purchase requests list successfully without any filters (keyword, status, dates are null).

**Tiếng Việt**: Kiểm thử lấy danh sách yêu cầu mua hàng thành công không có bộ lọc nào (keyword, status, dates là null).

**Preconditions**:
- Can connect with server: Yes
- Purchase Request Repository has data: Yes

**Input**:
- keyword: null
- status: null
- fromDate: null
- toDate: null
- page: 0
- size: 5

**Expected**: Successfully return Page<PurchaseRequestResponseDto> with data

**Result**: Normal, Passed

---

### UTCID02: Get Purchase Requests - With Keyword Filter (PR code)
**English**: Test getting purchase requests list with keyword filter matching PR code.

**Tiếng Việt**: Kiểm thử lấy danh sách yêu cầu mua hàng với bộ lọc keyword khớp mã PR.

**Preconditions**:
- Can connect with server: Yes
- Purchase Request Repository has data: Yes

**Input**:
- keyword: "PR-2025"
- status: null
- fromDate: null
- toDate: null
- page: 0
- size: 5

**Expected**: Successfully return Page<PurchaseRequestResponseDto> filtered by keyword

**Result**: Normal, Passed

---

### UTCID03: Get Purchase Requests - With Keyword Filter (Quotation code)
**English**: Test getting purchase requests list with keyword filter matching quotation code.

**Tiếng Việt**: Kiểm thử lấy danh sách yêu cầu mua hàng với bộ lọc keyword khớp mã báo giá.

**Preconditions**:
- Can connect with server: Yes
- Purchase Request Repository has data: Yes

**Input**:
- keyword: "BG-2025"
- status: null
- fromDate: null
- toDate: null
- page: 0
- size: 5

**Expected**: Successfully return Page<PurchaseRequestResponseDto> filtered by keyword

**Result**: Normal, Passed

---

### UTCID04: Get Purchase Requests - With Empty Keyword
**English**: Test getting purchase requests list with empty keyword string.

**Tiếng Việt**: Kiểm thử lấy danh sách yêu cầu mua hàng với keyword rỗng.

**Preconditions**:
- Can connect with server: Yes
- Purchase Request Repository has data: Yes

**Input**:
- keyword: ""
- status: null
- fromDate: null
- toDate: null
- page: 0
- size: 5

**Expected**: Successfully return Page<PurchaseRequestResponseDto> (empty keyword treated as no filter)

**Result**: Normal, Passed

---

### UTCID05: Get Purchase Requests - Empty Result
**English**: Test getting purchase requests list when repository is empty.

**Tiếng Việt**: Kiểm thử lấy danh sách yêu cầu mua hàng khi repository rỗng.

**Preconditions**:
- Can connect with server: Yes
- Purchase Request Repository has data: No (empty)

**Input**:
- keyword: null
- status: null
- fromDate: null
- toDate: null
- page: 0
- size: 5

**Expected**: Successfully return empty Page<PurchaseRequestResponseDto>

**Result**: Normal, Passed

