# Additional Test Cases - Missing Scenarios / Test Cases Bổ Sung - Các Tình Huống Còn Thiếu

## Tổng Quan / Overview

Tài liệu này phân tích 200 test cases đã thiết kế và đề xuất thêm các test cases cho các tình huống có thể xảy ra mà code triển khai chưa xử lý hoặc chưa được test đầy đủ.

This document analyzes the 200 designed test cases and proposes additional test cases for scenarios that may occur but are not handled or fully tested in the implementation.

---

## Phân Loại Test Cases Thiếu / Missing Test Cases Categories

### 1. Concurrency & Race Conditions / Đồng Thời & Điều Kiện Đua

#### 1.1 payDebt - Race Condition
**Vấn đề / Issue**: Khi nhiều request thanh toán cùng một debt cùng lúc, có thể dẫn đến:
- Tính toán sai `paidAmount`
- Cập nhật sai `status`
- Tính toán sai `totalSpending` của customer

**Test Cases Cần Bổ Sung / Additional Test Cases Needed**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC201 | **payDebt_ShouldHandleConcurrentPayments_WhenMultipleRequests** | Khi 2 requests cùng thanh toán debt cùng lúc, chỉ 1 request thành công, request còn lại phải detect conflict |
| TC202 | **payDebt_ShouldUseOptimisticLocking_WhenDebtUpdated** | Sử dụng version field để detect concurrent updates |
| TC203 | **payDebt_ShouldRollback_WhenSecondPaymentExceedsRemaining** | Nếu payment thứ 2 vượt quá remaining amount, phải rollback |

**Code Example**:
```java
@Test
void payDebt_ShouldHandleConcurrentPayments_WhenMultipleRequests() throws Exception {
    Debt debt = Debt.builder()
            .debtId(1L)
            .amount(new BigDecimal("1000000"))
            .paidAmount(new BigDecimal("0"))
            .version(1L) // Optimistic locking
            .build();
    
    PayDebtRequestDto dto1 = PayDebtRequestDto.builder()
            .debtId(1L)
            .amount(new BigDecimal("600000"))
            .method("CASH")
            .build();
    
    PayDebtRequestDto dto2 = PayDebtRequestDto.builder()
            .debtId(1L)
            .amount(new BigDecimal("600000"))
            .method("CASH")
            .build();
    
    // Simulate concurrent requests
    when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
    
    // First request succeeds
    service.payDebt(1L, dto1);
    
    // Second request should detect conflict
    assertThrows(OptimisticLockingException.class, 
            () -> service.payDebt(1L, dto2));
}
```

---

#### 1.2 approvePayroll - Duplicate Approval
**Vấn đề / Issue**: Không có check để prevent duplicate approval

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC204 | **approvePayroll_ShouldPreventDuplicateApproval_WhenAlreadyApproved** | Nếu payroll đã được approve, không cho phép approve lại |
| TC205 | **approvePayroll_ShouldUseOptimisticLocking_WhenConcurrentApproval** | Sử dụng version để prevent concurrent approval |

---

#### 1.3 createTransaction - Duplicate Order Code
**Vấn đề / Issue**: `orderCode = System.currentTimeMillis() / 1000` có thể duplicate nếu 2 requests cùng lúc

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC206 | **createTransaction_ShouldGenerateUniqueOrderCode_WhenConcurrentRequests** | Order code phải unique, không được duplicate |
| TC207 | **createTransaction_ShouldRetry_WhenOrderCodeConflict** | Nếu order code conflict, phải retry với code mới |

---

### 2. Transaction Rollback & Error Recovery / Rollback Giao Dịch & Khôi Phục Lỗi

#### 2.1 payDebt - Partial Failure
**Vấn đề / Issue**: Nếu `updateTotalSpending` fail, debt đã được update nhưng customer spending không được update

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC208 | **payDebt_ShouldRollback_WhenCustomerServiceFails** | Nếu `customerService.updateTotalSpending` fail, phải rollback toàn bộ transaction |
| TC209 | **payDebt_ShouldRollback_WhenDebtSaveFails** | Nếu `debtRepository.save` fail sau khi transaction created, phải rollback |
| TC210 | **payDebt_ShouldHandleDatabaseConnectionLoss** | Khi database connection bị mất giữa transaction, phải rollback |

