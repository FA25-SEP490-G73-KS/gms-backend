# Test Case Matrix Implementation Guide / Hướng Dẫn Triển Khai Ma Trận Test Case

## Tổng Quan / Overview

Tài liệu này hướng dẫn cách implement các test cases đã được thiết kế trong `TestCaseMatrix_Comprehensive_Design.md` vào code test thực tế.

This document guides how to implement the test cases designed in `TestCaseMatrix_Comprehensive_Design.md` into actual test code.

---

## Cấu Trúc Test Case / Test Case Structure

Mỗi test case trong ma trận thiết kế cần được implement theo cấu trúc sau:

Each test case in the design matrix needs to be implemented following this structure:

```java
@Test
void testCaseName_ShouldExpectedBehavior_WhenCondition() {
    // Arrange - Setup preconditions
    // Act - Execute the method
    // Assert - Verify results
}
```

---

## Danh Sách Test Cases Cần Implement / List of Test Cases to Implement

### 1. createPart (TC001-TC010)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/PartServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC003: Create universal part (null category, null vehicleModel)
- TC004: Create part with override sellingPrice
- TC005: Create part with minimum valid purchasePrice
- TC007: Create part with all optional fields
- TC008: Create part with only required fields
- TC009: Create part with maximum valid purchasePrice

**Ví dụ Implementation**:
```java
@Test
void createPart_ShouldCreateUniversalPart_WhenCategoryAndVehicleModelAreNull() {
    // TC003: Create universal part
    PartUpdateReqDto dto = PartUpdateReqDto.builder()
            .name("Universal Part")
            .purchasePrice(new BigDecimal("100000"))
            .marketId(1L)
            .unitId(1L)
            .supplierId(1L)
            .categoryId(null)  // Universal part
            .vehicleModelId(null)  // Universal part
            .build();
    
    // Mock dependencies
    when(marketRepo.findById(1L)).thenReturn(Optional.of(market));
    when(unitRepo.findById(1L)).thenReturn(Optional.of(unit));
    when(supplierRepo.findById(1L)).thenReturn(Optional.of(supplier));
    when(categoryRepo.findById(any())).thenReturn(Optional.empty());
    when(vehicleModelRepo.findById(any())).thenReturn(Optional.empty());
    
    // Execute and verify
    PartReqDto result = service.createPart(dto);
    assertNotNull(result);
    verify(partRepository).save(any(Part.class));
}
```

---

### 2. createAllowance (TC031-TC040)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/AllowanceServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC032: Create allowance with OVERTIME type
- TC033: Create allowance with TRANSPORTATION type
- TC034: Create allowance with null type (invalid)
- TC036: Create allowance with boundary amount (0)
- TC037: Create allowance with invalid amount (< 0)
- TC038: Create allowance with maximum valid amount
- TC039: Create allowance with minimum valid amount

**Ví dụ Implementation**:
```java
@Test
void createAllowance_ShouldReturnDto_WhenOVERTIMEType() {
    // TC032: Create allowance with OVERTIME type
    AllowanceRequestDto dto = AllowanceRequestDto.builder()
            .employeeId(1L)
            .type(AllowanceType.OVERTIME)
            .amount(new BigDecimal("200000"))
            .build();
    
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    when(allowanceRepository.save(any(Allowance.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    
    AllowanceDto result = allowanceService.createAllowance(dto, accountant);
    
    assertNotNull(result);
    assertEquals(AllowanceType.OVERTIME.getVietnamese(), result.getType());
    verify(allowanceRepository).save(any(Allowance.class));
}

@Test
void createAllowance_ShouldThrowValidationException_WhenTypeIsNull() {
    // TC034: Create allowance with null type (invalid)
    AllowanceRequestDto dto = AllowanceRequestDto.builder()
            .employeeId(1L)
            .type(null)  // Invalid
            .amount(new BigDecimal("150000"))
            .build();
    
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    
    assertThrows(ValidationException.class,
            () -> allowanceService.createAllowance(dto, accountant));
}
```

---

### 3. createDeduction (TC041-TC050)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/DeductionServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC041: Create deduction with LATE type
- TC042: Create deduction with ABSENT type
- TC044: Create deduction with null type (invalid)
- TC045: Create deduction with boundary amount (0)
- TC047: Create deduction with maximum valid amount
- TC048: Create deduction with minimum valid amount
- TC049: Create deduction with long content

