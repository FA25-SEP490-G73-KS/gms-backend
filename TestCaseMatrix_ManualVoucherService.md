# Test Case Matrix: Manual Voucher Service Functions / Ma Trận Test Case: Các Chức Năng Manual Voucher Service

## Test Case Matrix: Approve Voucher

| Precondition | UTCID01 | UTCID02 | UTCID03 |
|--------------|---------|---------|---------|
| **Can connect with server** | O | O | O |
| **Ledger Voucher** | | | | |
| Existing {id: 1, status: PENDING} | O | | |
| Existing {id: 1, status: APPROVED} | | O | |
| Does not exist {id: 999} | | | O |
| **Input Parameters** | | | | |
| **voucherId** | | | | |
| "1" (valid) | O | O | |
| "999" (not exist) | | | O |
| **approverId** | | | | |
| "99" (valid) | O | O | |
| **Expected Outcome** | | | | |
| **Return** | | | | |
| Successfully (LedgerVoucherDetailResponse) | O | | |
| **Exception** | | | | |
| ResourceNotFoundException | | | O |
| RuntimeException (not PENDING) | | O | |
| **Voucher Status** | | | | |
| Updated to APPROVED | O | | |
| approvedBy and approvedAt set | O | | |
| **Result** | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A |
| Passed/Failed | P | P | P |

---

## Test Case Matrix: Create Voucher

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Stock Receipt Item** | | | |
| Existing {id: 100, has actualUnitPrice} | O | |
| Does not exist {id: 999} | | O |
| **Supplier** | | | |
| Existing {supplierId: 5} | O | |
| **Input Parameters** | | | | |
| **itemId** | | | | |
| "100" (valid) | O | |
| "999" (not exist) | | O |
| **request.relatedSupplierId** | | | | |
| "5" (valid) | O | |
| **Expected Outcome** | | | | |
| **Return** | | | | |
| Successfully (LedgerVoucherDetailResponse) | O | |
| **Exception** | | | | |
| ResourceNotFoundException | | O |
| **Voucher** | | | | |
| Created with code (PAY-2025-00001) | O | |
| Type: STOCK_RECEIPT_PAYMENT | O | |
| Amount calculated from item | O | |
| Status: APPROVED | O | |
| **Item** | | | | |
| Marked as paid | O | |
| **Result** | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Approve Voucher - Normal Flow
**English**: Test approving voucher successfully. Voucher status updated to APPROVED.

**Tiếng Việt**: Kiểm thử phê duyệt phiếu thành công. Trạng thái phiếu được cập nhật thành APPROVED.

**Preconditions**:
- Can connect with server: Yes
- Ledger Voucher exists: Yes (id: 1, status: PENDING)

**Input**:
- voucherId: "1"
- approverId: "99"

**Expected**: 
- Successfully return LedgerVoucherDetailResponse
- Voucher status updated to APPROVED
- approvedBy and approvedAt set

**Result**: Normal, Passed

---

### UTCID02: Approve Voucher - Already Approved
**English**: Test approving voucher that is already approved should throw RuntimeException.

**Tiếng Việt**: Kiểm thử phê duyệt phiếu đã được phê duyệt sẽ ném RuntimeException.

**Preconditions**:
- Can connect with server: Yes
- Ledger Voucher exists: Yes (id: 1, status: APPROVED)

**Input**:
- voucherId: "1"
- approverId: "99"

**Expected**: RuntimeException (voucher not in PENDING status)

**Result**: Abnormal, Passed

---

### UTCID01: Create Voucher - Normal Flow
**English**: Test creating payment voucher for stock receipt item successfully.

**Tiếng Việt**: Kiểm thử tạo phiếu chi cho mặt hàng phiếu nhập kho thành công.

**Preconditions**:
- Can connect with server: Yes
- Stock Receipt Item exists: Yes (id: 100, has actualUnitPrice)
- Supplier exists: Yes (supplierId: 5)

**Input**:
- itemId: "100"
- request: {relatedSupplierId: 5}

**Expected**: 
- Successfully return LedgerVoucherDetailResponse
- Voucher created with code "PAY-2025-00001"
- Type: STOCK_RECEIPT_PAYMENT
- Amount calculated from item (actualUnitPrice × quantityReceived)
- Status: APPROVED
- Item marked as paid

**Result**: Normal, Passed