**Code Example**:
```java
@Test
void payDebt_ShouldRollback_WhenCustomerServiceFails() throws Exception {
    Debt debt = Debt.builder()
            .debtId(1L)
            .amount(new BigDecimal("1000000"))
            .paidAmount(new BigDecimal("0"))
            .build();
    
    when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
    when(transactionService.createTransaction(any())).thenReturn(new TransactionResponseDto());
    doThrow(new RuntimeException("Database error")).when(customerService).updateTotalSpending(anyLong(), any());
    
    // Should rollback entire transaction
    assertThrows(RuntimeException.class, () -> service.payDebt(1L, requestDto));
    
    // Verify debt was not updated
    verify(debtRepository, never()).save(any());
}
```

---

#### 2.2 approvePurchaseRequest - Stock Receipt Creation Failure
**Vấn đề / Issue**: Nếu `stockReceiptService.createReceiptFromPurchaseRequest` fail, purchase request đã được approve nhưng stock receipt không được tạo

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC211 | **approvePurchaseRequest_ShouldRollback_WhenStockReceiptCreationFails** | Nếu stock receipt creation fail, phải rollback approval |
| TC212 | **approvePurchaseRequest_ShouldHandlePartialFailure** | Nếu một số items fail khi tạo stock receipt, phải rollback toàn bộ |

---

### 3. Null Safety & NPE Prevention / An Toàn Null & Ngăn Chặn NPE

#### 3.1 getDebtsByCustomer - Null Collections
**Vấn đề / Issue**: `customer.getVehicles().isEmpty()` có thể throw NPE nếu `getVehicles()` return null

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC213 | **getDebtsByCustomer_ShouldHandleNullVehicles_WhenCustomerHasNoVehicles** | Nếu `customer.getVehicles()` là null, phải return null cho licensePlate |
| TC214 | **getDebtsByCustomer_ShouldHandleNullAddress_WhenCustomerAddressIsNull** | Nếu `customer.getAddress()` là null, phải handle gracefully |

**Code Example**:
```java
@Test
void getDebtsByCustomer_ShouldHandleNullVehicles_WhenCustomerHasNoVehicles() {
    Customer customer = Customer.builder()
            .customerId(1L)
            .fullName("Test Customer")
            .vehicles(null) // Null vehicles
            .build();
    
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(debtRepository.findByCustomerAndFilter(any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(Collections.emptyList()));
    
    DebtDetailResponseDto result = service.getDebtsByCustomer(1L, null, null, 0, 5, null);
    
    assertNull(result.getLicensePlate());
}
```

---

#### 3.2 TransactionServiceImpl - Null Invoice/Debt
**Vấn đề / Issue**: `invoice.getServiceTicket().getCustomer()` có thể throw NPE nếu `getServiceTicket()` là null

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC215 | **processPaymentByPaymentLinkId_ShouldHandleNullServiceTicket_WhenInvoiceHasNoServiceTicket** | Nếu invoice không có serviceTicket, phải handle gracefully |
| TC216 | **processPaymentByPaymentLinkId_ShouldHandleNullCustomer_WhenServiceTicketHasNoCustomer** | Nếu serviceTicket không có customer, phải handle gracefully |

---

#### 3.3 PayrollServiceImpl - Null Collections
**Vấn đề / Issue**: `customer.getVehicles().isEmpty()` và các null checks khác

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC217 | **getPayrollDetail_ShouldHandleNullAccount_WhenEmployeeHasNoAccount** | Nếu employee không có account, phải return null cho role |
| TC218 | **getPayrollDetail_ShouldHandleNullAllowances_WhenNoAllowances** | Nếu không có allowances, phải return empty list |

---

### 4. Data Consistency & Integrity / Tính Nhất Quán & Toàn Vẹn Dữ Liệu