**Ví dụ Implementation**:
```java
@Test
void createDeduction_ShouldCreateAndReturnDto_WhenLATEType() {
    // TC041: Create deduction with LATE type
    DeductionRequestDto dto = DeductionRequestDto.builder()
            .employeeId(1L)
            .type(DeductionType.LATE)
            .content("Late 30 minutes")
            .amount(new BigDecimal("50000"))
            .build();
    
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    when(employeeRepository.findById(2L)).thenReturn(Optional.of(creator));
    when(deductionRepository.save(any(Deduction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    
    DeductionDto result = service.createDeduction(dto, creator);
    
    assertNotNull(result);
    assertEquals("Đi muộn", result.getType());
    verify(deductionRepository).save(any(Deduction.class));
}

@Test
void createDeduction_ShouldThrowValidationException_WhenTypeIsNull() {
    // TC044: Create deduction with null type (invalid)
    DeductionRequestDto dto = DeductionRequestDto.builder()
            .employeeId(1L)
            .type(null)  // Invalid
            .content("Some content")
            .amount(new BigDecimal("50000"))
            .build();
    
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    
    assertThrows(ValidationException.class,
            () -> service.createDeduction(dto, creator));
}
```

---

### 4. createDebt (TC051-TC060)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/DebtServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC052: Create debt with default dueDate (7 days)
- TC053: Create debt with custom dueDate
- TC054: Create debt with minimum valid amount
- TC055: Create debt with maximum valid amount
- TC056: Create debt with boundary amount (0)
- TC057: Create debt with invalid amount (< 0)
- TC058: Create debt with past dueDate (invalid)
- TC059: Create debt with null amount (invalid)

**Ví dụ Implementation**:
```java
@Test
void createDebt_ShouldSetDefaultDueDate_WhenDueDateNotProvided() {
    // TC052: Create debt with default dueDate (7 days)
    CreateDebtDto dto = CreateDebtDto.builder()
            .customerId(1L)
            .serviceTicketId(1L)
            .amount(new BigDecimal("1000000"))
            .dueDate(null)  // Should default to 7 days
            .build();
    
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(serviceTicket));
    when(debtRepository.save(any(Debt.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    
    DebtDetailResponseDto result = service.createDebt(dto);
    
    assertNotNull(result);
    ArgumentCaptor<Debt> captor = ArgumentCaptor.forClass(Debt.class);
    verify(debtRepository).save(captor.capture());
    Debt saved = captor.getValue();
    assertEquals(LocalDate.now().plusDays(7), saved.getDueDate());
}

@Test
void createDebt_ShouldThrowValidationException_WhenAmountIsZero() {
    // TC056: Create debt with boundary amount (0)
    CreateDebtDto dto = CreateDebtDto.builder()
            .customerId(1L)
            .serviceTicketId(1L)
            .amount(new BigDecimal("0"))  // Invalid
            .build();
    
    assertThrows(ValidationException.class,
            () -> service.createDebt(dto));
}
```

---

### 5. payDebt (TC061-TC070)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/DebtServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC063: Pay debt with over payment (CASH)
- TC064: Pay debt with minimum valid amount
- TC065: Pay debt with maximum valid amount
- TC067: Pay debt with boundary amount (0)
- TC068: Pay debt with invalid amount (< 0)
- TC069: Pay debt with amount > remaining amount

**Ví dụ Implementation**:
```java
@Test
void payDebt_ShouldHandleOverPayment_WhenAmountExceedsRemaining() {
    // TC063: Pay debt with over payment
    Debt debt = Debt.builder()
            .debtId(1L)
            .amount(new BigDecimal("1000000"))
            .paidAmount(new BigDecimal("0"))
            .build();
    
    PayDebtRequestDto dto = PayDebtRequestDto.builder()
            .debtId(1L)
            .amount(new BigDecimal("1500000"))  // Over payment
            .method(TransactionMethod.CASH)
            .build();
    
    when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
    when(transactionService.createTransaction(any()))
            .thenReturn(new TransactionResponseDto());
    
    TransactionResponseDto result = service.payDebt(dto);
    
    assertNotNull(result);
    ArgumentCaptor<Debt> captor = ArgumentCaptor.forClass(Debt.class);
    verify(debtRepository).save(captor.capture());
    Debt saved = captor.getValue();
    assertEquals(new BigDecimal("1500000"), saved.getPaidAmount());
    assertEquals(DebtStatus.PAID_IN_FULL, saved.getStatus());
}
```

