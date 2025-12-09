# Test Case Matrix: Debt Service Functions / Ma Trận Test Case: Các Chức Năng Debt Service

## Test Case Matrix: Get All Debts Summary

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Debts** | | |
| Has debts | O | |
| Empty | | O |
| **Input Parameters** | | |
| **page** | | |
| 0 (first page) | O | O |
| **size** | | |
| 5 (valid) | O | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (Page<CustomerDebtSummaryDto>) | O | O |
| Empty page | | O |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N |
| Passed/Failed | P | P |

---

## Test Case Matrix: Get Debts By Customer

| Precondition | UTCID01 | UTCID02 | UTCID03 |
|--------------|---------|---------|---------|
| **Can connect with server** | O | O | O |
| **Customer** | | | |
| Existing {customerId: 10} | O | O | |
| Does not exist {customerId: 999} | | | O |
| **Debts** | | | |
| Has debts for customer | O | O | |
| **Input Parameters** | | | |
| **customerId** | | | |
| "10" (valid) | O | O | |
| "999" (not exist) | | | O |
| **status** | | | |
| OUTSTANDING | O | | |
| null | | O | |
| **keyword** | | | |
| null | O | | |
| " keyword " (with spaces) | | O | |
| **sort** | | | |
| null | O | | |
| "amount,asc" | | O | |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (DebtDetailResponseDto) | O | O | |
| **Exception** | | | |
| ResourceNotFoundException | | | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | A |
| Passed/Failed | P | P | P |

---

## Test Case Matrix: Create Debt

| Precondition | UTCID01 | UTCID02 | UTCID03 |
|--------------|---------|---------|---------|
| **Can connect with server** | O | O | O |
| **Customer** | | | |
| Existing {customerId: 10} | O | O | |
| Does not exist {customerId: 999} | | | O |
| **Service Ticket** | | | |
| Existing {serviceTicketId: 5} | O | O | |
| Does not exist {serviceTicketId: 999} | | | O |
| **Input Parameters** | | | |
| **customerId** | | | |
| "10" (valid) | O | O | |
| "999" (not exist) | | | O |
| **serviceTicketId** | | | |
| "5" (valid) | O | | |
| "999" (not exist) | | O | |
| **amount** | | | |
| "250000" (valid) | O | O | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (DebtDetailResponseDto) | O | | |
| **Exception** | | | |
| ResourceNotFoundException (Customer) | | | O |
| ResourceNotFoundException (ServiceTicket) | | O | |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A |
| Passed/Failed | P | P | P |

---

## Test Case Matrix: Pay Debt

| Precondition | UTCID01 | UTCID02 | UTCID03 | UTCID04 | UTCID05 |
|--------------|---------|---------|---------|---------|---------|
| **Can connect with server** | O | O | O | O | O |
| **Debt** | | | | | |
| Existing {id: 1, amount: 500000, paidAmount: 0} | O | O | O | O | |
| Existing {id: 1, amount: 500000, paidAmount: 100000} | | | | | |
| Does not exist {id: 999} | | | | | O |
| **Transaction Method** | | | | | |
| BANK_TRANSFER | O | | | | |
| CASH | | O | O | O | |
| **Payment Amount** | | | | | |
| Exact amount (500000) | O | | | O | |
| Partial (200000) | | O | | | |
| Over payment (600000) | | | O | | |
| **Expected Outcome** | | | | | |
| **Return** | | | | | |
| Successfully (TransactionResponseDto) | O | O | O | O | |
| **Exception** | | | | | |
| DebtNotFoundException | | | | | O |
| **Debt Status** | | | | | |
| Not updated (BANK_TRANSFER) | O | | | | |
| Updated to OUTSTANDING | | O | | | |
| Updated to PAID_IN_FULL | | | | O | |
| **Customer Spending** | | | | | |
| Updated (CASH) | | O | O | O | |
| Not updated (BANK_TRANSFER) | O | | | | |
| **Result** | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P |

---

## Test Case Matrix: Get Debt Detail By Service Ticket ID

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Service Ticket** | | |
| Existing {serviceTicketId: 5, has debt} | O | |
| Does not exist {serviceTicketId: 999} | | O |
| **Transactions** | | | |
| Has transactions | O | |
| Empty | | |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (ServiceTicketDebtDetail) | O | |
| **Exception** | | |
| ServiceTicketNotFoundException | | O |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Get All Debts Summary - Normal Flow
**English**: Test getting all debts summary grouped by customer successfully.

**Tiếng Việt**: Kiểm thử lấy tổng hợp tất cả công nợ nhóm theo khách hàng thành công.

**Preconditions**:
- Can connect with server: Yes
- Debts exist: Yes

**Input**:
- page: 0
- size: 5

**Expected**: Successfully return Page<CustomerDebtSummaryDto> with customer debt summaries

**Result**: Normal, Passed

---

### UTCID01: Get Debts By Customer - Normal Flow with Status Filter
**English**: Test getting debts by customer with OUTSTANDING status filter successfully.

**Tiếng Việt**: Kiểm thử lấy công nợ theo khách hàng với bộ lọc trạng thái OUTSTANDING thành công.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: Yes (customerId: 10)
- Debts exist: Yes (has debts for customer)