#### 4.1 payDebt - Amount Consistency
**Vấn đề / Issue**: Có thể có inconsistency giữa `debt.paidAmount` và tổng các transactions

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC219 | **payDebt_ShouldMaintainConsistency_WhenMultiplePayments** | Tổng các transactions phải bằng `debt.paidAmount` |
| TC220 | **payDebt_ShouldRecalculatePaidAmount_WhenTransactionsExist** | Nếu có transactions, phải recalculate `paidAmount` từ transactions |
| TC221 | **payDebt_ShouldPreventOverPayment_WhenAmountExceedsDebt** | Không cho phép payment vượt quá debt amount (trừ khi có business rule cho phép) |

---

#### 4.2 approvePayroll - Duplicate Prevention
**Vấn đề / Issue**: `submitPayroll` check duplicate nhưng `approvePayroll` không check

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC222 | **approvePayroll_ShouldPreventDuplicateApproval_WhenAlreadyApproved** | Không cho phép approve payroll đã được approve |
| TC223 | **approvePayroll_ShouldPreventApproval_WhenStatusIsPaid** | Không cho phép approve payroll đã được paid |

---

#### 4.3 createInvoice - Amount Calculation
**Vấn đề / Issue**: Có thể có inconsistency giữa `invoice.totalAmount`, `discount`, và `debtAmount`

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC224 | **createInvoice_ShouldMaintainAmountConsistency_WhenDiscountApplied** | `finalAmount = totalAmount - discount - debtAmount` phải đúng |
| TC225 | **createInvoice_ShouldPreventNegativeFinalAmount_WhenDiscountTooLarge** | Không cho phép discount lớn hơn totalAmount |

---

### 5. Boundary Conditions & Edge Cases / Điều Kiện Biên & Trường Hợp Đặc Biệt

#### 5.1 BigDecimal Overflow & Precision
**Vấn đề / Issue**: BigDecimal có thể overflow hoặc mất precision

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC226 | **createPart_ShouldHandleVeryLargePurchasePrice_WhenPriceExceedsMax** | Xử lý purchasePrice rất lớn (gần MAX_VALUE) |
| TC227 | **payDebt_ShouldHandlePrecisionLoss_WhenCalculatingRemaining** | Đảm bảo không mất precision khi tính toán remaining amount |
| TC228 | **createInvoice_ShouldHandleDecimalPrecision_WhenCalculatingFinalAmount** | Đảm bảo precision khi tính finalAmount với discount |

---

#### 5.2 Date & Time Edge Cases
**Vấn đề / Issue**: Các edge cases với dates (leap year, month end, timezone)

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC229 | **createDebt_ShouldHandleLeapYear_WhenDueDateIsFeb29** | Xử lý dueDate vào ngày 29/2 trong năm nhuận |
| TC230 | **createAppointment_ShouldHandleMonthEnd_WhenDateIsLastDayOfMonth** | Xử lý appointment vào ngày cuối tháng |
| TC231 | **getPayrollDetail_ShouldHandleLeapYear_WhenCalculatingDays** | Tính toán số ngày trong năm nhuận |
| TC232 | **createAppointment_ShouldHandleTimezone_WhenDateCrossesMidnight** | Xử lý timezone khi date crosses midnight |

---

#### 5.3 Empty Collections & Null Lists
**Vấn đề / Issue**: Xử lý empty collections và null lists

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC233 | **getAllDebtsSummary_ShouldHandleEmptyDebtList_WhenCustomerHasNoDebts** | Xử lý khi customer không có debts |
| TC234 | **createQuotation_ShouldHandleEmptyItemsList_WhenNoItemsProvided** | Xử lý khi quotation không có items |
| TC235 | **approvePurchaseRequest_ShouldHandleEmptyItemsList_WhenNoItems** | Xử lý khi purchase request không có items |

---

### 6. External Service Failures / Lỗi Dịch Vụ Bên Ngoài

#### 6.1 PayOS Integration Failures
**Vấn đề / Issue**: PayOS API có thể fail hoặc timeout

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC236 | **createTransaction_ShouldHandlePayOSApiFailure_WhenApiIsDown** | Xử lý khi PayOS API down |
| TC237 | **createTransaction_ShouldHandlePayOSTimeout_WhenResponseDelayed** | Xử lý khi PayOS API timeout |
| TC238 | **createTransaction_ShouldRetry_WhenPayOSReturnsTemporaryError** | Retry khi PayOS trả về temporary error |
| TC239 | **handleCallback_ShouldHandleInvalidSignature_WhenSignatureMismatch** | Xử lý khi PayOS callback signature không hợp lệ |
| TC240 | **handleCallback_ShouldHandleDuplicateCallback_WhenCallbackReceivedTwice** | Xử lý khi nhận duplicate callback |