---

### 6. getAllDebtsSummary (TC071-TC080)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/DebtServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC072: Get all debts summary sorted by totalRemaining DESC
- TC073: Get all debts summary sorted by dueDate ASC
- TC075: Get all debts summary with pagination (page 1, size 5)
- TC076: Get all debts summary with large page size
- TC077: Get all debts summary with null values handling
- TC078: Get all debts summary with multiple customers
- TC079: Get all debts summary with single customer

**Ví dụ Implementation**:
```java
@Test
void getAllDebtsSummary_ShouldSortByTotalRemainingDesc_WhenSortByProvided() {
    // TC072: Get all debts summary sorted by totalRemaining DESC
    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "totalRemaining"));
    
    Object[] row1 = new Object[]{1L, "Customer 1", "0909000001", 
            new BigDecimal("200000"), new BigDecimal("0"), new BigDecimal("200000"), 
            LocalDate.now().plusDays(7), "OUTSTANDING"};
    Object[] row2 = new Object[]{2L, "Customer 2", "0909000002", 
            new BigDecimal("100000"), new BigDecimal("0"), new BigDecimal("100000"), 
            LocalDate.now().plusDays(7), "OUTSTANDING"};
    
    Page<Object[]> page = new PageImpl<>(List.of(row1, row2), pageable, 2);
    when(debtRepository.findTotalDebtGroupedByCustomer(pageable)).thenReturn(page);
    
    Page<CustomerDebtSummaryDto> result = service.getAllDebtsSummary(0, 5, "totalRemaining", "DESC");
    
    assertEquals(2, result.getTotalElements());
    assertEquals(new BigDecimal("200000"), result.getContent().get(0).getTotalRemaining());
}
```

---

### 7. createInvoice (TC081-TC090)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/InvoiceServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC082: Create invoice with discount
- TC083: Create invoice with debtAmount
- TC084: Create invoice with discount and debtAmount
- TC085: Create invoice with customer from ServiceTicket
- TC086: Create invoice with null customer
- TC087: Create invoice with maximum valid discount
- TC088: Create invoice with invalid discount (< 0)
- TC089: Create invoice with invalid debtAmount (< 0)

**Ví dụ Implementation**:
```java
@Test
void createInvoice_ShouldApplyDiscount_WhenDiscountProvided() {
    // TC082: Create invoice with discount
    when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
    when(priceQuotationRepo.findById(1L)).thenReturn(Optional.of(quotation));
    when(codeSequenceService.generateCode("HD")).thenReturn("HD-2025-00001");
    
    InvoiceDetailResDto result = service.createInvoice(1L, 1L, new BigDecimal("10000"), 
            new BigDecimal("0"), null);
    
    assertNotNull(result);
    ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
    verify(invoiceRepo).save(captor.capture());
    Invoice saved = captor.getValue();
    assertEquals(new BigDecimal("10000"), saved.getDiscount());
}
```

---

### 8. payInvoice (TC091-TC100)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/InvoiceServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC092: Pay invoice with partial amount (CASH)
- TC093: Pay invoice with over payment (CASH)
- TC094: Pay invoice with minimum valid amount
- TC095: Pay invoice with maximum valid amount
- TC096: Pay invoice with BANK_TRANSFER
- TC097: Pay invoice with boundary amount (0)
- TC098: Pay invoice with invalid amount (< 0)
- TC099: Pay invoice with amount > remaining amount

**Ví dụ Implementation**:
```java
@Test
void payInvoice_ShouldHandlePartialPayment_WhenAmountLessThanTotal() {
    // TC092: Pay invoice with partial amount
    Invoice invoice = Invoice.builder()
            .invoiceId(1L)
            .totalAmount(new BigDecimal("1000000"))
            .paidAmount(new BigDecimal("0"))
            .status(InvoiceStatus.UNPAID)
            .build();
    
    PayInvoiceRequestDto dto = PayInvoiceRequestDto.builder()
            .invoiceId(1L)
            .amount(new BigDecimal("500000"))  // Partial payment
            .method(TransactionMethod.CASH)
            .build();
    
    when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
    when(transactionService.createTransaction(any()))
            .thenReturn(new TransactionResponseDto());
    
    TransactionResponseDto result = service.payInvoice(dto);
    
    assertNotNull(result);
    ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
    verify(invoiceRepo).save(captor.capture());
    Invoice saved = captor.getValue();
    assertEquals(new BigDecimal("500000"), saved.getPaidAmount());
    assertEquals(InvoiceStatus.PARTIALLY_PAID, saved.getStatus());
}
```