**Input**:
- customerId: "10"
- status: OUTSTANDING
- keyword: null
- sort: null

**Expected**: Successfully return DebtDetailResponseDto with customer debts and total remaining amount

**Result**: Normal, Passed

---

### UTCID02: Get Debts By Customer - With Keyword and Sort
**English**: Test getting debts by customer with keyword (trimmed) and sorting successfully.

**Tiếng Việt**: Kiểm thử lấy công nợ theo khách hàng với keyword (đã trim) và sắp xếp thành công.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: Yes (customerId: 10)
- Debts exist: Yes

**Input**:
- customerId: "10"
- status: null
- keyword: " keyword " (with spaces, should be trimmed)
- sort: "amount,asc"

**Expected**: Successfully return DebtDetailResponseDto with keyword trimmed and sorted

**Result**: Normal, Passed

---

### UTCID01: Create Debt - Normal Flow
**English**: Test creating debt successfully with valid customer and service ticket.

**Tiếng Việt**: Kiểm thử tạo công nợ thành công với khách hàng và phiếu dịch vụ hợp lệ.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: Yes (customerId: 10)
- Service Ticket exists: Yes (serviceTicketId: 5)

**Input**:
- customerId: "10"
- serviceTicketId: "5"
- amount: "250000"

**Expected**: 
- Successfully return DebtDetailResponseDto
- Debt saved with default dueDate (14 days from now)
- Status set to OUTSTANDING

**Result**: Normal, Passed

---

### UTCID01: Pay Debt - Bank Transfer (No Debt Update)
**English**: Test paying debt with bank transfer method. Debt should not be updated immediately.

**Tiếng Việt**: Kiểm thử thanh toán công nợ bằng phương thức chuyển khoản. Công nợ không được cập nhật ngay lập tức.

**Preconditions**:
- Can connect with server: Yes
- Debt exists: Yes (id: 1, amount: 500000, paidAmount: 0)

**Input**:
- debtId: "1"
- method: BANK_TRANSFER
- price: 500000L

**Expected**: 
- Successfully return TransactionResponseDto
- Transaction created with isActive = false
- Debt not updated
- Customer spending not updated

**Result**: Normal, Passed

---

### UTCID02: Pay Debt - Cash Partial Payment
**English**: Test paying debt with cash method, partial payment. Debt status remains OUTSTANDING.

**Tiếng Việt**: Kiểm thử thanh toán công nợ bằng tiền mặt, thanh toán một phần. Trạng thái công nợ vẫn là OUTSTANDING.

**Preconditions**:
- Can connect with server: Yes
- Debt exists: Yes (id: 1, amount: 500000, paidAmount: 100000)

**Input**:
- debtId: "1"
- method: CASH
- price: 200000L

**Expected**: 
- Successfully return TransactionResponseDto
- Debt paidAmount updated to 300000
- Debt status remains OUTSTANDING
- Customer spending updated

**Result**: Normal, Passed

---

### UTCID03: Pay Debt - Cash Over Payment
**English**: Test paying debt with cash method, over payment. Debt status becomes PAID_IN_FULL, excess updates customer spending.

**Tiếng Việt**: Kiểm thử thanh toán công nợ bằng tiền mặt, thanh toán vượt. Trạng thái công nợ trở thành PAID_IN_FULL, số tiền vượt cập nhật chi tiêu khách hàng.

**Preconditions**:
- Can connect with server: Yes
- Debt exists: Yes (id: 1, amount: 500000, paidAmount: 0)

**Input**:
- debtId: "1"
- method: CASH
- price: 600000L (over payment)

**Expected**: 
- Successfully return TransactionResponseDto
- Debt paidAmount updated to 500000
- Debt status updated to PAID_IN_FULL
- Customer spending updated with excess amount (100000)

**Result**: Normal, Passed

---

### UTCID04: Pay Debt - Cash Exact Payment
**English**: Test paying debt with cash method, exact payment amount. Debt status becomes PAID_IN_FULL.

**Tiếng Việt**: Kiểm thử thanh toán công nợ bằng tiền mặt, số tiền thanh toán chính xác. Trạng thái công nợ trở thành PAID_IN_FULL.

**Preconditions**:
- Can connect with server: Yes
- Debt exists: Yes (id: 1, amount: 500000, paidAmount: 0)

**Input**:
- debtId: "1"
- method: CASH
- price: 500000L (exact amount)

**Expected**: 
- Successfully return TransactionResponseDto
- Debt paidAmount updated to 500000
- Debt status updated to PAID_IN_FULL
- Customer spending updated

**Result**: Normal, Passed

---

### UTCID01: Get Debt Detail By Service Ticket ID - Normal Flow
**English**: Test getting debt detail by service ticket ID successfully.

**Tiếng Việt**: Kiểm thử lấy chi tiết công nợ theo ID phiếu dịch vụ thành công.

**Preconditions**:
- Can connect with server: Yes
- Service Ticket exists: Yes (serviceTicketId: 5, has debt)
- Transactions exist: Yes

**Input**:
- serviceTicketId: "5"

**Expected**: Successfully return ServiceTicketDebtDetail with debt and transaction history

**Result**: Normal, Passed

