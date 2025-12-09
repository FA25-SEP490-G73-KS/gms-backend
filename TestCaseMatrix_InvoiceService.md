# Test Case Matrix: Invoice Service Functions / Ma Trận Test Case: Các Chức Năng Invoice Service

## Test Case Matrix: Create Invoice

| Precondition | UTCID01 | UTCID02 | UTCID03 | UTCID04 |
|--------------|---------|---------|---------|---------|
| **Can connect with server** | O | O | O | O |
| **Service Ticket** | | | | |
| Existing {id: 10, has customer} | O | O | O | |
| Does not exist {id: 999} | | | | O |
| Customer is null | | | O | |
| **Price Quotation** | | | | |
| Existing {id: 100, estimateAmount: 1000000} | O | O | O | |
| Does not exist {id: 999} | | O | | |
| **Customer** | | | | |
| Has discount policy (10%) | O | O | | |
| **Previous Debt** | | | | |
| Has debt (200000) | O | | | |
| No debt (0) | | O | O | |
| **Input Parameters** | | | | |
| **serviceTicketId** | | | | |
| "10" (valid) | O | O | O | |
| "999" (not exist) | | | | O |
| **quotationId** | | | | |
| "100" (valid) | O | | | |
| "999" (not exist) | | O | | |
| **Expected Outcome** | | | | |
| **Return** | | | | |
| Successfully (Invoice created) | O | | | |
| **Exception** | | | | |
| ResourceNotFoundException (ServiceTicket) | | | | O |
| ResourceNotFoundException (Quotation) | | O | | |
| ResourceNotFoundException (Customer null) | | | O | |
| **Invoice Calculation** | | | | |
| finalAmount = itemTotal - discount + previousDebt | O | | | |
| Code generated (HD001) | O | | | |
| **Result** | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A | A |
| Passed/Failed | P | P | P | P |

---

## Test Case Matrix: Get Invoice List

| Precondition | UTCID01 |
|--------------|---------|
| **Can connect with server** | O |
| **Invoices** | | |
| Has invoices | O |
| **Input Parameters** | | |
| **page** | | |
| 0 (first page) | O |
| **size** | | |
| 5 (valid) | O |
| **sort** | | |
| "createdAt,desc" (valid) | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (Page<InvoiceListResDto>) | O |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N |
| Passed/Failed | P | P |

---

## Test Case Matrix: Get Invoice Detail

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Invoice** | | | |
| Existing {id: 1} | O | |
| Does not exist {id: 999} | | O |
| **Input Parameters** | | | |
| **invoiceId** | | | |
| "1" (valid) | O | |
| "999" (not exist) | | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (InvoiceDetailResDto) | O | |
| **Exception** | | | |
| ResourceNotFoundException | | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |

---

## Test Case Matrix: Pay Invoice

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Invoice** | | | |
| Existing {id: 1, status: PENDING} | O | |
| Does not exist {id: 999} | | O |
| **Input Parameters** | | | |
| **invoiceId** | | | |
| "1" (valid) | O | |
| "999" (not exist) | | O |
| **method** | | | |
| CASH | O | |
| BANK_TRANSFER | | |
| **price** | | | |
| Valid amount | O | |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (TransactionResponseDto) | O | |
| **Exception** | | | |
| PaymentNotFoundException | | O |
| **Invoice Status** | | | |
| Updated to PAID_IN_FULL | O | |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Create Invoice - Normal Flow with Discount and Debt
**English**: Test creating invoice successfully with discount policy and previous debt. finalAmount = itemTotal - discount + previousDebt.

**Tiếng Việt**: Kiểm thử tạo hóa đơn thành công với chính sách giảm giá và công nợ trước đó. finalAmount = itemTotal - discount + previousDebt.

**Preconditions**:
- Can connect with server: Yes
- Service Ticket exists: Yes (id: 10, has customer)
- Price Quotation exists: Yes (id: 100, estimateAmount: 1000000)
- Customer has discount policy: Yes (10%)
- Previous debt exists: Yes (200000)

**Input**:
- serviceTicketId: "10"
- quotationId: "100"

**Expected**: 
- Invoice created with code "HD001"
- finalAmount = 1,000,000 - 100,000 + 200,000 = 1,100,000
- depositReceived = 0

**Result**: Normal, Passed

---

### UTCID02: Create Invoice - Quotation Not Found
**English**: Test creating invoice when quotation does not exist should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử tạo hóa đơn khi báo giá không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Service Ticket exists: Yes (id: 10)
- Price Quotation exists: No (id: 999)

**Input**:
- serviceTicketId: "10"
- quotationId: "999"

**Expected**: ResourceNotFoundException

**Result**: Abnormal, Passed

---

### UTCID03: Create Invoice - Customer Null
**English**: Test creating invoice when service ticket has null customer should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử tạo hóa đơn khi phiếu dịch vụ có customer null sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Service Ticket exists: Yes (id: 10, customer: null)
- Price Quotation exists: Yes (id: 100)

**Input**:
- serviceTicketId: "10"
- quotationId: "100"

**Expected**: ResourceNotFoundException

**Result**: Abnormal, Passed

---

### UTCID01: Get Invoice List - Normal Flow
**English**: Test getting invoice list with pagination and sorting successfully.

**Tiếng Việt**: Kiểm thử lấy danh sách hóa đơn với phân trang và sắp xếp thành công.

**Preconditions**:
- Can connect with server: Yes
- Invoices exist: Yes

**Input**:
- page: 0
- size: 5
- sort: "createdAt,desc"

**Expected**: Successfully return Page<InvoiceListResDto> sorted by createdAt descending

**Result**: Normal, Passed

---

### UTCID01: Get Invoice Detail - Normal Flow
**English**: Test getting invoice detail successfully.

**Tiếng Việt**: Kiểm thử lấy chi tiết hóa đơn thành công.

**Preconditions**:
- Can connect with server: Yes
- Invoice exists: Yes (id: 1)

**Input**:
- invoiceId: "1"

**Expected**: Successfully return InvoiceDetailResDto with all invoice details

**Result**: Normal, Passed

---

### UTCID01: Pay Invoice - Normal Flow
**English**: Test paying invoice successfully with cash method.

**Tiếng Việt**: Kiểm thử thanh toán hóa đơn thành công bằng phương thức tiền mặt.

**Preconditions**:
- Can connect with server: Yes
- Invoice exists: Yes (id: 1, status: PENDING)

**Input**:
- invoiceId: "1"
- method: CASH
- price: Valid amount

**Expected**: 
- Successfully return TransactionResponseDto
- Invoice status updated to PAID_IN_FULL
- Customer spending updated

**Result**: Normal, Passed