---

### 9. createServiceTicket (TC101-TC110)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/ServiceTicketServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC102: Create service ticket with new customer and existing vehicle
- TC103: Create service ticket with existing customer and new vehicle
- TC104: Create service ticket with new customer and new vehicle
- TC105: Create service ticket with all required fields
- TC106: Create service ticket with minimal fields
- TC107: Create service ticket with null customerId (new customer)
- TC108: Create service ticket with null vehicleId (new vehicle)
- TC109: Create service ticket with invalid phone format
- TC110: Brand not found

**Ví dụ Implementation**:
```java
@Test
void createServiceTicket_ShouldCreateNewCustomer_WhenCustomerIdIsNull() {
    // TC107: Create service ticket with null customerId (new customer)
    ServiceTicketRequestDto dto = ServiceTicketRequestDto.builder()
            .customerId(null)  // New customer
            .customerName("New Customer")
            .phone("0912345678")
            .vehicleId(1L)
            .build();
    
    when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
    when(customerRepository.findByPhone("0912345678")).thenReturn(Optional.empty());
    when(customerRepository.save(any(Customer.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    when(codeSequenceService.generateCode("STK")).thenReturn("STK-2025-00001");
    
    ServiceTicketResponseDto result = service.createServiceTicket(dto, currentEmployee);
    
    assertNotNull(result);
    verify(customerRepository).save(any(Customer.class));
}
```

---

### 10. updateServiceTicket (TC111-TC120)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/ServiceTicketServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC111: Update service ticket status to PENDING
- TC112: Update service ticket status to IN_PROGRESS
- TC113: Update service ticket status to COMPLETED
- TC114: Update service ticket with customerName only
- TC115: Update service ticket with partial fields
- TC116: Update service ticket with all fields
- TC117: Update service ticket with minimal fields
- TC118: Update service ticket with null status (no update)
- TC119: Update service ticket with invalid status

**Ví dụ Implementation**:
```java
@Test
void updateServiceTicket_ShouldUpdateStatus_WhenStatusProvided() {
    // TC111: Update service ticket status to PENDING
    ServiceTicket ticket = ServiceTicket.builder()
            .serviceTicketId(1L)
            .status(ServiceTicketStatus.IN_PROGRESS)
            .build();
    
    TicketUpdateReqDto dto = TicketUpdateReqDto.builder()
            .status(ServiceTicketStatus.PENDING)
            .build();
    
    when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
    when(serviceTicketRepository.save(any(ServiceTicket.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    
    ServiceTicketResponseDto result = service.updateServiceTicket(1L, dto);
    
    assertNotNull(result);
    ArgumentCaptor<ServiceTicket> captor = ArgumentCaptor.forClass(ServiceTicket.class);
    verify(serviceTicketRepository).save(captor.capture());
    ServiceTicket saved = captor.getValue();
    assertEquals(ServiceTicketStatus.PENDING, saved.getStatus());
}
```

---

### 11. createQuotation (TC121-TC130)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/PriceQuotationServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC122: Create quotation with single item
- TC123: Create quotation with multiple items
- TC124: Create quotation with minimum quantity (1)
- TC125: Create quotation with maximum quantity
- TC126: Create quotation with empty items list
- TC127: Create quotation with null items
- TC128: Create quotation with invalid quantity (0)
- TC129: Create quotation with invalid quantity (< 0)

