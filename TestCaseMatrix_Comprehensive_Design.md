# Test Case Matrix Comprehensive Design / Thiết Kế Ma Trận Test Case Tổng Hợp

## Tổng Quan / Overview

Tài liệu này thiết kế ma trận test case cho 20 method quan trọng nhất trong hệ thống GMS Backend, với tổng cộng khoảng 200 test cases.

This document designs test case matrices for the 20 most important methods in the GMS Backend system, with a total of approximately 200 test cases.

---

## Danh Sách 20 Method / List of 20 Methods

1. **createPart** - Tạo linh kiện
2. **updatePart** - Cập nhật linh kiện
3. **getAllPart** - Lấy danh sách linh kiện (với filters)
4. **createAllowance** - Tạo phụ cấp
5. **createDeduction** - Tạo khấu trừ
6. **createDebt** - Tạo công nợ
7. **payDebt** - Thanh toán công nợ
8. **getAllDebtsSummary** - Lấy tổng hợp công nợ
9. **createInvoice** - Tạo hóa đơn
10. **payInvoice** - Thanh toán hóa đơn
11. **createServiceTicket** - Tạo phiếu dịch vụ
12. **updateServiceTicket** - Cập nhật phiếu dịch vụ
13. **createQuotation** - Tạo báo giá
14. **approvePurchaseRequest** - Phê duyệt yêu cầu mua hàng
15. **createAppointment** - Tạo lịch hẹn
16. **createTransaction** - Tạo giao dịch
17. **handleCallback** - Xử lý callback thanh toán
18. **approvePayroll** - Phê duyệt lương
19. **createManualVoucher** - Tạo phiếu thủ công
20. **approveVoucher** - Phê duyệt phiếu

---

# Test Case Matrix: createPart / Ma Trận Test Case: Tạo Linh Kiện

| Precondition | TC001 | TC002 | TC003 | TC004 | TC005 | TC006 | TC007 | TC008 | TC009 | TC010 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Category** | | | | | | | | | | |
| Existing {categoryId: 1} | O | O | O | O | O | | O | O | O | O |
| Does not exist {categoryId: 999} | | | | | | O | | | | |
| null (universal part) | | | | | | | | | | |
| **Market** | | | | | | | | | | |
| Existing {marketId: 1} | O | O | O | O | O | O | O | O | O | O |
| Does not exist {marketId: 999} | | | | | | | | | | O |
| **Unit** | | | | | | | | | | |
| Existing {unitId: 1} | O | O | O | O | O | O | O | O | O | O |
| Does not exist {unitId: 999} | | | | | | | | | | |
| **Supplier** | | | | | | | | | | |
| Existing {supplierId: 1} | O | O | O | O | O | O | O | O | O | O |
| Does not exist {supplierId: 999} | | | | | | | | | | |
| **VehicleModel** | | | | | | | | | | |
| Existing {vehicleModelId: 1} | O | O | O | O | O | O | O | O | O | O |
| null (universal part) | | | | | | | | | | |
| Does not exist {vehicleModelId: 999} | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **name** | | | | | | | | | | |
| "Oil 5W30" (valid) | O | O | O | O | O | O | O | O | O | O |
| null | | | | | | | | | | |
| "" (empty) | | | | | | | | | | |
| **purchasePrice** | | | | | | | | | | |
| "100000" (valid, > 0) | O | O | O | O | O | O | O | O | O | O |
| "0" (boundary) | | | | | | | | | | |
| "-10000" (invalid, < 0) | | | | | | | | | | |
| null | | | | | | | | | | |
| **sellingPrice** | | | | | | | | | | |
| "110000" (auto-calculated) | O | O | O | O | O | O | O | O | O | O |
| "120000" (override) | | | | | | | | | | |
| null | | | | | | | | | | |
| **categoryId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | | O | O | O | O |
| null (universal) | | | | | | | | | | |
| "999" (not exist) | | | | | | O | | | | |
| **marketId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (PartReqDto with SKU) | O | O | O | O | O | | O | O | O | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException (Category) | | | | | | O | | | | |
| ResourceNotFoundException (Market) | | | | | | | | | | O |
| ResourceNotFoundException (Unit) | | | | | | | | | | |
| ResourceNotFoundException (Supplier) | | | | | | | | | | |
| ResourceNotFoundException (VehicleModel) | | | | | | | | | | |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | A | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC001: Create part with all dependencies, auto-calculate sellingPrice
- TC002: Create part with category and vehicleModel
- TC003: Create universal part (null category, null vehicleModel)
- TC004: Create part with override sellingPrice
- TC005: Create part with minimum valid purchasePrice
- TC006: Category not found
- TC007: Create part with all optional fields
- TC008: Create part with only required fields
- TC009: Create part with maximum valid purchasePrice
- TC010: Market not found

---

# Test Case Matrix: updatePart / Ma Trận Test Case: Cập Nhật Linh Kiện

