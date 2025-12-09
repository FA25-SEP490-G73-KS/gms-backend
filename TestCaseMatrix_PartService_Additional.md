# Test Case Matrix: Part Service Additional Functions / Ma Trận Test Case: Các Chức Năng Bổ Sung Part Service

## Test Case Matrix: Get All Parts

| Precondition | UTCID01 | UTCID02 | UTCID03 | UTCID04 | UTCID05 |
|--------------|---------|---------|---------|---------|---------|
| **Can connect with server** | O | O | O | O | O |
| **Parts** | | | | | |
| Has parts | O | O | O | O | |
| Empty | | | | | O |
| **Input Parameters** | | | | | |
| **page** | | | | | |
| 0 (first page) | O | O | O | O | O |
| **size** | | | | | |
| 5 (valid) | O | O | O | O | O |
| **categoryId** | | | | | |
| null | O | | | | O |
| 1 (valid) | | O | | | |
| **status** | | | | | |
| null | O | O | | | O |
| IN_STOCK | | | O | | |
| **categoryId AND status** | | | | | |
| Both provided | | | | O | |
| **Expected Outcome** | | | | | |
| **Return** | | | | | |
| Successfully (Page<PartReqDto>) | O | O | O | O | O |
| Empty page | | | | | O |
| Stock status updated if needed | O | O | O | O | |
| **Result** | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N |
| Passed/Failed | P | P | P | P | P |

---

## Test Case Matrix: Get Part By ID

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Part** | | |
| Existing {id: 1} | O | |
| Does not exist {id: 999} | | O |
| **Input Parameters** | | |
| **id** | | |
| "1" (valid) | O | |
| "999" (not exist) | | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (PartReqDto) | O | |
| null | | O |
| Stock status updated if needed | O | |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N |
| Passed/Failed | P | P |

---

## Test Case Matrix: Update Part

| Precondition | UTCID01 | UTCID02 | UTCID03 |
|--------------|---------|---------|---------|
| **Can connect with server** | O | O | O |
| **Part** | | | |
| Existing {id: 1} | O | O | |
| Does not exist {id: 999} | | | O |
| **Input Parameters** | | | |
| **id** | | | |
| "1" (valid) | O | O | |
| "999" (not exist) | | | O |
| **dto fields** | | | |
| Partial update (only name) | O | | |
| Full update | | O | |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (PartReqDto) | O | O | |
| **Exception** | | | |
| ResourceNotFoundException | | | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | A |
| Passed/Failed | P | P | P |

---

## Test Case Matrix: Get Part By Category

| Precondition | UTCID01 |
|--------------|---------|
| **Can connect with server** | O |
| **Parts** | | |
| Has parts in category | O |
| **Input Parameters** | | |
| **categoryId** | | |
| 1 (valid) | O |
| **page** | | |
| 0 (first page) | O |
| **size** | | |
| 5 (valid) | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (Page<PartReqDto>) | O |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N |
| Passed/Failed | P | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Get All Parts - No Filters
**English**: Test getting all parts without filters successfully.

**Tiếng Việt**: Kiểm thử lấy tất cả linh kiện không có bộ lọc thành công.

**Preconditions**:
- Can connect with server: Yes
- Parts exist: Yes

**Input**:
- page: 0
- size: 5
- categoryId: null
- status: null

**Expected**: Successfully return Page<PartReqDto> using findAll()

**Result**: Normal, Passed

---

### UTCID02: Get All Parts - With Category Filter
**English**: Test getting all parts filtered by category successfully.

**Tiếng Việt**: Kiểm thử lấy tất cả linh kiện lọc theo danh mục thành công.

**Preconditions**:
- Can connect with server: Yes
- Parts exist: Yes

**Input**:
- page: 0
- size: 5
- categoryId: 1
- status: null

**Expected**: Successfully return Page<PartReqDto> using findByCategory_Id()

**Result**: Normal, Passed

---

### UTCID03: Get All Parts - With Status Filter
**English**: Test getting all parts filtered by status successfully.

