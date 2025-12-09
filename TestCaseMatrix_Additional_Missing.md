# Test Case Matrix: Additional Missing Test Cases / Ma Trận Test Case: Các Test Cases Bổ Sung Còn Thiếu

## Tổng Quan / Overview

Tài liệu này chứa test case matrix cho các test cases bổ sung được đề xuất để cover các tình huống có thể xảy ra mà code chưa xử lý đầy đủ.

This document contains test case matrices for additional test cases proposed to cover scenarios that may occur but are not fully handled in the code.

---

# Test Case Matrix: payDebt - Concurrency & Race Conditions / Ma Trận Test Case: Thanh Toán Công Nợ - Đồng Thời & Điều Kiện Đua

| Precondition | TC201 | TC202 | TC203 |
|--------------|-------|-------|-------|
| **Can connect with server** | O | O | O |
| **Debt** | | | |
| Existing {debtId: 1, amount: 1000000, paidAmount: 0, version: 1} | O | O | O |
| **Concurrent Requests** | | | |
| Multiple requests same debt | O | O | O |
| **Input Parameters** | | | |
| **debtId** | | | |
| "1" (same debt) | O | O | O |
| **amount** | | | |
| "600000" (request 1) | O | O | |
| "600000" (request 2, concurrent) | O | | |
| "500000" (request 2, exceeds remaining) | | | O |
| **method** | | | |
| CASH | O | O | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (TransactionResponseDto) - Request 1 | O | O | |
| **Exception** | | | |
| OptimisticLockingException (Request 2) | O | O | |
| IllegalStateException (Amount exceeds remaining) | | | O |
| **Data Consistency** | | | |
| Only one payment succeeds | O | O | O |
| Debt paidAmount updated correctly | O | O | |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A | A |
| Passed/Failed | TBD | TBD | TBD |
| Executed Date | TBD | TBD | TBD |
| Defect ID | TBD | TBD | TBD |

**Test Case Descriptions:**
- TC201: payDebt_ShouldHandleConcurrentPayments_WhenMultipleRequests - Khi 2 requests cùng thanh toán debt cùng lúc, chỉ 1 request thành công
- TC202: payDebt_ShouldUseOptimisticLocking_WhenDebtUpdated - Sử dụng version field để detect concurrent updates
- TC203: payDebt_ShouldRollback_WhenSecondPaymentExceedsRemaining - Nếu payment thứ 2 vượt quá remaining amount, phải rollback

---

# Test Case Matrix: payDebt - Transaction Rollback / Ma Trận Test Case: Thanh Toán Công Nợ - Rollback Giao Dịch

| Precondition | TC208 | TC209 | TC210 |
|--------------|-------|-------|-------|
| **Can connect with server** | O | O | |
| **Database Connection** | | | |
| Connected | O | O | |
| Lost during transaction | | | O |
| **Debt** | | | |
| Existing {debtId: 1, amount: 1000000, paidAmount: 0} | O | O | O |
| **Transaction Service** | | | |
| Creates transaction successfully | O | O | O |
| **Customer Service** | | | |
| updateTotalSpending fails | O | | |
| **Debt Repository** | | | |
| save fails | | O | |
| **Input Parameters** | | | |
| **debtId** | | | |
| "1" (valid) | O | O | O |
| **amount** | | | |
| "500000" (valid) | O | O | O |
| **method** | | | |
| CASH | O | O | O |
| **Expected Outcome** | | | |
| **Exception** | | | |
| RuntimeException (Customer service fails) | O | | |
| DataAccessException (Debt save fails) | | O | |
| DataAccessException (Connection lost) | | | O |
| **Transaction Rollback** | | | |
| Debt not updated | O | O | O |
| Transaction not created | O | O | O |
| Customer spending not updated | O | O | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A | A |
| Passed/Failed | TBD | TBD | TBD |
| Executed Date | TBD | TBD | TBD |
| Defect ID | TBD | TBD | TBD |