**Ví dụ Implementation**:
```java
@Test
void createQuotation_ShouldCreateWithMultipleItems_WhenMultipleItemsProvided() {
    // TC123: Create quotation with multiple items
    QuotationItemRequestDto item1 = QuotationItemRequestDto.builder()
            .partId(1L)
            .quantity(2)
            .build();
    QuotationItemRequestDto item2 = QuotationItemRequestDto.builder()
            .partId(2L)
            .quantity(3)
            .build();
    
    CreateQuotationRequestDto dto = CreateQuotationRequestDto.builder()
            .serviceTicketId(1L)
            .items(List.of(item1, item2))
            .build();
    
    when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(serviceTicket));
    when(partRepository.findById(1L)).thenReturn(Optional.of(part1));
    when(partRepository.findById(2L)).thenReturn(Optional.of(part2));
    
    PriceQuotationResponseDto result = service.createQuotation(dto);
    
    assertNotNull(result);
    assertEquals(2, result.getItems().size());
}
```

---

### 12. approvePurchaseRequest (TC131-TC140)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/PurchaseRequestServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC132: Approve purchase request with items
- TC133: Approve purchase request without items
- TC134: Approve purchase request and create stock receipt
- TC135: Approve purchase request with multiple items
- TC136: Approve purchase request with single item
- TC137: Approve purchase request with stock receipt creation failure
- TC138: Approve purchase request that is already approved
- TC139: Approve purchase request with no items

**Ví dụ Implementation**:
```java
@Test
void approvePurchaseRequest_ShouldCreateStockReceipt_WhenApproved() {
    // TC134: Approve purchase request and create stock receipt
    PurchaseRequest request = PurchaseRequest.builder()
            .purchaseRequestId(1L)
            .reviewStatus(ManagerReviewStatus.PENDING)
            .items(List.of(item1, item2))
            .build();
    
    when(purchaseRequestRepository.findById(1L)).thenReturn(Optional.of(request));
    when(purchaseRequestRepository.save(any(PurchaseRequest.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    doNothing().when(stockReceiptService).createReceiptFromPurchaseRequest(any());
    
    service.approvePurchaseRequest(1L);
    
    ArgumentCaptor<PurchaseRequest> captor = ArgumentCaptor.forClass(PurchaseRequest.class);
    verify(purchaseRequestRepository).save(captor.capture());
    PurchaseRequest saved = captor.getValue();
    assertEquals(ManagerReviewStatus.APPROVED, saved.getReviewStatus());
    verify(stockReceiptService).createReceiptFromPurchaseRequest(any());
}
```

---

### 13. createAppointment (TC141-TC150)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/AppointmentServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC142: Create appointment with new customer and existing vehicle
- TC143: Create appointment with existing customer and new vehicle
- TC144: Create appointment with new customer and new vehicle
- TC145: Create appointment with available time slot
- TC146: Create appointment with time slot already booked
- TC147: Create appointment with daily limit not exceeded
- TC148: Create appointment with daily limit exceeded (max 20/day)
- TC149: Create appointment with past date (invalid)
- TC150: Create appointment with invalid time slot (outside hours)

**Ví dụ Implementation**:
```java
@Test
void createAppointment_ShouldThrowException_WhenTimeSlotAlreadyBooked() {
    // TC146: Create appointment with time slot already booked
    AppointmentRequestDto dto = AppointmentRequestDto.builder()
            .customerId(1L)
            .vehicleId(1L)
            .appointmentDate(LocalDate.now().plusDays(1))
            .timeSlot("08:00")
            .build();
    
    when(appointmentRepository.countByAppointmentDateAndTimeSlot(
            LocalDate.now().plusDays(1), "08:00")).thenReturn(1L);
    
    assertThrows(IllegalArgumentException.class,
            () -> service.createAppointment(dto));
}
```

---

### 14. createTransaction (TC151-TC160)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/TransactionServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC152: Create transaction for debt payment (BANK_TRANSFER)
- TC153: Create transaction for invoice payment (CASH)
- TC154: Create transaction for invoice payment (BANK_TRANSFER)
- TC155: Create transaction with valid amount
- TC156: Create transaction with boundary amount (0)
- TC157: Create transaction with invalid amount (< 0)
- TC158: Create transaction with null amount
- TC159: Create transaction with invalid type
- TC160: Debt/Invoice not found

**Ví dụ Implementation**:
```java
@Test
void createTransaction_ShouldCreatePayOSLink_WhenBankTransfer() {
    // TC152: Create transaction for debt payment (BANK_TRANSFER)
    CreateTransactionRequestDto dto = CreateTransactionRequestDto.builder()
            .type(PaymentTransactionType.DEBT_PAYMENT)
            .method(TransactionMethod.BANK_TRANSFER)
            .amount(new BigDecimal("1000000"))
            .debtId(1L)
            .build();
    
    when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
    when(payOSService.createPaymentLink(any())).thenReturn("https://payos.vn/...");
    
    TransactionResponseDto result = service.createTransaction(dto);
    
    assertNotNull(result);
    assertNotNull(result.getPaymentLink());
    verify(payOSService).createPaymentLink(any());
}
```

