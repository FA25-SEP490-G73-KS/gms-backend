# Test Case Matrix: Approve Purchase Request / Ma Trận Test Case: Phê Duyệt Yêu Cầu Mua Hàng

## Test Case Matrix

| Precondition | UTCID01 | UTCID02 | UTCID03 | UTCID04 | UTCID05 |
|--------------|---------|---------|---------|---------|---------|
| **Can connect with server** | O | O | O | O | O |
| **Purchase Request** | | | | | |
| Existing {id: 100, code: "PR-2025-00001", status: PENDING} | O | O | O | | O |
| Existing {id: 100, status: APPROVED} | | | | O | |
| Does not exist {id: 999} | | | | | |
| **Purchase Request Items** | | | | | |
| Has items (list not empty) | O | O | O | O | |
| No items (list empty) | | | O | | |
| **Stock Receipt Service** | | | | | |
| Can create receipt successfully | O | O | | O | |
| Fails to create receipt | | | O | | |
| **Input Parameters** | | | | | |
| **requestId** | | | | | |
| "100" (valid) | O | O | O | O | |
| "999" (not exist) | | | | | O |
| null | | | | | |
| **Expected Outcome** | | | | | |
| **Return** | | | | | |
| Successfully (PurchaseRequest with status APPROVED) | O | | | | |
| **Exception** | | | | | |
| ResourceNotFoundException ("Không tìm thấy yêu cầu mua hàng") | | | | | O |
| RuntimeException ("Phiếu yêu cầu mua hàng không có item") | | | O | | |
| RuntimeException (Stock receipt creation failed) | | O | | | |
| **Log message** | | | | | |
| Created stock receipt {} from purchase request {} | O | | | | |
| **Result** | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A | A | A |
| Passed/Failed | P | P | P | P | P |
| Executed Date | | | | | |
| Defect ID | | | | | |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Approve Purchase Request - Normal Flow
**English**: Test approving a purchase request successfully. The request has items, status is PENDING, and stock receipt is created successfully.

**Tiếng Việt**: Kiểm thử phê duyệt yêu cầu mua hàng thành công. Yêu cầu có items, trạng thái là PENDING và phiếu nhập kho được tạo thành công.

**Preconditions**:
- Can connect with server: Yes
- Purchase Request exists: Yes (id: 100, status: PENDING)
- Purchase Request has items: Yes (list not empty)
- Stock Receipt Service: Can create receipt successfully

**Input**:
- requestId: "100"

**Expected**: 
- Purchase Request status updated to APPROVED
- Stock Receipt created successfully
- Return PurchaseRequest entity

**Result**: Normal, Passed

---

### UTCID02: Approve Purchase Request - Stock Receipt Creation Failed
**English**: Test approving a purchase request when stock receipt creation fails. The request should still be approved but receipt creation fails.

**Tiếng Việt**: Kiểm thử phê duyệt yêu cầu mua hàng khi việc tạo phiếu nhập kho thất bại. Yêu cầu vẫn được phê duyệt nhưng việc tạo phiếu nhập kho thất bại.

**Preconditions**:
- Can connect with server: Yes
- Purchase Request exists: Yes (id: 100, status: PENDING)
- Purchase Request has items: Yes (list not empty)
- Stock Receipt Service: Fails to create receipt

**Input**:
- requestId: "100"

**Expected**: 
- RuntimeException (Stock receipt creation failed)
- Purchase Request may or may not be approved depending on transaction rollback

**Result**: Abnormal, Passed

---

### UTCID03: Approve Purchase Request - No Items
**English**: Test approving a purchase request with no items should throw RuntimeException.

**Tiếng Việt**: Kiểm thử phê duyệt yêu cầu mua hàng không có items sẽ ném RuntimeException.

**Preconditions**:
- Can connect with server: Yes
- Purchase Request exists: Yes (id: 100, status: PENDING)
- Purchase Request has items: No (list empty)

**Input**:
- requestId: "100"

**Expected**: RuntimeException ("Phiếu yêu cầu mua hàng không có item")

**Result**: Abnormal, Passed

---

### UTCID04: Approve Purchase Request - Already Approved
**English**: Test approving a purchase request that is already approved. The system should handle this appropriately (may allow re-approval or prevent it).

**Tiếng Việt**: Kiểm thử phê duyệt yêu cầu mua hàng đã được phê duyệt. Hệ thống nên xử lý điều này một cách phù hợp (có thể cho phép phê duyệt lại hoặc ngăn chặn).

**Preconditions**:
- Can connect with server: Yes
- Purchase Request exists: Yes (id: 100, status: APPROVED)
- Purchase Request has items: Yes (list not empty)
- Stock Receipt Service: Can create receipt successfully

**Input**:
- requestId: "100"

**Expected**: 
- May allow re-approval or throw exception
- Stock receipt may or may not be created again

**Result**: Abnormal, Passed

---

### UTCID05: Approve Purchase Request - Not Found
**English**: Test approving a non-existent purchase request should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử phê duyệt yêu cầu mua hàng không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Purchase Request exists: No (id: 999)

**Input**:
- requestId: "999"

**Expected**: ResourceNotFoundException ("Không tìm thấy yêu cầu mua hàng")

**Result**: Abnormal, Passed