**Test Case Descriptions:**
- TC208: payDebt_ShouldRollback_WhenCustomerServiceFails - Nếu customerService.updateTotalSpending fail, phải rollback toàn bộ transaction
- TC209: payDebt_ShouldRollback_WhenDebtSaveFails - Nếu debtRepository.save fail sau khi transaction created, phải rollback
- TC210: payDebt_ShouldHandleDatabaseConnectionLoss - Khi database connection bị mất giữa transaction, phải rollback

---

# Test Case Matrix: approvePayroll - Duplicate Prevention / Ma Trận Test Case: Phê Duyệt Lương - Ngăn Chặn Trùng Lặp

| Precondition | TC204 | TC205 |
|--------------|-------|-------|
| **Can connect with server** | O | O |
| **Payroll** | | | |
| Existing {payrollId: 1, status: APPROVED} | O | |
| Existing {payrollId: 1, status: PENDING, version: 1} | | O |
| **Concurrent Requests** | | | |
| Multiple approval requests | | O |
| **Input Parameters** | | | |
| **payrollId** | | | |
| "1" (valid) | O | O |
| **managerId** | | | |
| "99" (valid) | O | O |
| **Expected Outcome** | | | |
| **Exception** | | | |
| IllegalStateException (Already approved) | O | |
| OptimisticLockingException (Concurrent update) | | O |
| **Status Check** | | | |
| Prevents duplicate approval | O | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A |
| Passed/Failed | TBD | TBD |
| Executed Date | TBD | TBD |
| Defect ID | TBD | TBD |

**Test Case Descriptions:**
- TC204: approvePayroll_ShouldPreventDuplicateApproval_WhenAlreadyApproved - Nếu payroll đã được approve, không cho phép approve lại
- TC205: approvePayroll_ShouldUseOptimisticLocking_WhenConcurrentApproval - Sử dụng version để prevent concurrent approval

---

# Test Case Matrix: createTransaction - Order Code Uniqueness / Ma Trận Test Case: Tạo Giao Dịch - Tính Duy Nhất Order Code

| Precondition | TC206 | TC207 |
|--------------|-------|-------|
| **Can connect with server** | O | O |
| **Concurrent Requests** | | | |
| Multiple requests same time | O | O |
| **Order Code Generation** | | | |
| System.currentTimeMillis() / 1000 | O | O |
| **Input Parameters** | | | |
| **method** | | | |
| BANK_TRANSFER | O | O |
| **amount** | | | |
| "1000000" (valid) | O | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (TransactionResponseDto) | O | O |
| **Order Code** | | | |
| Unique for each transaction | O | O |
| Retry with new code if conflict | | O |
| **Exception** | | | |
| None (handled internally) | O | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A |
| Passed/Failed | TBD | TBD |
| Executed Date | TBD | TBD |
| Defect ID | TBD | TBD |

**Test Case Descriptions:**
- TC206: createTransaction_ShouldGenerateUniqueOrderCode_WhenConcurrentRequests - Order code phải unique, không được duplicate
- TC207: createTransaction_ShouldRetry_WhenOrderCodeConflict - Nếu order code conflict, phải retry với code mới

---

# Test Case Matrix: getDebtsByCustomer - Null Safety / Ma Trận Test Case: Lấy Công Nợ Theo Khách Hàng - An Toàn Null

| Precondition | TC213 | TC214 |
|--------------|-------|-------|
| **Can connect with server** | O | O |
| **Customer** | | | |
| Existing {customerId: 1, vehicles: null} | O | |
| Existing {customerId: 1, address: null} | | O |
| **Debts** | | | |
| Has debts | O | O |
| **Input Parameters** | | | |
| **customerId** | | | |
| "1" (valid) | O | O |
| **status** | | | |
| null (no filter) | O | O |
| **keyword** | | | |
| null (no filter) | O | O |
| **page** | | | |
| "0" (first page) | O | O |
| **size** | | | |
| "5" (valid) | O | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (DebtDetailResponseDto) | O | O |
| **Null Handling** | | | |
| licensePlate = null (when vehicles is null) | O | |
| address = null (when address is null) | | O |
| **Exception** | | | |
| None (handled gracefully) | O | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A |
| Passed/Failed | TBD | TBD |
| Executed Date | TBD | TBD |
| Defect ID | TBD | TBD |