---

### 15. handleCallback (TC161-TC170)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/TransactionServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC162: Handle callback with cancelled code (07)
- TC163: Handle callback with failed code (09)
- TC164: Handle callback and update debt status
- TC165: Handle callback and update invoice status
- TC166: Handle callback with already processed transaction
- TC167: Handle callback with invalid code
- TC168: Handle callback with null data
- TC169: Handle callback with invalid signature
- TC170: Transaction not found

**Ví dụ Implementation**:
```java
@Test
void handleCallback_ShouldUpdateDebtStatus_WhenSuccess() {
    // TC164: Handle callback and update debt status
    Transaction transaction = Transaction.builder()
            .transactionId(1L)
            .type(PaymentTransactionType.DEBT_PAYMENT)
            .status(TransactionStatus.PENDING)
            .debt(debt)
            .build();
    
    PayOSCallbackDto callback = PayOSCallbackDto.builder()
            .code("00")
            .desc("Success")
            .data(PayOSDataDto.builder().transactionId(1L).build())
            .build();
    
    when(transactionRepository.findByTransactionId(1L)).thenReturn(Optional.of(transaction));
    when(transactionRepository.save(any(Transaction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    
    service.handleCallback(callback);
    
    ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
    verify(transactionRepository).save(captor.capture());
    Transaction saved = captor.getValue();
    assertEquals(TransactionStatus.SUCCESS, saved.getStatus());
    verify(debtRepository).save(any(Debt.class));
}
```

---

### 16. approvePayroll (TC171-TC180)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/PayrollServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC172: Approve payroll with PENDING status
- TC173: Approve payroll with multiple employees
- TC174: Approve payroll with single employee
- TC175: Approve payroll with allowances
- TC176: Approve payroll with deductions
- TC177: Approve payroll with allowances and deductions
- TC178: Approve payroll that is already approved
- TC179: Approve payroll with zero total amount
- TC180: Payroll not found

**Ví dụ Implementation**:
```java
@Test
void approvePayroll_ShouldThrowException_WhenAlreadyApproved() {
    // TC178: Approve payroll that is already approved
    Payroll payroll = Payroll.builder()
            .payrollId(1L)
            .status(PayrollStatus.APPROVED)
            .build();
    
    when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));
    
    assertThrows(IllegalStateException.class,
            () -> service.approvePayroll(1L));
}
```

---

### 17. createManualVoucher (TC181-TC190)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/ManualVoucherServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC182: Create manual voucher with RECEIPT type
- TC183: Create manual voucher with valid amount
- TC184: Create manual voucher with description
- TC185: Create manual voucher with attachments
- TC186: Create manual voucher without attachments
- TC187: Create manual voucher with boundary amount (0)
- TC188: Create manual voucher with invalid amount (< 0)
- TC189: Create manual voucher with null description
- TC190: Create manual voucher with invalid type

**Ví dụ Implementation**:
```java
@Test
void createManualVoucher_ShouldCreateWithAttachments_WhenFilesProvided() {
    // TC185: Create manual voucher with attachments
    CreateManualVoucherDto dto = CreateManualVoucherDto.builder()
            .type(LedgerVoucherType.PAYMENT)
            .amount(new BigDecimal("1000000"))
            .description("Payment for supplies")
            .attachments(List.of(mockMultipartFile1, mockMultipartFile2))
            .build();
    
    when(fileStorageService.uploadFiles(anyList())).thenReturn(List.of("file1.pdf", "file2.pdf"));
    when(ledgerVoucherRepository.save(any(LedgerVoucher.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    
    LedgerVoucherDetailResponse result = service.createManualVoucher(dto, creator);
    
    assertNotNull(result);
    verify(fileStorageService).uploadFiles(anyList());
}
```

---

### 18. approveVoucher (TC191-TC200)

**File**: `test-classes/fpt/edu/vn/gms/service/impl/ManualVoucherServiceImplTest.java`

