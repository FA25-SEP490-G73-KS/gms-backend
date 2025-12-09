# Test Case Matrix: Get Purchase Request Detail / Ma Trận Test Case: Lấy Chi Tiết Yêu Cầu Mua Hàng

## Test Case Matrix

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Purchase Request** | | |
| Existing {id: 1, code: "PR-2025-00001"} | O | |
| Does not exist {id: 999} | | O |
| **Input Parameters** | | |
| **id** | | |
| "1" (valid) | O | |
| "999" (not exist) | | O |
| null | | |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (PurchaseRequestDetailDto) | O | |
| **Exception** | | |
| ResourceNotFoundException ("Không tìm thấy yêu cầu mua hàng") | | O |
| **Log message** | | |
| | | |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |
| Executed Date | | |
| Defect ID | | |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Get Purchase Request Detail - Normal Flow
**English**: Test getting purchase request detail successfully when purchase request exists.

**Tiếng Việt**: Kiểm thử lấy chi tiết yêu cầu mua hàng thành công khi yêu cầu mua hàng tồn tại.

**Preconditions**:
- Can connect with server: Yes
- Purchase Request exists: Yes (id: 1, code: "PR-2025-00001")

**Input**:
- id: "1"

**Expected**: Successfully return PurchaseRequestDetailDto with all details including items, quotation code, customer name, etc.

**Result**: Normal, Passed

---

### UTCID02: Get Purchase Request Detail - Not Found
**English**: Test getting purchase request detail when purchase request does not exist should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử lấy chi tiết yêu cầu mua hàng khi yêu cầu mua hàng không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Purchase Request exists: No (id: 999)

**Input**:
- id: "999"

**Expected**: ResourceNotFoundException ("Không tìm thấy yêu cầu mua hàng")

**Result**: Abnormal, Passed

