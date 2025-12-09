# Test Case Matrix: Warehouse Quotation Service Functions / Ma Trận Test Case: Các Chức Năng Warehouse Quotation Service

## Test Case Matrix: Get Pending Quotations

| Precondition | UTCID01 |
|--------------|---------|
| **Can connect with server** | O |
| **Price Quotations** | | |
| Has quotations with PART items | O |
| **Items Status** | | |
| PART + UNKNOWN | O | |
| PART + OUT_OF_STOCK | O | |
| PART + AVAILABLE | O | |
| SERVICE | O | |
| **Input Parameters** | | |
| **page** | | |
| 0 (first page) | O |
| **size** | | |
| 5 (valid) | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (Page<PriceQuotationResponseDto>) | O |
| Filtered: Only PART items with UNKNOWN or OUT_OF_STOCK | O |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N |
| Passed/Failed | P | P |

---

## Test Case Matrix: Confirm Item During Warehouse Review

| Precondition | UTCID01 | UTCID02 | UTCID03 |
|--------------|---------|---------|---------|
| **Can connect with server** | O | O | O |
| **Price Quotation Item** | | | |
| Existing {id: 1, has part, status: PENDING} | O | | |
| Does not exist {id: 999} | | O | |
| Part is null | | | O |
| **Part** | | | | |
| Has inventory (quantityInStock: 10, reserved: 2) | O | | |
| **Input Parameters** | | | | |
| **itemId** | | | | |
| "1" (valid) | O | | |
| "999" (not exist) | | O | |
| "2" (part null) | | | O |
| **note** | | | | |
| "Kho xác nhận đủ" | O | | |
| null | | O | O |
| **Expected Outcome** | | | | |
| **Return** | | | | |
| Successfully (PriceQuotationItemResponseDto) | O | | |
| **Exception** | | | | |
| ResourceNotFoundException | | O | |
| IllegalStateException | | | O |
| **Item Status** | | | | |
| warehouseReviewStatus: CONFIRMED | O | | |
| inventoryStatus: AVAILABLE | O | | |
| **Quotation** | | | | |
| Estimate amount recalculated | O | | |
| **Result** | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A |
| Passed/Failed | P | P | P |

---

## Test Case Matrix: Reject Item During Warehouse Review

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Price Quotation Item** | | | |
| Existing {id: 1} | O | |
| Does not exist {id: 999} | | O |
| **Input Parameters** | | | |
| **itemId** | | | |
| "1" (valid) | O | |
| "999" (not exist) | | O |
| **note** | | | |
| "Kho không có hàng" | O | |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (PriceQuotationItemResponseDto) | O | |
| **Exception** | | | |
| ResourceNotFoundException | | O |
| **Item Status** | | | |
| warehouseReviewStatus: REJECTED | O | |
| warehouseNote set | O | |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |

---

## Test Case Matrix: Create Part During Warehouse Review

| Precondition | UTCID01 | UTCID02 | UTCID03 |
|--------------|---------|---------|---------|
| **Can connect with server** | O | O | O |
| **Price Quotation Item** | | | | |
| Existing {id: 1, part: null} | O | | |
| Existing {id: 2, has part} | | O | |
| Does not exist {id: 999} | | | O |
| **Part Service** | | | | |
| Can create part | O | | |
| **Input Parameters** | | | | |
| **itemId** | | | | |
| "1" (valid, no part) | O | | |
| "2" (has part) | | O | |
| "999" (not exist) | | | O |
| **partDto** | | | | |
| Valid PartUpdateReqDto | O | | |
| **Expected Outcome** | | | | |
| **Return** | | | | |
| Successfully (PriceQuotationItemResponseDto) | O | | |
| **Exception** | | | | |
| ResourceNotFoundException | | | O |
| IllegalStateException (already has part) | | O | |
| **Part** | | | | |
| Created and linked to item | O | | |
| **Result** | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A |
| Passed/Failed | P | P | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Get Pending Quotations - Normal Flow
**English**: Test getting pending quotations filtered to show only PART items with UNKNOWN or OUT_OF_STOCK status.