**Code Example**:
```java
@Test
void createTransaction_ShouldHandlePayOSApiFailure_WhenApiIsDown() throws Exception {
    CreateTransactionRequestDto request = CreateTransactionRequestDto.builder()
            .method(TransactionMethod.BANK_TRANSFER)
            .price(1000000L)
            .build();
    
    when(payOS.paymentRequests().create(any())).thenThrow(new RuntimeException("PayOS API is down"));
    
    assertThrows(RuntimeException.class, () -> service.createTransaction(request));
    
    // Verify transaction was not saved
    verify(transactionRepository, never()).save(any());
}
```

---

#### 6.2 Database Connection Failures
**Vấn đề / Issue**: Database có thể mất kết nối

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC241 | **createPart_ShouldHandleDatabaseConnectionLoss_WhenConnectionLost** | Xử lý khi database connection bị mất |
| TC242 | **payDebt_ShouldRollback_WhenDatabaseConnectionLostMidTransaction** | Rollback khi connection mất giữa transaction |

---

### 7. Security & Authorization / Bảo Mật & Phân Quyền

#### 7.1 SQL Injection Prevention
**Vấn đề / Issue**: Sort parameters có thể bị SQL injection

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC243 | **getAllDebtsSummary_ShouldPreventSQLInjection_WhenSortContainsSQL** | Prevent SQL injection trong sort parameter |
| TC244 | **getAllDebtsSummary_ShouldSanitizeSortParameter_WhenInvalidSortProvided** | Sanitize sort parameter để prevent injection |

**Code Example**:
```java
@Test
void getAllDebtsSummary_ShouldPreventSQLInjection_WhenSortContainsSQL() {
    String maliciousSort = "totalRemaining; DROP TABLE debts;--";
    
    // Should sanitize or reject malicious input
    assertThrows(IllegalArgumentException.class, 
            () -> service.getAllDebtsSummary(0, 5, maliciousSort, "DESC"));
}
```

---

#### 7.2 Authorization Checks
**Vấn đề / Issue**: Không có test cases cho authorization

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC245 | **approvePayroll_ShouldCheckAuthorization_WhenUserIsNotManager** | Chỉ manager mới được approve payroll |
| TC246 | **createManualVoucher_ShouldCheckAuthorization_WhenUserIsNotAccountant** | Chỉ accountant mới được tạo voucher |
| TC247 | **approveVoucher_ShouldCheckAuthorization_WhenUserIsNotAuthorized** | Chỉ authorized user mới được approve voucher |

---

### 8. Performance & Scalability / Hiệu Suất & Khả Năng Mở Rộng

#### 8.1 Large Dataset Handling
**Vấn đề / Issue**: Performance với large datasets

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC248 | **getAllPart_ShouldHandleLargeDataset_WhenThousandsOfParts** | Performance test với hàng nghìn parts |
| TC249 | **getAllDebtsSummary_ShouldHandleLargeDataset_WhenThousandsOfDebts** | Performance test với hàng nghìn debts |
| TC250 | **getPayrollPreview_ShouldHandleLargeEmployeeList_WhenHundredsOfEmployees** | Performance test với hàng trăm employees |

---

#### 8.2 N+1 Query Problems
**Vấn đề / Issue**: Có thể có N+1 query problems

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC251 | **getAllPart_ShouldUseEagerLoading_WhenFetchingCategories** | Sử dụng eager loading để prevent N+1 |
| TC252 | **getDebtsByCustomer_ShouldUseEagerLoading_WhenFetchingTransactions** | Sử dụng eager loading cho transactions |

---

### 9. Business Logic Edge Cases / Trường Hợp Đặc Biệt Logic Nghiệp Vụ