**Tiếng Việt**: Kiểm thử lấy tất cả linh kiện lọc theo trạng thái thành công.

**Preconditions**:
- Can connect with server: Yes
- Parts exist: Yes

**Input**:
- page: 0
- size: 5
- categoryId: null
- status: IN_STOCK

**Expected**: Successfully return Page<PartReqDto> using findByStatus()

**Result**: Normal, Passed

---

### UTCID04: Get All Parts - With Both Filters
**English**: Test getting all parts filtered by both category and status successfully.

**Tiếng Việt**: Kiểm thử lấy tất cả linh kiện lọc theo cả danh mục và trạng thái thành công.

**Preconditions**:
- Can connect with server: Yes
- Parts exist: Yes

**Input**:
- page: 0
- size: 5
- categoryId: 1
- status: IN_STOCK

**Expected**: Successfully return Page<PartReqDto> using findByCategory_IdAndStatus()

**Result**: Normal, Passed

---

### UTCID05: Get All Parts - Empty Result
**English**: Test getting all parts when repository is empty.

**Tiếng Việt**: Kiểm thử lấy tất cả linh kiện khi repository rỗng.

**Preconditions**:
- Can connect with server: Yes
- Parts exist: No (empty)

**Input**:
- page: 0
- size: 5
- categoryId: null
- status: null

**Expected**: Successfully return empty Page<PartReqDto>

**Result**: Normal, Passed

---

### UTCID01: Get Part By ID - Normal Flow
**English**: Test getting part by ID successfully.

**Tiếng Việt**: Kiểm thử lấy linh kiện theo ID thành công.

**Preconditions**:
- Can connect with server: Yes
- Part exists: Yes (id: 1)

**Input**:
- id: "1"

**Expected**: Successfully return PartReqDto, stock status updated if needed

**Result**: Normal, Passed

---

### UTCID02: Get Part By ID - Not Found
**English**: Test getting part by ID when part does not exist should return null.

**Tiếng Việt**: Kiểm thử lấy linh kiện theo ID khi linh kiện không tồn tại sẽ trả về null.

**Preconditions**:
- Can connect with server: Yes
- Part exists: No (id: 999)

**Input**:
- id: "999"

**Expected**: Return null PartReqDto

**Result**: Normal, Passed

---

### UTCID01: Update Part - Partial Update
**English**: Test updating part with partial fields (only name) successfully.

**Tiếng Việt**: Kiểm thử cập nhật linh kiện với một phần trường (chỉ tên) thành công.

**Preconditions**:
- Can connect with server: Yes
- Part exists: Yes (id: 1)

**Input**:
- id: "1"
- dto: {name: "New Name", other fields: null}

**Expected**: Successfully return PartReqDto with updated name, other fields unchanged

**Result**: Normal, Passed

---

### UTCID02: Update Part - Full Update
**English**: Test updating part with all fields successfully.

**Tiếng Việt**: Kiểm thử cập nhật linh kiện với tất cả các trường thành công.

**Preconditions**:
- Can connect with server: Yes
- Part exists: Yes (id: 1)

**Input**:
- id: "1"
- dto: {all fields provided}

**Expected**: Successfully return PartReqDto with all fields updated, selling price auto-calculated, SKU regenerated

**Result**: Normal, Passed

---

### UTCID03: Update Part - Not Found
**English**: Test updating part when part does not exist should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử cập nhật linh kiện khi linh kiện không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Part exists: No (id: 999)

**Input**:
- id: "999"
- dto: {any fields}

**Expected**: ResourceNotFoundException ("Không tìm thấy linh kiện ID: 999")

**Result**: Abnormal, Passed

---

### UTCID01: Get Part By Category - Normal Flow
**English**: Test getting parts by category successfully.

**Tiếng Việt**: Kiểm thử lấy linh kiện theo danh mục thành công.

**Preconditions**:
- Can connect with server: Yes
- Parts exist: Yes (has parts in category)

**Input**:
- categoryId: 1
- page: 0
- size: 5

**Expected**: Successfully return Page<PartReqDto> filtered by category

**Result**: Normal, Passed