**Test Case Descriptions:**
- TC213: getDebtsByCustomer_ShouldHandleNullVehicles_WhenCustomerHasNoVehicles - Nếu customer.getVehicles() là null, phải return null cho licensePlate
- TC214: getDebtsByCustomer_ShouldHandleNullAddress_WhenCustomerAddressIsNull - Nếu customer.getAddress() là null, phải handle gracefully

---

# Test Case Matrix: processPaymentByPaymentLinkId - Null Safety / Ma Trận Test Case: Xử Lý Thanh Toán - An Toàn Null

| Precondition | TC215 | TC216 |
|--------------|-------|-------|
| **Can connect with server** | O | O |
| **Transaction** | | | |
| Existing {transactionId: 1, invoice: {serviceTicket: null}} | O | |
| Existing {transactionId: 1, invoice: {serviceTicket: {customer: null}}} | | O |
| **PayOS Status** | | | |
| PAID | O | O |
| **Input Parameters** | | | |
| **paymentLinkId** | | | |
| "link123" (valid) | O | O |
| **Expected Outcome** | | | |
| **Exception** | | | |
| NullPointerException (if not handled) | | |
| None (if handled gracefully) | O | O |
| **Null Handling** | | | |
| Handles null serviceTicket | O | |
| Handles null customer | | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A |
| Passed/Failed | TBD | TBD |
| Executed Date | TBD | TBD |
| Defect ID | TBD | TBD |

**Test Case Descriptions:**
- TC215: processPaymentByPaymentLinkId_ShouldHandleNullServiceTicket_WhenInvoiceHasNoServiceTicket - Nếu invoice không có serviceTicket, phải handle gracefully
- TC216: processPaymentByPaymentLinkId_ShouldHandleNullCustomer_WhenServiceTicketHasNoCustomer - Nếu serviceTicket không có customer, phải handle gracefully

---

# Test Case Matrix: approvePurchaseRequest - Stock Receipt Failure / Ma Trận Test Case: Phê Duyệt Yêu Cầu Mua Hàng - Lỗi Tạo Phiếu Nhập Kho

| Precondition | TC211 | TC212 |
|--------------|-------|-------|
| **Can connect with server** | O | O |
| **PurchaseRequest** | | | |
| Existing {purchaseRequestId: 1, status: PENDING} | O | O |
| **Items** | | | |
| Has items | O | O |
| **Stock Receipt Service** | | | |
| createReceiptFromPurchaseRequest fails | O | |
| Partial failure (some items fail) | | O |
| **Input Parameters** | | | |
| **purchaseRequestId** | | | |
| "1" (valid) | O | O |
| **Expected Outcome** | | | |
| **Exception** | | | |
| RuntimeException (Stock receipt creation failed) | O | |
| RuntimeException (Partial failure) | | O |
| **Transaction Rollback** | | | |
| PurchaseRequest status not updated | O | O |
| Stock receipt not created | O | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A |
| Passed/Failed | TBD | TBD |
| Executed Date | TBD | TBD |
| Defect ID | TBD | TBD |

**Test Case Descriptions:**
- TC211: approvePurchaseRequest_ShouldRollback_WhenStockReceiptCreationFails - Nếu stock receipt creation fail, phải rollback approval
- TC212: approvePurchaseRequest_ShouldHandlePartialFailure - Nếu một số items fail khi tạo stock receipt, phải rollback toàn bộ

---

# Test Case Matrix: createTransaction - PayOS API Failures / Ma Trận Test Case: Tạo Giao Dịch - Lỗi PayOS API