**Test Cases cần bổ sung**:
- TC192: Approve voucher with PENDING status
- TC193: Approve voucher with PAYMENT type
- TC194: Approve voucher with RECEIPT type
- TC195: Approve voucher with attachments
- TC196: Approve voucher without attachments
- TC197: Approve voucher that is already approved
- TC198: Approve voucher that is already rejected
- TC199: Reject voucher with reason
- TC200: Voucher not found

**Ví dụ Implementation**:
```java
@Test
void approveVoucher_ShouldUpdateStatus_WhenPending() {
    // TC192: Approve voucher with PENDING status
    LedgerVoucher voucher = LedgerVoucher.builder()
            .voucherId(1L)
            .status(LedgerVoucherStatus.PENDING)
            .build();
    
    when(ledgerVoucherRepository.findById(1L)).thenReturn(Optional.of(voucher));
    when(ledgerVoucherRepository.save(any(LedgerVoucher.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    
    service.approveVoucher(1L);
    
    ArgumentCaptor<LedgerVoucher> captor = ArgumentCaptor.forClass(LedgerVoucher.class);
    verify(ledgerVoucherRepository).save(captor.capture());
    LedgerVoucher saved = captor.getValue();
    assertEquals(LedgerVoucherStatus.APPROVED, saved.getStatus());
}
```

---

## Checklist Implementation / Danh Sách Kiểm Tra Triển Khai

### Bước 1: Chuẩn Bị / Preparation
- [ ] Đọc và hiểu rõ `TestCaseMatrix_Comprehensive_Design.md`
- [ ] Xác định các file test cần chỉnh sửa
- [ ] Backup các file test hiện tại

### Bước 2: Implement Test Cases / Triển Khai Test Cases
- [ ] Implement test cases cho createPart (TC001-TC010)
- [ ] Implement test cases cho createAllowance (TC031-TC040)
- [ ] Implement test cases cho createDeduction (TC041-TC050)
- [ ] Implement test cases cho createDebt (TC051-TC060)
- [ ] Implement test cases cho payDebt (TC061-TC070)
- [ ] Implement test cases cho getAllDebtsSummary (TC071-TC080)
- [ ] Implement test cases cho createInvoice (TC081-TC090)
- [ ] Implement test cases cho payInvoice (TC091-TC100)
- [ ] Implement test cases cho createServiceTicket (TC101-TC110)
- [ ] Implement test cases cho updateServiceTicket (TC111-TC120)
- [ ] Implement test cases cho createQuotation (TC121-TC130)
- [ ] Implement test cases cho approvePurchaseRequest (TC131-TC140)
- [ ] Implement test cases cho createAppointment (TC141-TC150)
- [ ] Implement test cases cho createTransaction (TC151-TC160)
- [ ] Implement test cases cho handleCallback (TC161-TC170)
- [ ] Implement test cases cho approvePayroll (TC171-TC180)
- [ ] Implement test cases cho createManualVoucher (TC181-TC190)
- [ ] Implement test cases cho approveVoucher (TC191-TC200)

### Bước 3: Kiểm Tra / Verification
- [ ] Chạy tất cả test cases và đảm bảo pass
- [ ] Kiểm tra code coverage đạt >= 80%
- [ ] Review code test để đảm bảo chất lượng
- [ ] Cập nhật documentation nếu cần

### Bước 4: Hoàn Thiện / Completion
- [ ] Tạo pull request với description rõ ràng
- [ ] Yêu cầu code review
- [ ] Merge sau khi được approve

---

## Lưu Ý / Notes

1. **Naming Convention**: Tuân theo convention `methodName_ShouldExpectedBehavior_WhenCondition`
2. **Test Isolation**: Mỗi test case phải độc lập, không phụ thuộc vào test case khác
3. **Mock Data**: Sử dụng mock data hợp lý, không hardcode giá trị không cần thiết
4. **Assertions**: Đảm bảo assertions đầy đủ và chính xác
5. **Exception Handling**: Test cả success và failure scenarios
6. **Code Coverage**: Đảm bảo coverage đạt mục tiêu đề ra

---

## Tài Liệu Tham Khảo / References

- `TestCaseMatrix_Comprehensive_Design.md` - Thiết kế ma trận test case
- JUnit 5 Documentation
- Mockito Documentation
- Spring Boot Testing Guide