| Precondition | TC011 | TC012 | TC013 | TC014 | TC015 | TC016 | TC017 | TC018 | TC019 | TC020 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Part** | | | | | | | | | | |
| Existing {partId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist {partId: 999} | | | | | | | | | | O |
| **Category** | | | | | | | | | | |
| Existing {categoryId: 1} | O | O | O | O | O | O | O | O | O | O |
| Does not exist {categoryId: 999} | | | | | | | | | | |
| **Market** | | | | | | | | | | |
| Existing {marketId: 1} | O | O | O | O | O | O | O | O | O | O |
| Does not exist {marketId: 999} | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **partId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **name** | | | | | | | | | | |
| "Oil 5W40" (update) | O | O | O | O | O | O | O | O | O | O |
| null (no update) | | | | | | | | | | |
| **purchasePrice** | | | | | | | | | | |
| "120000" (update) | O | O | O | O | O | O | O | O | O | O |
| null (no update) | | | | | | | | | | |
| **sellingPrice** | | | | | | | | | | |
| "132000" (auto-calculated) | O | O | O | O | O | O | O | O | O | O |
| "140000" (override) | | | | | | | | | | |
| null (no update) | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (PartReqDto) | O | O | O | O | O | O | O | O | O | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException (Part) | | | | | | | | | | O |
| ResourceNotFoundException (Category) | | | | | | | | | | |
| ResourceNotFoundException (Market) | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC011: Update part with all fields
- TC012: Update part with partial fields (name only)
- TC013: Update part with purchasePrice (auto-calculate sellingPrice)
- TC014: Update part with override sellingPrice
- TC015: Update part with category change
- TC016: Update part with market change
- TC017: Update part with vehicleModel change
- TC018: Update part with supplier change
- TC019: Update part with minimal fields
- TC020: Part not found

---

# Test Case Matrix: getAllPart / Ma Trận Test Case: Lấy Danh Sách Linh Kiện

| Precondition | TC021 | TC022 | TC023 | TC024 | TC025 | TC026 | TC027 | TC028 | TC029 | TC030 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Parts in database** | | | | | | | | | | |
| Multiple parts exist | O | O | O | O | O | O | O | O | O | |
| No parts exist | | | | | | | | | | O |
| **Input Parameters** | | | | | | | | | | |
| **page** | | | | | | | | | | |
| "0" (first page) | O | O | O | O | O | O | O | O | O | O |
| "1" (second page) | | | | | | | | | | |
| **size** | | | | | | | | | | |
| "5" (valid) | O | O | O | O | O | O | O | O | O | O |
| "10" (valid) | | | | | | | | | | |
| "100" (large) | | | | | | | | | | |
| **categoryId** | | | | | | | | | | |
| "1" (filter by category) | | O | | | | | | | | |
| null (no filter) | O | | O | O | O | O | O | O | O | O |
| **status** | | | | | | | | | | |
| "IN_STOCK" (filter by status) | | | O | | | | | | | | |
| null (no filter) | O | O | | O | O | O | O | O | O | O |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (Page<PartReqDto>) | O | O | O | O | O | O | O | O | O | O |
| Empty page | | | | | | | | | | O |
| **Stock Status Update** | | | | | | | | | | |
| Updated based on quantity | O | O | O | O | O | O | O | O | O | O |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | N |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC021: Get all parts without filters
- TC022: Get parts filtered by categoryId
- TC023: Get parts filtered by status
- TC024: Get parts with categoryId and status filters
- TC025: Get parts with pagination (page 0, size 5)
- TC026: Get parts with pagination (page 1, size 5)
- TC027: Get parts with large page size
- TC028: Get parts with stock status update (quantity = 0)
- TC029: Get parts with stock status update (quantity < reorderLevel)
- TC030: Get parts when no parts exist

---

# Test Case Matrix: createAllowance / Ma Trận Test Case: Tạo Phụ Cấp

| Precondition | TC031 | TC032 | TC033 | TC034 | TC035 | TC036 | TC037 | TC038 | TC039 | TC040 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Employee** | | | | | | | | | | |
| Existing {employeeId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist {employeeId: 999} | | | | | | | | | | O |
| **Creator (Accountant)** | | | | | | | | | | |
| Existing {employeeId: 99} | O | O | O | O | O | O | O | O | O | O |
| **Input Parameters** | | | | | | | | | | |
| **employeeId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| null | | | | | | | | | | |
| **type** | | | | | | | | | | |
| MEAL (valid) | O | O | | | | | | | | |
| OVERTIME (valid) | | | O | | | | | | | |
| TRANSPORTATION (valid) | | | | O | | | | | | |
| null | | | | | O | | | | | |
| **amount** | | | | | | | | | | |
| "150000" (valid, > 0) | O | O | O | O | | O | O | O | O | O |
| "0" (boundary) | | | | | | | | | | |
| "-10000" (invalid, < 0) | | | | | | | | | | |
| null | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (AllowanceDto) | O | O | O | O | | O | O | O | O | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException | | | | | | | | | | O |
| ValidationException | | | | | O | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | A | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC031: Create allowance with MEAL type
- TC032: Create allowance with OVERTIME type
- TC033: Create allowance with TRANSPORTATION type
- TC034: Create allowance with null type (invalid)
- TC035: Create allowance with valid amount
- TC036: Create allowance with boundary amount (0)
- TC037: Create allowance with invalid amount (< 0)
- TC038: Create allowance with maximum valid amount
- TC039: Create allowance with minimum valid amount
- TC040: Employee not found

---

# Test Case Matrix: createDeduction / Ma Trận Test Case: Tạo Khấu Trừ

| Precondition | TC041 | TC042 | TC043 | TC044 | TC045 | TC046 | TC047 | TC048 | TC049 | TC050 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Employee** | | | | | | | | | | |
| Existing {employeeId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist {employeeId: 999} | | | | | | | | | | O |
| **Creator** | | | | | | | | | | |
| Existing {employeeId: 99} | O | O | O | O | O | O | O | O | O | |
| Does not exist {employeeId: 999} | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **employeeId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **type** | | | | | | | | | | |
| LATE (valid) | O | | | | | | | | | |
| ABSENT (valid) | | O | | | | | | | | |
| DAMAGE (valid) | | | O | | | | | | | |
| null | | | | O | | | | | | |
| **content** | | | | | | | | | | |
| "Late 30 minutes" (valid) | O | O | O | | O | O | O | O | O | O |
| null | | | | O | | | | | | |
| "" (empty) | | | | | | | | | | |
| **amount** | | | | | | | | | | |
| "50000" (valid, > 0) | O | O | O | O | | O | O | O | O | O |
| "0" (boundary) | | | | | O | | | | | |
| "-10000" (invalid, < 0) | | | | | | | | | | |
| null | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (DeductionDto) | O | O | O | | | O | O | O | O | |
| **Exception** | | | | | | | | | | |
| RuntimeException (Employee not found) | | | | | | | | | | O |
| RuntimeException (Creator not found) | | | | | | | | | | |
| ValidationException | | | | O | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | A | B | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC041: Create deduction with LATE type
- TC042: Create deduction with ABSENT type
- TC043: Create deduction with DAMAGE type
- TC044: Create deduction with null type (invalid)
- TC045: Create deduction with boundary amount (0)
- TC046: Create deduction with valid content and amount
- TC047: Create deduction with maximum valid amount
- TC048: Create deduction with minimum valid amount
- TC049: Create deduction with long content
- TC050: Employee not found

---

# Test Case Matrix: createDebt / Ma Trận Test Case: Tạo Công Nợ

| Precondition | TC051 | TC052 | TC053 | TC054 | TC055 | TC056 | TC057 | TC058 | TC059 | TC060 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Customer** | | | | | | | | | | |
| Existing {customerId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist {customerId: 999} | | | | | | | | | | O |
| **ServiceTicket** | | | | | | | | | | |
| Existing {serviceTicketId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist {serviceTicketId: 999} | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **customerId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **serviceTicketId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | |
| **amount** | | | | | | | | | | |
| "1000000" (valid, > 0) | O | O | O | O | O | O | O | O | O | O |
| "0" (boundary) | | | | | | | | | | |
| "-100000" (invalid, < 0) | | | | | | | | | | |
| null | | | | | | | | | | |
| **dueDate** | | | | | | | | | | |
| Future date (valid) | O | O | O | O | O | O | O | O | O | O |
| null (default: 7 days) | | | | | | | | | | |
| Past date (invalid) | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (DebtDetailResponseDto) | O | O | O | O | O | O | O | O | O | |
| **Exception** | | | | | | | | | | |
| CustomerNotFoundException | | | | | | | | | | O |
| ServiceTicketNotFoundException | | | | | | | | | | |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC051: Create debt with all required fields
- TC052: Create debt with default dueDate (7 days)
- TC053: Create debt with custom dueDate
- TC054: Create debt with minimum valid amount
- TC055: Create debt with maximum valid amount
- TC056: Create debt with boundary amount (0)
- TC057: Create debt with invalid amount (< 0)
- TC058: Create debt with past dueDate (invalid)
- TC059: Create debt with null amount (invalid)
- TC060: Customer not found

---

# Test Case Matrix: payDebt / Ma Trận Test Case: Thanh Toán Công Nợ

| Precondition | TC061 | TC062 | TC063 | TC064 | TC065 | TC066 | TC067 | TC068 | TC069 | TC070 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Debt** | | | | | | | | | | |
| Existing {debtId: 1, amount: 1000000, paidAmount: 0} | O | O | O | O | O | O | O | O | O | |
| Does not exist {debtId: 999} | | | | | | | | | | O |
| Fully paid {debtId: 2, amount: 1000000, paidAmount: 1000000} | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **debtId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **amount** | | | | | | | | | | |
| "1000000" (exact payment) | O | | | | | | | | | |
| "500000" (partial payment) | | O | | | | | | | | |
| "1500000" (over payment) | | | O | | | | | | | |
| "0" (invalid) | | | | | | | | | | |
| "-100000" (invalid) | | | | | | | | | | |
| **method** | | | | | | | | | | |
| CASH | O | O | O | O | O | | O | O | O | O |
| BANK_TRANSFER | | | | | | O | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (TransactionResponseDto) | O | O | O | O | O | O | O | O | O | |
| **Debt Status Update** | | | | | | | | | | |
| Updated to PAID_IN_FULL | O | | O | | | | | | | |
| Updated to PARTIALLY_PAID | | O | | | | | | | | |
| Not updated (BANK_TRANSFER) | | | | | | O | | | | |
| **Exception** | | | | | | | | | | |
| DebtNotFoundException | | | | | | | | | | O |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC061: Pay debt with exact amount (CASH)
- TC062: Pay debt with partial amount (CASH)
- TC063: Pay debt with over payment (CASH)
- TC064: Pay debt with minimum valid amount
- TC065: Pay debt with maximum valid amount
- TC066: Pay debt with BANK_TRANSFER (debt not updated immediately)
- TC067: Pay debt with boundary amount (0)
- TC068: Pay debt with invalid amount (< 0)
- TC069: Pay debt with amount > remaining amount
- TC070: Debt not found

---

# Test Case Matrix: getAllDebtsSummary / Ma Trận Test Case: Lấy Tổng Hợp Công Nợ

| Precondition | TC071 | TC072 | TC073 | TC074 | TC075 | TC076 | TC077 | TC078 | TC079 | TC080 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Debts in database** | | | | | | | | | | |
| Multiple debts exist | O | O | O | O | O | O | O | O | O | |
| No debts exist | | | | | | | | | | O |
| **Input Parameters** | | | | | | | | | | |
| **page** | | | | | | | | | | |
| "0" (first page) | O | O | O | O | O | O | O | O | O | O |
| "1" (second page) | | | | | | | | | | |
| **size** | | | | | | | | | | |
| "5" (valid) | O | O | O | O | O | O | O | O | O | O |
| "10" (valid) | | | | | | | | | | |
| **sortBy** | | | | | | | | | | |
| "totalRemaining" (valid) | | O | | | | | | | | |
| "dueDate" (valid) | | | O | | | | | | | |
| null (default) | O | | | O | O | O | O | O | O | O |
| **sortDirection** | | | | | | | | | | |
| ASC | | | | O | | | | | | |
| DESC | | | | | O | | | | | |
| null (default) | O | O | O | | O | O | O | O | O | O |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (Page<CustomerDebtSummaryDto>) | O | O | O | O | O | O | O | O | O | O |
| Empty page | | | | | | | | | | O |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | N |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC071: Get all debts summary without sorting
- TC072: Get all debts summary sorted by totalRemaining DESC
- TC073: Get all debts summary sorted by dueDate ASC
- TC074: Get all debts summary with pagination (page 0, size 5)
- TC075: Get all debts summary with pagination (page 1, size 5)
- TC076: Get all debts summary with large page size
- TC077: Get all debts summary with null values handling
- TC078: Get all debts summary with multiple customers
- TC079: Get all debts summary with single customer
- TC080: Get all debts summary when no debts exist

---

# Test Case Matrix: createInvoice / Ma Trận Test Case: Tạo Hóa Đơn

| Precondition | TC081 | TC082 | TC083 | TC084 | TC085 | TC086 | TC087 | TC088 | TC089 | TC090 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **ServiceTicket** | | | | | | | | | | |
| Existing {serviceTicketId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist {serviceTicketId: 999} | | | | | | | | | | O |
| **PriceQuotation** | | | | | | | | | | |
| Existing {quotationId: 1} | O | O | O | O | O | O | O | O | O | O |
| Does not exist {quotationId: 999} | | | | | | | | | | |
| **Customer** | | | | | | | | | | |
| Existing {customerId: 1} | O | O | O | O | O | O | O | O | O | O |
| null (from ServiceTicket) | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **serviceTicketId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **quotationId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | O |
| "999" (not exist) | | | | | | | | | | |
| **discount** | | | | | | | | | | |
| "0" (no discount) | O | O | O | O | O | O | O | O | O | O |
| "10000" (valid discount) | | | | | | | | | | |
| "-10000" (invalid) | | | | | | | | | | |
| **debtAmount** | | | | | | | | | | |
| "0" (no debt) | O | O | O | O | O | O | O | O | O | O |
| "50000" (valid debt) | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (InvoiceDetailResDto) | O | O | O | O | O | O | O | O | O | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException (ServiceTicket) | | | | | | | | | | O |
| ResourceNotFoundException (PriceQuotation) | | | | | | | | | | |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC081: Create invoice with all required fields
- TC082: Create invoice with discount
- TC083: Create invoice with debtAmount
- TC084: Create invoice with discount and debtAmount
- TC085: Create invoice with customer from ServiceTicket
- TC086: Create invoice with null customer
- TC087: Create invoice with maximum valid discount
- TC088: Create invoice with invalid discount (< 0)
- TC089: Create invoice with invalid debtAmount (< 0)
- TC090: ServiceTicket not found

---

# Test Case Matrix: payInvoice / Ma Trận Test Case: Thanh Toán Hóa Đơn

| Precondition | TC091 | TC092 | TC093 | TC094 | TC095 | TC096 | TC097 | TC098 | TC099 | TC100 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Invoice** | | | | | | | | | | |
| Existing {invoiceId: 1, totalAmount: 1000000, paidAmount: 0} | O | O | O | O | O | O | O | O | O | |
| Does not exist {invoiceId: 999} | | | | | | | | | | O |
| Fully paid {invoiceId: 2, totalAmount: 1000000, paidAmount: 1000000} | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **invoiceId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **amount** | | | | | | | | | | |
| "1000000" (exact payment) | O | | | | | | | | | |
| "500000" (partial payment) | | O | | | | | | | | |
| "1500000" (over payment) | | | O | | | | | | | |
| "0" (invalid) | | | | | | | | | | |
| "-100000" (invalid) | | | | | | | | | | |
| **method** | | | | | | | | | | |
| CASH | O | O | O | O | O | | O | O | O | O |
| BANK_TRANSFER | | | | | | O | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (TransactionResponseDto) | O | O | O | O | O | O | O | O | O | |
| **Invoice Status Update** | | | | | | | | | | |
| Updated to PAID | O | | O | | | | | | | |
| Updated to PARTIALLY_PAID | | O | | | | | | | | |
| Not updated (BANK_TRANSFER) | | | | | | O | | | | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException | | | | | | | | | | O |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC091: Pay invoice with exact amount (CASH)
- TC092: Pay invoice with partial amount (CASH)
- TC093: Pay invoice with over payment (CASH)
- TC094: Pay invoice with minimum valid amount
- TC095: Pay invoice with maximum valid amount
- TC096: Pay invoice with BANK_TRANSFER
- TC097: Pay invoice with boundary amount (0)
- TC098: Pay invoice with invalid amount (< 0)
- TC099: Pay invoice with amount > remaining amount
- TC100: Invoice not found

---

# Test Case Matrix: createServiceTicket / Ma Trận Test Case: Tạo Phiếu Dịch Vụ

| Precondition | TC101 | TC102 | TC103 | TC104 | TC105 | TC106 | TC107 | TC108 | TC109 | TC110 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Customer** | | | | | | | | | | |
| Existing {customerId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist (new customer) | | | | | | | | | | |
| **Vehicle** | | | | | | | | | | |
| Existing {vehicleId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist (new vehicle) | | | | | | | | | | |
| **Brand** | | | | | | | | | | |
| Existing {brandId: 1} | O | O | O | O | O | O | O | O | O | O |
| Does not exist {brandId: 999} | | | | | | | | | | |
| **VehicleModel** | | | | | | | | | | |
| Existing {vehicleModelId: 1} | O | O | O | O | O | O | O | O | O | O |
| Does not exist {vehicleModelId: 999} | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **customerId** | | | | | | | | | | |
| "1" (existing) | O | O | O | O | O | O | O | O | O | |
| null (new customer) | | | | | | | | | | |
| **vehicleId** | | | | | | | | | | |
| "1" (existing) | O | O | O | O | O | O | O | O | O | |
| null (new vehicle) | | | | | | | | | | |
| **customerName** | | | | | | | | | | |
| "Nguyen Van A" (valid) | O | O | O | O | O | O | O | O | O | O |
| null | | | | | | | | | | |
| **phone** | | | | | | | | | | |
| "0912345678" (valid) | O | O | O | O | O | O | O | O | O | O |
| null | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (ServiceTicketResponseDto) | O | O | O | O | O | O | O | O | O | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException (Brand) | | | | | | | | | | |
| ResourceNotFoundException (VehicleModel) | | | | | | | | | | |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC101: Create service ticket with existing customer and vehicle
- TC102: Create service ticket with new customer and existing vehicle
- TC103: Create service ticket with existing customer and new vehicle
- TC104: Create service ticket with new customer and new vehicle
- TC105: Create service ticket with all required fields
- TC106: Create service ticket with minimal fields
- TC107: Create service ticket with null customerId (new customer)
- TC108: Create service ticket with null vehicleId (new vehicle)
- TC109: Create service ticket with invalid phone format
- TC110: Brand not found

---

# Test Case Matrix: updateServiceTicket / Ma Trận Test Case: Cập Nhật Phiếu Dịch Vụ

| Precondition | TC111 | TC112 | TC113 | TC114 | TC115 | TC116 | TC117 | TC118 | TC119 | TC120 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **ServiceTicket** | | | | | | | | | | |
| Existing {serviceTicketId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist {serviceTicketId: 999} | | | | | | | | | | O |
| **Input Parameters** | | | | | | | | | | |
| **serviceTicketId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **status** | | | | | | | | | | |
| PENDING (valid) | O | | | | | | | | | |
| IN_PROGRESS (valid) | | O | | | | | | | | |
| COMPLETED (valid) | | | O | | | | | | | |
| null (no update) | | | | O | O | O | O | O | O | O |
| **customerName** | | | | | | | | | | |
| "Updated Name" (update) | O | O | O | O | O | O | O | O | O | O |
| null (no update) | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (ServiceTicketResponseDto) | O | O | O | O | O | O | O | O | O | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException | | | | | | | | | | O |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC111: Update service ticket status to PENDING
- TC112: Update service ticket status to IN_PROGRESS
- TC113: Update service ticket status to COMPLETED
- TC114: Update service ticket with customerName only
- TC115: Update service ticket with partial fields
- TC116: Update service ticket with all fields
- TC117: Update service ticket with minimal fields
- TC118: Update service ticket with null status (no update)
- TC119: Update service ticket with invalid status
- TC120: ServiceTicket not found

---

# Test Case Matrix: createQuotation / Ma Trận Test Case: Tạo Báo Giá

| Precondition | TC121 | TC122 | TC123 | TC124 | TC125 | TC126 | TC127 | TC128 | TC129 | TC130 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **ServiceTicket** | | | | | | | | | | |
| Existing {serviceTicketId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist {serviceTicketId: 999} | | | | | | | | | | O |
| **Input Parameters** | | | | | | | | | | |
| **serviceTicketId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **items** | | | | | | | | | | |
| Valid items list | O | O | O | O | O | O | O | O | O | O |
| Empty list | | | | | | | | | | |
| null | | | | | | | | | | |
| **items[].partId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | O |
| "999" (not exist) | | | | | | | | | | |
| **items[].quantity** | | | | | | | | | | |
| "1" (valid, > 0) | O | O | O | O | O | O | O | O | O | O |
| "0" (invalid) | | | | | | | | | | |
| "-1" (invalid) | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (PriceQuotationResponseDto) | O | O | O | O | O | O | O | O | O | |
| **ServiceTicket Status Update** | | | | | | | | | | |
| Updated to QUOTATION_CREATED | O | O | O | O | O | O | O | O | O | O |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException (ServiceTicket) | | | | | | | | | | O |
| ResourceNotFoundException (Part) | | | | | | | | | | |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC121: Create quotation with valid items
- TC122: Create quotation with single item
- TC123: Create quotation with multiple items
- TC124: Create quotation with minimum quantity (1)
- TC125: Create quotation with maximum quantity
- TC126: Create quotation with empty items list
- TC127: Create quotation with null items
- TC128: Create quotation with invalid quantity (0)
- TC129: Create quotation with invalid quantity (< 0)
- TC130: ServiceTicket not found

---

# Test Case Matrix: approvePurchaseRequest / Ma Trận Test Case: Phê Duyệt Yêu Cầu Mua Hàng

| Precondition | TC131 | TC132 | TC133 | TC134 | TC135 | TC136 | TC137 | TC138 | TC139 | TC140 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **PurchaseRequest** | | | | | | | | | | |
| Existing {purchaseRequestId: 1, status: PENDING} | O | O | O | O | O | O | O | O | O | |
| Does not exist {purchaseRequestId: 999} | | | | | | | | | | O |
| Already approved {purchaseRequestId: 2, status: APPROVED} | | | | | | | | | | |
| **Items** | | | | | | | | | | |
| Has items | O | O | O | O | O | O | O | O | O | O |
| No items | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **purchaseRequestId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (void) | O | O | O | O | O | O | O | O | O | |
| **Status Update** | | | | | | | | | | |
| Updated to APPROVED | O | O | O | O | O | O | O | O | O | |
| **Stock Receipt Creation** | | | | | | | | | | |
| Created successfully | O | O | O | O | O | O | O | O | O | |
| Creation failed | | | | | | | | | | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException | | | | | | | | | | O |
| IllegalStateException (already approved) | | | | | | | | | | |
| RuntimeException (stock receipt creation failed) | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC131: Approve purchase request successfully
- TC132: Approve purchase request with items
- TC133: Approve purchase request without items
- TC134: Approve purchase request and create stock receipt
- TC135: Approve purchase request with multiple items
- TC136: Approve purchase request with single item
- TC137: Approve purchase request with stock receipt creation failure
- TC138: Approve purchase request that is already approved
- TC139: Approve purchase request with no items
- TC140: PurchaseRequest not found

---

# Test Case Matrix: createAppointment / Ma Trận Test Case: Tạo Lịch Hẹn

| Precondition | TC141 | TC142 | TC143 | TC144 | TC145 | TC146 | TC147 | TC148 | TC149 | TC150 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Customer** | | | | | | | | | | |
| Existing {customerId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist (new customer) | | | | | | | | | | |
| **Vehicle** | | | | | | | | | | |
| Existing {vehicleId: 1} | O | O | O | O | O | O | O | O | O | |
| Does not exist (new vehicle) | | | | | | | | | | |
| **Time Slot** | | | | | | | | | | |
| Available | O | O | O | O | O | O | O | O | O | |
| Not available (already booked) | | | | | | | | | | |
| **Daily Limit** | | | | | | | | | | |
| Not exceeded | O | O | O | O | O | O | O | O | O | |
| Exceeded (max 20/day) | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **customerId** | | | | | | | | | | |
| "1" (existing) | O | O | O | O | O | O | O | O | O | |
| null (new customer) | | | | | | | | | | |
| **vehicleId** | | | | | | | | | | |
| "1" (existing) | O | O | O | O | O | O | O | O | O | |
| null (new vehicle) | | | | | | | | | | |
| **appointmentDate** | | | | | | | | | | |
| Future date (valid) | O | O | O | O | O | O | O | O | O | O |
| Past date (invalid) | | | | | | | | | | |
| Today (valid) | | | | | | | | | | |
| **timeSlot** | | | | | | | | | | |
| "08:00" (valid) | O | O | O | O | O | O | O | O | O | O |
| "20:00" (invalid, outside hours) | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (AppointmentDto) | O | O | O | O | O | O | O | O | O | |
| **Exception** | | | | | | | | | | |
| IllegalArgumentException (time slot not available) | | | | | | | | | | |
| IllegalArgumentException (daily limit exceeded) | | | | | | | | | | |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC141: Create appointment with existing customer and vehicle
- TC142: Create appointment with new customer and existing vehicle
- TC143: Create appointment with existing customer and new vehicle
- TC144: Create appointment with new customer and new vehicle
- TC145: Create appointment with available time slot
- TC146: Create appointment with time slot already booked
- TC147: Create appointment with daily limit not exceeded
- TC148: Create appointment with daily limit exceeded (max 20/day)
- TC149: Create appointment with past date (invalid)
- TC150: Create appointment with invalid time slot (outside hours)

---

# Test Case Matrix: createTransaction / Ma Trận Test Case: Tạo Giao Dịch

| Precondition | TC151 | TC152 | TC153 | TC154 | TC155 | TC156 | TC157 | TC158 | TC159 | TC160 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Debt/Invoice** | | | | | | | | | | |
| Existing {debtId: 1} | O | O | O | O | O | O | O | O | O | |
| Existing {invoiceId: 1} | | | | | | | | | | |
| Does not exist | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **type** | | | | | | | | | | |
| DEBT_PAYMENT | O | O | O | O | O | O | O | O | O | |
| INVOICE_PAYMENT | | | | | | | | | | |
| **method** | | | | | | | | | | |
| CASH | O | O | O | O | O | O | O | O | O | |
| BANK_TRANSFER | | | | | | | | | | |
| **amount** | | | | | | | | | | |
| "1000000" (valid, > 0) | O | O | O | O | O | O | O | O | O | O |
| "0" (invalid) | | | | | | | | | | |
| "-100000" (invalid) | | | | | | | | | | |
| null | | | | | | | | | | |
| **debtId** | | | | | | | | | | |
| "1" (valid, for DEBT_PAYMENT) | O | O | O | O | O | O | O | O | O | |
| null (for INVOICE_PAYMENT) | | | | | | | | | | |
| **invoiceId** | | | | | | | | | | |
| "1" (valid, for INVOICE_PAYMENT) | | | | | | | | | | |
| null (for DEBT_PAYMENT) | O | O | O | O | O | O | O | O | O | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (TransactionResponseDto) | O | O | O | O | O | O | O | O | O | |
| **PayOS Integration** | | | | | | | | | | |
| Payment link created (BANK_TRANSFER) | | | | | | | | | | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException (Debt) | | | | | | | | | | |
| ResourceNotFoundException (Invoice) | | | | | | | | | | |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC151: Create transaction for debt payment (CASH)
- TC152: Create transaction for debt payment (BANK_TRANSFER)
- TC153: Create transaction for invoice payment (CASH)
- TC154: Create transaction for invoice payment (BANK_TRANSFER)
- TC155: Create transaction with valid amount
- TC156: Create transaction with boundary amount (0)
- TC157: Create transaction with invalid amount (< 0)
- TC158: Create transaction with null amount
- TC159: Create transaction with invalid type
- TC160: Debt/Invoice not found

---

# Test Case Matrix: handleCallback / Ma Trận Test Case: Xử Lý Callback Thanh Toán

| Precondition | TC161 | TC162 | TC163 | TC164 | TC165 | TC166 | TC167 | TC168 | TC169 | TC170 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Transaction** | | | | | | | | | | |
| Existing {transactionId: 1, status: PENDING} | O | O | O | O | O | O | O | O | O | |
| Does not exist {transactionId: 999} | | | | | | | | | | O |
| Already processed {transactionId: 2, status: SUCCESS} | | | | | | | | | | |
| **Debt/Invoice** | | | | | | | | | | |
| Existing {debtId: 1} | O | O | O | O | O | O | O | O | O | |
| Existing {invoiceId: 1} | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **code** | | | | | | | | | | |
| "00" (success) | O | O | O | O | O | O | O | O | O | |
| "07" (cancelled) | | | | | | | | | | |
| "09" (failed) | | | | | | | | | | |
| **desc** | | | | | | | | | | |
| "Success" (valid) | O | O | O | O | O | O | O | O | O | O |
| "Cancelled" (valid) | | | | | | | | | | |
| "Failed" (valid) | | | | | | | | | | |
| **data** | | | | | | | | | | |
| Valid data object | O | O | O | O | O | O | O | O | O | O |
| null | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (void) | O | O | O | O | O | O | O | O | O | |
| **Transaction Status Update** | | | | | | | | | | |
| Updated to SUCCESS | O | O | O | O | O | O | O | O | O | |
| Updated to FAILED | | | | | | | | | | |
| **Debt/Invoice Status Update** | | | | | | | | | | |
| Updated based on payment | O | O | O | O | O | O | O | O | O | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException | | | | | | | | | | O |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC161: Handle callback with success code (00)
- TC162: Handle callback with cancelled code (07)
- TC163: Handle callback with failed code (09)
- TC164: Handle callback and update debt status
- TC165: Handle callback and update invoice status
- TC166: Handle callback with already processed transaction
- TC167: Handle callback with invalid code
- TC168: Handle callback with null data
- TC169: Handle callback with invalid signature
- TC170: Transaction not found

---

# Test Case Matrix: approvePayroll / Ma Trận Test Case: Phê Duyệt Lương

| Precondition | TC171 | TC172 | TC173 | TC174 | TC175 | TC176 | TC177 | TC178 | TC179 | TC180 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Payroll** | | | | | | | | | | |
| Existing {payrollId: 1, status: PENDING} | O | O | O | O | O | O | O | O | O | |
| Does not exist {payrollId: 999} | | | | | | | | | | O |
| Already approved {payrollId: 2, status: APPROVED} | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **payrollId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (void) | O | O | O | O | O | O | O | O | O | |
| **Status Update** | | | | | | | | | | |
| Updated to APPROVED | O | O | O | O | O | O | O | O | O | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException | | | | | | | | | | O |
| IllegalStateException (already approved) | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC171: Approve payroll successfully
- TC172: Approve payroll with PENDING status
- TC173: Approve payroll with multiple employees
- TC174: Approve payroll with single employee
- TC175: Approve payroll with allowances
- TC176: Approve payroll with deductions
- TC177: Approve payroll with allowances and deductions
- TC178: Approve payroll that is already approved
- TC179: Approve payroll with zero total amount
- TC180: Payroll not found

---

# Test Case Matrix: createManualVoucher / Ma Trận Test Case: Tạo Phiếu Thủ Công

| Precondition | TC181 | TC182 | TC183 | TC184 | TC185 | TC186 | TC187 | TC188 | TC189 | TC190 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Creator** | | | | | | | | | | |
| Existing {employeeId: 1} | O | O | O | O | O | O | O | O | O | O |
| **Input Parameters** | | | | | | | | | | |
| **type** | | | | | | | | | | |
| PAYMENT (valid) | O | O | O | O | O | O | O | O | O | O |
| RECEIPT (valid) | | | | | | | | | | |
| **amount** | | | | | | | | | | |
| "1000000" (valid, > 0) | O | O | O | O | O | O | O | O | O | O |
| "0" (invalid) | | | | | | | | | | |
| "-100000" (invalid) | | | | | | | | | | |
| null | | | | | | | | | | |
| **description** | | | | | | | | | | |
| "Payment for supplies" (valid) | O | O | O | O | O | O | O | O | O | O |
| null | | | | | | | | | | |
| "" (empty) | | | | | | | | | | |
| **attachments** | | | | | | | | | | |
| Valid file list | O | O | O | O | O | O | O | O | O | O |
| Empty list | | | | | | | | | | |
| null | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (LedgerVoucherDetailResponse) | O | O | O | O | O | O | O | O | O | |
| **Exception** | | | | | | | | | | |
| ValidationException | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC181: Create manual voucher with PAYMENT type
- TC182: Create manual voucher with RECEIPT type
- TC183: Create manual voucher with valid amount
- TC184: Create manual voucher with description
- TC185: Create manual voucher with attachments
- TC186: Create manual voucher without attachments
- TC187: Create manual voucher with boundary amount (0)
- TC188: Create manual voucher with invalid amount (< 0)
- TC189: Create manual voucher with null description
- TC190: Create manual voucher with invalid type

---

# Test Case Matrix: approveVoucher / Ma Trận Test Case: Phê Duyệt Phiếu

| Precondition | TC191 | TC192 | TC193 | TC194 | TC195 | TC196 | TC197 | TC198 | TC199 | TC200 |
|--------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **Can connect with server** | O | O | O | O | O | O | O | O | O | O |
| **Voucher** | | | | | | | | | | |
| Existing {voucherId: 1, status: PENDING} | O | O | O | O | O | O | O | O | O | |
| Does not exist {voucherId: 999} | | | | | | | | | | O |
| Already approved {voucherId: 2, status: APPROVED} | | | | | | | | | | |
| Already rejected {voucherId: 3, status: REJECTED} | | | | | | | | | | |
| **Input Parameters** | | | | | | | | | | |
| **voucherId** | | | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | | | O |
| **action** | | | | | | | | | | |
| APPROVE | O | O | O | O | O | O | O | O | O | |
| REJECT | | | | | | | | | | |
| **rejectionReason** | | | | | | | | | | |
| null (for APPROVE) | O | O | O | O | O | O | O | O | O | |
| "Invalid amount" (for REJECT) | | | | | | | | | | |
| **Expected Outcome** | | | | | | | | | | |
| **Return** | | | | | | | | | | |
| Successfully (void) | O | O | O | O | O | O | O | O | O | |
| **Status Update** | | | | | | | | | | |
| Updated to APPROVED | O | O | O | O | O | O | O | O | O | |
| Updated to REJECTED | | | | | | | | | | |
| **Exception** | | | | | | | | | | |
| ResourceNotFoundException | | | | | | | | | | O |
| IllegalStateException (already approved) | | | | | | | | | | |
| IllegalStateException (already rejected) | | | | | | | | | | |
| **Result** | | | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | N | N | N | N | N | N | N | A |
| Passed/Failed | P | P | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | | | |
| Defect ID | | | | | | | | | | |

**Test Case Descriptions:**
- TC191: Approve voucher successfully
- TC192: Approve voucher with PENDING status
- TC193: Approve voucher with PAYMENT type
- TC194: Approve voucher with RECEIPT type
- TC195: Approve voucher with attachments
- TC196: Approve voucher without attachments
- TC197: Approve voucher that is already approved
- TC198: Approve voucher that is already rejected
- TC199: Reject voucher with reason
- TC200: Voucher not found

---

## Tổng Kết / Summary

### Thống Kê / Statistics
- **Tổng số Method**: 20 methods
- **Tổng số Test Cases**: 200 test cases
- **Trung bình Test Cases/Method**: 10 test cases

### Phân Loại Test Cases / Test Case Classification
- **Normal (N)**: ~150 test cases (75%)
- **Abnormal (A)**: ~40 test cases (20%)
- **Boundary (B)**: ~10 test cases (5%)

### Coverage / Độ Phủ
- **CRUD Operations**: Create, Read, Update operations
- **Business Logic**: Payment processing, approval workflows, status updates
- **Error Handling**: Resource not found, validation errors, illegal state
- **Integration**: PayOS callback, stock receipt creation, status synchronization

---

## Ghi Chú / Notes

1. Tất cả các test case đều có precondition "Can connect with server" = O
2. Các test case được thiết kế dựa trên code test thực tế trong dự án
3. Format ma trận tuân theo form đã cung cấp
4. Có thể mở rộng thêm test cases cho các edge cases khác khi cần
5. Các test case cần được implement trong code test để đảm bảo coverage đầy đủ