| Precondition | TC236 | TC237 | TC238 |
|--------------|-------|-------|-------|
| **Can connect with server** | O | O | O |
| **PayOS API** | | | |
| API is down | O | | |
| API timeout | | O | |
| Temporary error (5xx) | | | O |
| **Input Parameters** | | | |
| **method** | | | |
| BANK_TRANSFER | O | O | O |
| **amount** | | | |
| "1000000" (valid) | O | O | O |
| **Expected Outcome** | | | |
| **Exception** | | | |
| RuntimeException (PayOS API down) | O | |
| TimeoutException (API timeout) | | O |
| Retry with new request | | | O |
| **Transaction** | | | |
| Not created | O | O | |
| Created after retry | | | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A | A |
| Passed/Failed | TBD | TBD | TBD |
| Executed Date | TBD | TBD | TBD |
| Defect ID | TBD | TBD | TBD |

**Test Case Descriptions:**
- TC236: createTransaction_ShouldHandlePayOSApiFailure_WhenApiIsDown - Xử lý khi PayOS API down
- TC237: createTransaction_ShouldHandlePayOSTimeout_WhenResponseDelayed - Xử lý khi PayOS API timeout
- TC238: createTransaction_ShouldRetry_WhenPayOSReturnsTemporaryError - Retry khi PayOS trả về temporary error

---

# Test Case Matrix: handleCallback - Invalid Signature & Duplicate / Ma Trận Test Case: Xử Lý Callback - Chữ Ký Không Hợp Lệ & Trùng Lặp

| Precondition | TC239 | TC240 |
|--------------|-------|-------|
| **Can connect with server** | O | O |
| **Transaction** | | | |
| Existing {transactionId: 1, status: PENDING} | O | O |
| Already processed {transactionId: 1, status: SUCCESS} | | O |
| **PayOS Callback** | | | |
| Invalid signature | O | |
| Duplicate callback | | O |
| **Input Parameters** | | | |
| **code** | | | |
| "00" (success) | O | O |
| **desc** | | | |
| "Success" | O | O |
| **signature** | | | |
| Invalid signature | O | |
| Valid signature | | O |
| **Expected Outcome** | | | |
| **Exception** | | | |
| SecurityException (Invalid signature) | O | |
| IllegalStateException (Already processed) | | O |
| **Transaction Status** | | | |
| Not updated (invalid signature) | O | |
| Not updated (already processed) | | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A |
| Passed/Failed | TBD | TBD |
| Executed Date | TBD | TBD |
| Defect ID | TBD | TBD |

**Test Case Descriptions:**
- TC239: handleCallback_ShouldHandleInvalidSignature_WhenSignatureMismatch - Xử lý khi PayOS callback signature không hợp lệ
- TC240: handleCallback_ShouldHandleDuplicateCallback_WhenCallbackReceivedTwice - Xử lý khi nhận duplicate callback

---

# Test Case Matrix: getAllDebtsSummary - SQL Injection Prevention / Ma Trận Test Case: Lấy Tổng Hợp Công Nợ - Ngăn Chặn SQL Injection

| Precondition | TC243 | TC244 |
|--------------|-------|-------|
| **Can connect with server** | O | O |
| **Debts in database** | | | |
| Multiple debts exist | O | O |
| **Input Parameters** | | | |
| **page** | | | |
| "0" (first page) | O | O |
| **size** | | | |
| "5" (valid) | O | O |
| **sortBy** | | | |
| "totalRemaining; DROP TABLE debts;--" (malicious) | O | |
| "totalRemaining'; DELETE FROM debts;--" (malicious) | | O |
| **sortDirection** | | | |
| "DESC" | O | O |
| **Expected Outcome** | | | |
| **Exception** | | | |
| IllegalArgumentException (Invalid sort parameter) | O | O |
| **SQL Injection** | | | |
| Prevented | O | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A |
| Passed/Failed | TBD | TBD |
| Executed Date | TBD | TBD |
| Defect ID | TBD | TBD |

**Test Case Descriptions:**
- TC243: getAllDebtsSummary_ShouldPreventSQLInjection_WhenSortContainsSQL - Prevent SQL injection trong sort parameter
- TC244: getAllDebtsSummary_ShouldSanitizeSortParameter_WhenInvalidSortProvided - Sanitize sort parameter để prevent injection