**Tiếng Việt**: Kiểm thử lấy các báo giá đang chờ, lọc chỉ hiển thị items PART có trạng thái UNKNOWN hoặc OUT_OF_STOCK.

**Preconditions**:
- Can connect with server: Yes
- Price Quotations exist: Yes (has PART items with various statuses)

**Input**:
- page: 0
- size: 5

**Expected**: 
- Successfully return Page<PriceQuotationResponseDto>
- Filtered items: Only PART items with UNKNOWN or OUT_OF_STOCK
- SERVICE items and PART items with AVAILABLE are excluded

**Result**: Normal, Passed

---

### UTCID01: Confirm Item During Warehouse Review - Normal Flow
**English**: Test confirming item during warehouse review successfully. Item status updated to CONFIRMED and AVAILABLE.

**Tiếng Việt**: Kiểm thử xác nhận item trong quá trình kho xem xét thành công. Trạng thái item được cập nhật thành CONFIRMED và AVAILABLE.

**Preconditions**:
- Can connect with server: Yes
- Price Quotation Item exists: Yes (id: 1, has part, status: PENDING)
- Part has inventory: Yes (quantityInStock: 10, reserved: 2)

**Input**:
- itemId: "1"
- note: "Kho xác nhận đủ"

**Expected**: 
- Successfully return PriceQuotationItemResponseDto
- warehouseReviewStatus updated to CONFIRMED
- inventoryStatus updated to AVAILABLE
- warehouseNote set
- Quotation estimate amount recalculated

**Result**: Normal, Passed

---

### UTCID02: Confirm Item During Warehouse Review - Item Not Found
**English**: Test confirming item when item does not exist should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử xác nhận item khi item không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Price Quotation Item exists: No (id: 999)

**Input**:
- itemId: "999"
- note: null

**Expected**: ResourceNotFoundException

**Result**: Abnormal, Passed

---

### UTCID03: Confirm Item During Warehouse Review - Part Null
**English**: Test confirming item when part is null should throw IllegalStateException.

**Tiếng Việt**: Kiểm thử xác nhận item khi part là null sẽ ném IllegalStateException.

**Preconditions**:
- Can connect with server: Yes
- Price Quotation Item exists: Yes (id: 2, part: null)

**Input**:
- itemId: "2"
- note: null

**Expected**: IllegalStateException

**Result**: Abnormal, Passed

---

### UTCID01: Reject Item During Warehouse Review - Normal Flow
**English**: Test rejecting item during warehouse review successfully. Item status updated to REJECTED.

**Tiếng Việt**: Kiểm thử từ chối item trong quá trình kho xem xét thành công. Trạng thái item được cập nhật thành REJECTED.

**Preconditions**:
- Can connect with server: Yes
- Price Quotation Item exists: Yes (id: 1)

**Input**:
- itemId: "1"
- note: "Kho không có hàng"

**Expected**: 
- Successfully return PriceQuotationItemResponseDto
- warehouseReviewStatus updated to REJECTED
- warehouseNote set

**Result**: Normal, Passed

---

### UTCID01: Create Part During Warehouse Review - Normal Flow
**English**: Test creating part during warehouse review when item has no part successfully.

**Tiếng Việt**: Kiểm thử tạo linh kiện trong quá trình kho xem xét khi item chưa có part thành công.

**Preconditions**:
- Can connect with server: Yes
- Price Quotation Item exists: Yes (id: 1, part: null)
- Part Service can create part: Yes

**Input**:
- itemId: "1"
- partDto: Valid PartUpdateReqDto

**Expected**: 
- Successfully return PriceQuotationItemResponseDto
- New part created
- Part linked to item

**Result**: Normal, Passed

---

### UTCID02: Create Part During Warehouse Review - Item Already Has Part
**English**: Test creating part when item already has part should throw IllegalStateException.

**Tiếng Việt**: Kiểm thử tạo linh kiện khi item đã có part sẽ ném IllegalStateException.

**Preconditions**:
- Can connect with server: Yes
- Price Quotation Item exists: Yes (id: 2, has part)

**Input**:
- itemId: "2"
- partDto: Valid PartUpdateReqDto

**Expected**: IllegalStateException ("Item already has Part")

**Result**: Abnormal, Passed

