# TEST IMPLEMENTATION PLAN
## Code lại toàn bộ test theo TEST_CASE_DESIGN_DOCUMENT.md

### PHÂN TÍCH PROJECT
- **Tech Stack:** Spring Boot 3.5.6, Java 21, JUnit 5, Mockito
- **Test Framework:** JUnit 5 + Mockito (spring-boot-starter-test)
- **Source Code:** `src/main/java/fpt/edu/vn/gms/service/impl/`
- **Test Folder:** `src/test/java/fpt/edu/vn/gms/service/impl/`

### TỔNG QUAN TEST CASES
- **Tổng số:** 220 test cases
- **EXISTING:** 14 test cases
- **NEW:** 206 test cases
- **20 Methods** cần test

### KẾ HOẠCH TRIỂN KHAI

#### Phase 1: Critical Methods (P0) - 66 test cases
1. ✅ PriceQuotationServiceImpl.createQuotation (Matrix 1) - 12 test cases
2. ✅ PriceQuotationServiceImpl.updateQuotationItems (Matrix 2) - 15 test cases  
3. ✅ PriceQuotationServiceImpl.confirmQuotationByCustomer (Matrix 3) - 15 test cases
4. ✅ DebtServiceImpl.payDebt (Matrix 4) - 12 test cases (6 existing, 6 new)
5. ✅ ServiceTicketServiceImpl.createServiceTicket (Matrix 5) - 15 test cases
6. ✅ TransactionServiceImpl.createTransaction (Matrix 6) - 12 test cases
7. ✅ StockExportServiceImpl.createExportFromQuotation (Matrix 7) - 12 test cases

#### Phase 2: High Priority Methods (P1) - 60 test cases
8. PurchaseRequestServiceImpl.createPurchaseRequest (Matrix 8) - 12 test cases
9. InvoiceServiceImpl.createInvoice (Matrix 9) - 12 test cases
10. CustomerServiceImpl.updateTotalSpending (Matrix 10) - 10 test cases
11. PartServiceImpl.updateInventory (Matrix 11) - 12 test cases
12. DebtServiceImpl.getDebtsByCustomer (Matrix 12) - 12 test cases (4 existing, 8 new)

#### Phase 3: Remaining Methods - 94 test cases
13. PriceQuotationServiceImpl.rejectQuotationByCustomer (Matrix 13) - 10 test cases
14. PriceQuotationServiceImpl.sendQuotationToCustomer (Matrix 14) - 10 test cases
15. PriceQuotationServiceImpl.updateQuotationToDraft (Matrix 15) - 10 test cases
16. DebtServiceImpl.createDebt (Matrix 16) - 12 test cases (4 existing, 8 new)
17. DebtServiceImpl.updateDueDate (Matrix 17) - 10 test cases
18. ServiceTicketServiceImpl.updateServiceTicket (Matrix 18) - 12 test cases
19. TransactionServiceImpl.processPaymentByPaymentLinkId (Matrix 19) - 12 test cases
20. PurchaseRequestServiceImpl.approvePurchaseRequest (Matrix 20) - 10 test cases

### NGUYÊN TẮC CODE TEST

1. **1 UTCID = 1 test function**
   - Tên function: `methodName_UTCIDxx_Description`
   - Ví dụ: `createQuotation_UTCID01_ShouldCreateQuotationWithDraftStatus_WhenValidTicket`

2. **Structure:**
   ```java
   @Test
   void createQuotation_UTCID01_ShouldCreateQuotationWithDraftStatus_WhenValidTicket() {
       // Given - Setup precondition
       // When - Execute method
       // Then - Assert expected result
   }
   ```

3. **Comment phải ghi rõ UTCID:**
   ```java
   /**
    * UTCID01: Valid ticket exists
    * Precondition: Valid ticket exists
    * Input: ticketId=1L
    * Expected: Creates quotation with DRAFT status, updates ticket to WAITING_FOR_QUOTATION
    * Type: N (Normal)
    */
   ```

4. **Không được:**
   - Tái sử dụng test code cũ
   - Tự sinh thêm test case
   - Gộp nhiều test case vào 1 test

### MAPPING UTCID → TEST FUNCTION

#### Matrix 1: createQuotation (PQ-001)
- UTCID01 → createQuotation_UTCID01_ShouldCreateQuotationWithDraftStatus_WhenValidTicket
- UTCID02 → createQuotation_UTCID02_ShouldThrowException_WhenTicketNotFound
- UTCID03 → createQuotation_UTCID03_ShouldThrowException_WhenTicketIdIsNull
- UTCID04 → createQuotation_UTCID04_ShouldThrowException_WhenTicketAlreadyHasQuotation
- UTCID05 → createQuotation_UTCID05_ShouldSetDiscountFromCustomerPolicy_WhenCustomerHasDiscount
- UTCID06 → createQuotation_UTCID06_ShouldHandleNullDiscountPolicy_WhenCustomerHasNoPolicy
- UTCID07 → createQuotation_UTCID07_ShouldGenerateCode_WhenCodeSequenceSucceeds
- UTCID08 → createQuotation_UTCID08_ShouldThrowException_WhenCodeSequenceFails
- UTCID09 → createQuotation_UTCID09_ShouldThrowException_WhenTicketStatusIsCompleted
- UTCID10 → createQuotation_UTCID10_ShouldThrowException_WhenTicketStatusIsCanceled
- UTCID11 → createQuotation_UTCID11_ShouldThrowException_WhenDatabaseSaveFails
- UTCID12 → createQuotation_UTCID12_ShouldHandleBoundary_WhenTicketIdIsMaxValue

### STATUS
- [ ] Phase 1: Critical Methods (P0) - 66 test cases
- [ ] Phase 2: High Priority Methods (P1) - 60 test cases
- [ ] Phase 3: Remaining Methods - 94 test cases

**TOTAL:** 220 test cases

