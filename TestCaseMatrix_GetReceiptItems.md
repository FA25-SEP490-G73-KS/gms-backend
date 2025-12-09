# Test Case Matrix: Get Receipt Items / Ma Trận Test Case: Lấy Danh Sách Mặt Hàng Phiếu Nhập Kho

## Test Case Matrix

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Stock Receipt** | | |
| Existing {receiptId: 1, has items} | O | |
| Does not exist {receiptId: 999} | | O |
| **Input Parameters** | | |
| **receiptId** | | |
| "1" (valid) | O | |
| "999" (not exist) | | O |
| **page** | | |
| 0 (first page) | O | O |
| **size** | | |
| 5 (valid) | O | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (Page<StockReceiptItemResponse>) | O | |
| **Exception** | | |
| ResourceNotFoundException | | O |
| **Log message** | | |
| | | |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |
| Executed Date | | |
| Defect ID | | |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Get Receipt Items - Normal Flow
**English**: Test getting receipt items successfully when receipt exists and has items.

**Tiếng Việt**: Kiểm thử lấy danh sách mặt hàng phiếu nhập kho thành công khi phiếu nhập kho tồn tại và có mặt hàng.

**Preconditions**:
- Can connect with server: Yes
- Stock Receipt exists: Yes (receiptId: 1, has items)

**Input**:
- receiptId: "1"
- page: 0
- size: 5

**Expected**: Successfully return Page<StockReceiptItemResponse> with items

**Result**: Normal, Passed

---

### UTCID02: Get Receipt Items - Receipt Not Found
**English**: Test getting receipt items when receipt does not exist should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử lấy danh sách mặt hàng phiếu nhập kho khi phiếu nhập kho không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Stock Receipt exists: No (receiptId: 999)

**Input**:
- receiptId: "999"
- page: 0
- size: 5

**Expected**: ResourceNotFoundException

**Result**: Abnormal, Passed

