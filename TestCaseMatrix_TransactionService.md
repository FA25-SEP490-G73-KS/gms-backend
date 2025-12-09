# Test Case Matrix: Transaction Service Functions / Ma Trận Test Case: Các Chức Năng Transaction Service

## Test Case Matrix: Create Transaction

| Precondition | UTCID01 | UTCID02 | UTCID03 |
|--------------|---------|---------|---------|
| **Can connect with server** | O | O | O |
| **Transaction Method** | | | | |
| CASH | O | | |
| BANK_TRANSFER | | O | |
| **Invoice** | | | | |
| Existing (for BANK_TRANSFER) | | O | |
| **PayOS Service** | | | | |
| Can create payment link | | O | |
| **Input Parameters** | | | | |
| **customerFullName** | | | | |
| "Customer Name" | O | O | |
| **customerPhone** | | | | |
| "0912345678" | O | O | |
| **price** | | | | |
| 1000000L (valid) | O | | |
| 2000000L (valid) | | O | |
| **type** | | | | |
| PAYMENT | O | O | |
| **method** | | | | |
| CASH | O | | |
| BANK_TRANSFER | | O | |
| **invoice** | | | | |
| null (CASH) | O | | |
| Existing (BANK_TRANSFER) | | O | |
| **Expected Outcome** | | | | |
| **Return** | | | | |
| Successfully (TransactionResponseDto) | O | O | |
| **Transaction** | | | | |
| isActive = true (CASH) | O | | |
| isActive = false (BANK_TRANSFER) | | O | |
| paymentLinkId set (BANK_TRANSFER) | | O | |
| paymentUrl returned (BANK_TRANSFER) | | O | |
| **Result** | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | |
| Passed/Failed | P | P | P |

---

## Test Case Matrix: Handle Callback

| Precondition | UTCID01 | UTCID02 | UTCID03 |
|--------------|---------|---------|---------|
| **Can connect with server** | O | O | O |
| **Transaction** | | | | |
| Existing {paymentLinkId: "payment-link-123", isActive: false} | O | O | |
| Does not exist | | | O |
| **Payment Status** | | | | |
| PAID | O | | |
| CANCELLED | | O | |
| **Invoice** | | | | |
| Existing (linked to transaction) | O | | |
| **Debt** | | | | |
| Existing (linked to transaction) | | O | |
| **Input Parameters** | | | | |
| **paymentLinkId** | | | | |
| "payment-link-123" (valid) | O | O | |
| "not-exist" | | | O |
| **Expected Outcome** | | | | |
| **Return** | | | | |
| Successfully | O | O | |
| **Exception** | | | | |
| TransactionNotFoundException | | | O |
| **Transaction** | | | | |
| isActive = true (PAID) | O | | |
| isActive = false (CANCELLED) | | O | |
| **Invoice** | | | | |
| Status updated to PAID_IN_FULL | O | | |
| depositReceived updated | O | | |
| **Debt** | | | | |
| Updated (paidAmount, status) | | O | |
| **Customer Spending** | | | | |
| Updated (PAID) | O | O | |
| **Result** | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | A |
| Passed/Failed | P | P | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Create Transaction - Cash Method
**English**: Test creating cash transaction successfully. Transaction is immediately active.

**Tiếng Việt**: Kiểm thử tạo giao dịch tiền mặt thành công. Giao dịch được kích hoạt ngay lập tức.

**Preconditions**:
- Can connect with server: Yes
- Transaction Method: CASH

**Input**:
- customerFullName: "Customer Name"
- customerPhone: "0912345678"
- price: 1000000L
- type: PAYMENT
- method: CASH
- invoice: null

**Expected**: 
- Successfully return TransactionResponseDto
- Transaction created with isActive = true
- No PayOS interaction

**Result**: Normal, Passed

---

### UTCID02: Create Transaction - Bank Transfer Method
**English**: Test creating bank transfer transaction successfully. Payment link created via PayOS.

**Tiếng Việt**: Kiểm thử tạo giao dịch chuyển khoản thành công. Liên kết thanh toán được tạo qua PayOS.

**Preconditions**:
- Can connect with server: Yes
- Transaction Method: BANK_TRANSFER
- Invoice exists: Yes
- PayOS Service can create payment link: Yes

**Input**:
- customerFullName: "Customer Name"
- customerPhone: "0912345678"
- price: 2000000L
- type: PAYMENT
- method: BANK_TRANSFER
- invoice: Existing invoice

**Expected**: 
- Successfully return TransactionResponseDto
- Transaction created with isActive = false
- paymentLinkId set
- paymentUrl returned in response
- PayOS payment link created

**Result**: Normal, Passed

---

### UTCID01: Handle Callback - Paid Status with Invoice
**English**: Test handling callback when payment is PAID. Invoice status updated and customer spending updated.

**Tiếng Việt**: Kiểm thử xử lý callback khi thanh toán là PAID. Trạng thái hóa đơn được cập nhật và chi tiêu khách hàng được cập nhật.

**Preconditions**:
- Can connect with server: Yes
- Transaction exists: Yes (paymentLinkId: "payment-link-123", isActive: false)
- Payment Status: PAID
- Invoice exists: Yes (linked to transaction)

**Input**:
- paymentLinkId: "payment-link-123"

**Expected**: 
- Transaction isActive updated to true
- Invoice status updated to PAID_IN_FULL
- depositReceived updated
- Customer spending updated

**Result**: Normal, Passed

---

### UTCID02: Handle Callback - Cancelled Status with Debt
**English**: Test handling callback when payment is CANCELLED. Transaction remains inactive, debt not updated.

**Tiếng Việt**: Kiểm thử xử lý callback khi thanh toán là CANCELLED. Giao dịch vẫn không hoạt động, công nợ không được cập nhật.

**Preconditions**:
- Can connect with server: Yes
- Transaction exists: Yes (paymentLinkId: "payment-link-123", isActive: false)
- Payment Status: CANCELLED
- Debt exists: Yes (linked to transaction)

**Input**:
- paymentLinkId: "payment-link-123"

**Expected**: 
- Transaction isActive remains false
- Debt not updated
- Customer spending not updated

**Result**: Normal, Passed

---

### UTCID03: Handle Callback - Transaction Not Found
**English**: Test handling callback when transaction does not exist should throw TransactionNotFoundException.

**Tiếng Việt**: Kiểm thử xử lý callback khi giao dịch không tồn tại sẽ ném TransactionNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Transaction exists: No

**Input**:
- paymentLinkId: "not-exist"

**Expected**: TransactionNotFoundException

**Result**: Abnormal, Passed