#### 9.1 Negative Amounts
**Vấn đề / Issue**: Có thể có negative amounts trong một số trường hợp

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC253 | **payDebt_ShouldPreventNegativeRemaining_WhenOverPayment** | Không cho phép remaining amount âm |
| TC254 | **createInvoice_ShouldPreventNegativeFinalAmount_WhenDiscountTooLarge** | Không cho phép finalAmount âm |

---

#### 9.2 Status Transition Validation
**Vấn đề / Issue**: Không có validation cho status transitions

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC255 | **updateServiceTicket_ShouldPreventInvalidStatusTransition_WhenTransitionNotAllowed** | Không cho phép chuyển status không hợp lệ |
| TC256 | **approvePurchaseRequest_ShouldPreventApproval_WhenStatusIsNotPending** | Chỉ cho phép approve khi status là PENDING |
| TC257 | **approvePayroll_ShouldPreventApproval_WhenStatusIsPaid** | Không cho phép approve payroll đã paid |

---

### 10. Data Validation & Constraints / Xác Thực & Ràng Buộc Dữ Liệu

#### 10.1 String Length Validation
**Vấn đề / Issue**: Không có validation cho string length

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC258 | **createPart_ShouldRejectVeryLongName_WhenNameExceedsMaxLength** | Reject name quá dài |
| TC259 | **createDeduction_ShouldRejectVeryLongContent_WhenContentExceedsMaxLength** | Reject content quá dài |
| TC260 | **createManualVoucher_ShouldRejectVeryLongDescription_WhenDescriptionExceedsMaxLength** | Reject description quá dài |

---

#### 10.2 Required Field Validation
**Vấn đề / Issue**: Một số required fields có thể không được validate

**Test Cases Cần Bổ Sung**:

| TC | Description | Expected Behavior |
|----|-------------|-------------------|
| TC261 | **createPart_ShouldRejectNullName_WhenNameIsNull** | Reject khi name là null |
| TC262 | **createDebt_ShouldRejectNullAmount_WhenAmountIsNull** | Reject khi amount là null |
| TC263 | **createAppointment_ShouldRejectNullDate_WhenDateIsNull** | Reject khi date là null |

---

## Tổng Kết / Summary

### Thống Kê Test Cases Bổ Sung / Additional Test Cases Statistics

| Category | Số Lượng | Priority |
|----------|----------|----------|
| Concurrency & Race Conditions | 7 | HIGH |
| Transaction Rollback & Error Recovery | 5 | HIGH |
| Null Safety & NPE Prevention | 6 | HIGH |
| Data Consistency & Integrity | 7 | HIGH |
| Boundary Conditions & Edge Cases | 9 | MEDIUM |
| External Service Failures | 5 | HIGH |
| Security & Authorization | 5 | HIGH |
| Performance & Scalability | 5 | MEDIUM |
| Business Logic Edge Cases | 5 | MEDIUM |
| Data Validation & Constraints | 6 | MEDIUM |
| **TỔNG CỘNG** | **60** | |

### Priority Breakdown / Phân Loại Ưu Tiên

- **HIGH Priority**: 35 test cases (58%)
- **MEDIUM Priority**: 25 test cases (42%)

---

## Khuyến Nghị / Recommendations

1. **Ưu tiên implement các test cases HIGH priority** trước, đặc biệt là:
   - Concurrency & Race Conditions
   - Transaction Rollback & Error Recovery
   - Null Safety & NPE Prevention
   - External Service Failures

2. **Sử dụng các testing tools**:
   - `@Transactional` với `@Rollback` để test transaction rollback
   - `CountDownLatch` hoặc `CompletableFuture` để test concurrency
   - `@MockBean` để mock external services (PayOS)

3. **Code improvements cần thiết**:
   - Thêm optimistic locking cho các entities quan trọng
   - Thêm null checks và defensive programming
   - Thêm retry logic cho external service calls
   - Thêm validation cho status transitions

4. **Monitoring & Alerting**:
   - Monitor các concurrency issues
   - Alert khi external services fail
   - Track transaction rollback rates

---

## Tài Liệu Tham Khảo / References

- `TestCaseMatrix_Comprehensive_Design.md` - Thiết kế ma trận test case tổng hợp
- Spring Transaction Management Documentation
- JUnit 5 Concurrency Testing Guide
- PayOS API Documentation