---

# Test Case Matrix: payDebt - Amount Consistency / Ma Trận Test Case: Thanh Toán Công Nợ - Tính Nhất Quán Số Tiền

| Precondition | TC219 | TC220 | TC221 |
|--------------|-------|-------|-------|
| **Can connect with server** | O | O | O |
| **Debt** | | | |
| Existing {debtId: 1, amount: 1000000, paidAmount: 0} | O | O | O |
| Has existing transactions | | O | |
| **Input Parameters** | | | |
| **debtId** | | | |
| "1" (valid) | O | O | O |
| **amount** | | | |
| "500000" (valid) | O | O | |
| "600000" (valid, with existing 400000) | | O | |
| "1500000" (exceeds debt amount) | | | O |
| **method** | | | |
| CASH | O | O | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (TransactionResponseDto) | O | O | |
| **Exception** | | | |
| IllegalArgumentException (Amount exceeds debt) | | | O |
| **Data Consistency** | | | |
| paidAmount = sum of transactions | O | O | |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A | A |
| Passed/Failed | TBD | TBD | TBD |
| Executed Date | TBD | TBD | TBD |
| Defect ID | TBD | TBD | TBD |

**Test Case Descriptions:**
- TC219: payDebt_ShouldMaintainConsistency_WhenMultiplePayments - Tổng các transactions phải bằng debt.paidAmount
- TC220: payDebt_ShouldRecalculatePaidAmount_WhenTransactionsExist - Nếu có transactions, phải recalculate paidAmount từ transactions
- TC221: payDebt_ShouldPreventOverPayment_WhenAmountExceedsDebt - Không cho phép payment vượt quá debt amount

---

# Test Case Matrix: createInvoice - Amount Calculation Consistency / Ma Trận Test Case: Tạo Hóa Đơn - Tính Nhất Quán Tính Toán Số Tiền

| Precondition | TC224 | TC225 |
|--------------|-------|-------|
| **Can connect with server** | O | O |
| **ServiceTicket** | | | |
| Existing {serviceTicketId: 1} | O | O |
| **PriceQuotation** | | | |
| Existing {quotationId: 1, totalAmount: 1000000} | O | O |
| **Input Parameters** | | | |
| **serviceTicketId** | | | |
| "1" (valid) | O | O |
| **quotationId** | | | |
| "1" (valid) | O | O |
| **discount** | | | |
| "10000" (valid) | O | |
| "1100000" (exceeds totalAmount) | | O |
| **debtAmount** | | | |
| "0" (no debt) | O | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (InvoiceDetailResDto) | O | |
| **Exception** | | | |
| IllegalArgumentException (Negative finalAmount) | | O |
| **Amount Calculation** | | | |
| finalAmount = totalAmount - discount - debtAmount | O | |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | A | A |
| Passed/Failed | TBD | TBD |
| Executed Date | TBD | TBD |
| Defect ID | TBD | TBD |

**Test Case Descriptions:**
- TC224: createInvoice_ShouldMaintainAmountConsistency_WhenDiscountApplied - finalAmount = totalAmount - discount - debtAmount phải đúng
- TC225: createInvoice_ShouldPreventNegativeFinalAmount_WhenDiscountTooLarge - Không cho phép discount lớn hơn totalAmount

---

# Test Case Matrix: createPart - Very Large Purchase Price / Ma Trận Test Case: Tạo Linh Kiện - Giá Nhập Rất Lớn

| Precondition | TC226 |
|--------------|-------|
| **Can connect with server** | O |
| **Dependencies** | | |
| All dependencies exist | O |
| **Input Parameters** | | |
| **name** | | |
| "Test Part" (valid) | O |
| **purchasePrice** | | |
| "999999999999999999" (very large, near MAX_VALUE) | O |
| **marketId** | | |
| "1" (valid) | O |
| **unitId** | | |
| "1" (valid) | O |
| **supplierId** | | |
| "1" (valid) | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (PartReqDto) | O |
| **Calculation** | | |
| sellingPrice calculated correctly | O |
| No overflow | O |
| **Exception** | | |
| None | O |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | B |
| Passed/Failed | TBD |
| Executed Date | TBD |
| Defect ID | TBD |

**Test Case Descriptions:**
- TC226: createPart_ShouldHandleVeryLargePurchasePrice_WhenPriceExceedsMax - Xử lý purchasePrice rất lớn (gần MAX_VALUE)

---

# Test Case Matrix: createAppointment - Date Edge Cases / Ma Trận Test Case: Tạo Lịch Hẹn - Trường Hợp Đặc Biệt Ngày Tháng

| Precondition | TC229 | TC230 | TC232 |
|--------------|-------|-------|-------|
| **Can connect with server** | O | O | O |
| **Customer** | | | |
| Existing {customerId: 1} | O | O | O |
| **Vehicle** | | | |
| Existing {vehicleId: 1} | O | O | O |
| **Time Slot** | | | |
| Available | O | O | O |
| **Input Parameters** | | | |
| **customerId** | | | |
| "1" (valid) | O | O | O |
| **vehicleId** | | | |
| "1" (valid) | O | O | O |
| **appointmentDate** | | | |
| "2024-02-29" (leap year) | O | | |
| "2024-01-31" (last day of month) | | O | |
| "2024-12-31 23:59" (crosses midnight) | | | O |
| **timeSlot** | | | |
| "08:00" (valid) | O | O | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (AppointmentDto) | O | O | O |
| **Date Handling** | | | |
| Handles leap year correctly | O | | |
| Handles month end correctly | | O | |
| Handles timezone correctly | | | O |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | B | B | B |
| Passed/Failed | TBD | TBD | TBD |
| Executed Date | TBD | TBD | TBD |
| Defect ID | TBD | TBD | TBD |

**Test Case Descriptions:**
- TC229: createAppointment_ShouldHandleLeapYear_WhenDueDateIsFeb29 - Xử lý dueDate vào ngày 29/2 trong năm nhuận
- TC230: createAppointment_ShouldHandleMonthEnd_WhenDateIsLastDayOfMonth - Xử lý appointment vào ngày cuối tháng
- TC232: createAppointment_ShouldHandleTimezone_WhenDateCrossesMidnight - Xử lý timezone khi date crosses midnight

---

## Tổng Kết / Summary

### Thống Kê Test Cases / Test Cases Statistics

| Category | Số Lượng | Priority |
|----------|----------|----------|
| Concurrency & Race Conditions | 5 | HIGH |
| Transaction Rollback & Error Recovery | 5 | HIGH |
| Null Safety & NPE Prevention | 4 | HIGH |
| External Service Failures | 5 | HIGH |
| Data Consistency & Integrity | 3 | HIGH |
| Security & Authorization | 2 | HIGH |
| Boundary Conditions & Edge Cases | 4 | MEDIUM |
| **TỔNG CỘNG** | **28** | |

### Phân Loại / Classification

- **HIGH Priority**: 24 test cases (86%)
- **MEDIUM Priority**: 4 test cases (14%)

---

## Ghi Chú / Notes

1. Tất cả các test cases này đều là **bổ sung** cho 200 test cases đã thiết kế trước đó
2. Các test cases này tập trung vào các **edge cases** và **error scenarios** mà code có thể chưa xử lý đầy đủ
3. Ưu tiên implement các test cases **HIGH priority** trước
4. Một số test cases có thể cần **code improvements** trước khi có thể test (ví dụ: optimistic locking, retry logic)

---

## Tài Liệu Tham Khảo / References

- `Additional_Test_Cases_Missing.md` - Tài liệu phân tích chi tiết các test cases thiếu
- `TestCaseMatrix_Comprehensive_Design.md` - Thiết kế ma trận test case tổng hợp (200 test cases)
- Spring Transaction Management Documentation
- JUnit 5 Concurrency Testing Guide

