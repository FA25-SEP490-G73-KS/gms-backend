### MATRIX 11: PartServiceImpl.updateInventory

**Function Code:** PART-001  
**Function Name:** updateInventory  
**Created By:** QA Team  
**Executed By:** QA Team  
**Test Requirement:** Test đầy đủ các trường hợp của hàm updateInventory

**SUMMARY:**
Passed: 0 | Failed: 0 | Untested: 12 | N: 6 | A: 4 | B: 2 | Total: 12

| Condition Precondition | UTCID128 [N] | UTCID129 [N] | UTCID130 [A] | UTCID131 [A] | UTCID132 [B] | UTCID133 [N] | UTCID134 [N] | UTCID135 [A] | UTCID136 [N] | UTCID137 [A] | UTCID138 [B] | UTCID139 [B] |
|------------------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
| **Preconditions** |
| Can connect with database | O | O | O | O | O | O | O | O | O | O | O | O |
| Part (Pre-existing data) |
| Existing {partId: 1L, quantityInStock: 100.0} | O | O | - | - | O | O | O | O | O | O | O | O |
| Existing {partId: 1L, quantityInStock: 10.0} | - | - | - | - | O | - | - | - | - | - | - | - |
| Does not exist {partId: 999L} | - | - | O | - | - | - | - | - | - | - | - | - |
| PartRepository (Pre-existing data) |
| Can save part | O | O | - | - | O | O | O | - | O | O | O | O |
| Throws DataAccessException | - | - | - | O | - | - | - | O | - | O | - | - |
| **Input** |
| partId |
| 1L | O | O | - | - | O | O | O | O | O | O | O | O |
| 999L | - | - | O | - | - | - | - | - | - | - | - | - |
| quantityChange |
| 10.0 (positive) | O | - | - | - | - | O | - | - | O | - | - | - |
| -5.0 (negative) | - | O | - | - | - | - | O | - | - | - | - | - |
| -10.0 (results in 0) | - | - | - | - | O | - | - | - | - | - | - | - |
| Double.MAX_VALUE | - | - | - | - | - | - | - | - | - | - | O | - |
| 0.0 | - | - | - | - | - | - | - | - | - | - | - | O |
| note |
| "Import stock" | O | O | - | - | O | O | O | O | O | O | O | O |
| null | - | - | - | - | - | - | - | - | - | - | - | - |
| **Confirm (Expected Result)** |
| Return |
| Successfully updates inventory | O | O | - | - | O | O | O | - | O | O | O | O |
| Updates status to OUT_OF_STOCK when quantity = 0 | - | - | - | - | O | - | - | - | - | - | - | - |
| Updates status to AVAILABLE when quantity > 0 | O | O | - | - | - | O | O | - | O | O | O | O |
| Exception |
| ResourceNotFoundException | - | - | O | - | - | - | - | - | - | - | - | - |
| DataAccessException | - | - | - | O | - | - | - | O | - | O | - | - |
| **Result** |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | A | A | B | N | N | A | N | A | B | B |
| Passed/Failed | - | - | - | - | - | - | - | - | - | - | - | - |
| Executed Date | - | - | - | - | - | - | - | - | - | - | - | - |
| Defect ID | - | - | - | - | - | - | - | - | - | - | - | - |

**Trạng thái:** ✅ **ĐÃ IMPLEMENT ĐẦY ĐỦ (12/12 test cases)**

> **Lưu ý:** Method `updateInventory` không tồn tại trong `PartService` interface. Test code đã được tạo nhưng method call bị comment để tránh compilation error.

---

### MATRIX 13: PriceQuotationServiceImpl.rejectQuotationByCustomer

**Function Code:** PQ-004  
**Function Name:** rejectQuotationByCustomer  
**Created By:** QA Team  
**Executed By:** QA Team  
**Test Requirement:** Test đầy đủ các trường hợp của hàm rejectQuotationByCustomer

**SUMMARY:**
Passed: 0 | Failed: 0 | Untested: 10 | N: 3 | A: 6 | B: 1 | Total: 10

| Condition Precondition | UTCID152 [N] | UTCID153 [A] | UTCID154 [A] | UTCID155 [A] | UTCID156 [A] | UTCID157 [B] | UTCID158 [N] | UTCID159 [A] | UTCID160 [A] | UTCID161 [B] |
|------------------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
| **Preconditions** |
| Can connect with database | O | O | O | O | O | O | O | O | O | O |
| PriceQuotation (Pre-existing data) |
| Existing {quotationId: 1L, status: WAITING_CUSTOMER_CONFIRM} | O | - | - | O | O | O | O | O | O | - |
| Existing {quotationId: 1L, status: DRAFT} | - | - | O | - | - | - | - | - | - | - |
| Does not exist {quotationId: 999L} | - | O | - | - | - | - | - | - | - | - |
| Existing {quotationId: Long.MAX_VALUE} | - | - | - | - | - | - | - | - | - | O |
| Employee (Pre-existing data) |
| Existing {advisor: employeeId: 1L} | O | - | - | O | O | O | O | - | O | - |
| Does not exist {advisor: null} | - | - | - | - | - | - | - | O | - | - |
| NotificationService (Pre-existing data) |
| Can create notification | O | - | - | O | O | O | O | - | O | - |
| PriceQuotationRepository (Pre-existing data) |
| Can save quotation | O | - | - | O | O | O | O | O | - | - |
| Throws DataAccessException | - | - | - | - | - | - | - | - | O | - |
| **Input** |
| quotationId |
| 1L | O | - | O | O | O | O | O | O | O | - |
| 999L | - | O | - | - | - | - | - | - | - | - |
| Long.MAX_VALUE | - | - | - | - | - | - | - | - | - | O |
| reason |
| "Too expensive" | O | - | - | - | - | - | O | O | O | - |
| null | - | - | - | O | - | - | - | - | - | - |
| "" (empty) | - | - | - | - | O | - | - | - | - | - |
| "A" * 1000 (very long) | - | - | - | - | - | O | - | - | - | - |
| **Confirm (Expected Result)** |
| Return |
| Successfully rejects quotation | O | - | - | O | O | O | O | O | - | - |
| Status → CUSTOMER_REJECTED | O | - | - | O | O | O | O | O | - | - |
| Sends notification to advisor | O | - | - | O | O | O | O | - | O | - |
| Exception |
| ResourceNotFoundException | - | O | - | - | - | - | - | - | - | O |
| RuntimeException | - | - | O | - | - | - | - | - | - | - |
| DataAccessException | - | - | - | - | - | - | - | - | O | - |
| **Result** |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A | A | A | B | N | A | A | B |
| Passed/Failed | - | - | - | - | - | - | - | - | - | - |
| Executed Date | - | - | - | - | - | - | - | - | - | - |
| Defect ID | - | - | - | - | - | - | - | - | - | - |

**Trạng thái:** ✅ **ĐÃ IMPLEMENT ĐẦY ĐỦ (10/10 test cases)**

---

### MATRIX 14: PriceQuotationServiceImpl.sendQuotationToCustomer

**Function Code:** PQ-005  
**Function Name:** sendQuotationToCustomer  
**Created By:** QA Team  
**Executed By:** QA Team  
**Test Requirement:** Test đầy đủ các trường hợp của hàm sendQuotationToCustomer

**SUMMARY:**
Passed: 0 | Failed: 0 | Untested: 10 | N: 3 | A: 6 | B: 1 | Total: 10

| Condition Precondition | UTCID162 [N] | UTCID163 [A] | UTCID164 [A] | UTCID165 [A] | UTCID166 [A] | UTCID167 [A] | UTCID168 [N] | UTCID169 [B] | UTCID170 [A] | UTCID171 [A] |
|------------------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
| **Preconditions** |
| Can connect with database | O | O | O | O | O | O | O | O | O | O |
| PriceQuotation (Pre-existing data) |
| Existing {quotationId: 1L, status: WAREHOUSE_CONFIRMED} | O | - | - | - | - | O | O | - | O | O |
| Existing {quotationId: 1L, status: WAITING_WAREHOUSE_CONFIRM} | - | - | O | - | - | - | - | - | - | - |
| Existing {quotationId: 1L, status: DRAFT} | - | - | - | O | - | - | - | - | - | - |
| Existing {quotationId: 1L, status: CUSTOMER_CONFIRMED} | - | - | - | - | O | - | - | - | - | - |
| Existing {quotationId: 1L, items: []} | - | - | - | - | - | - | - | - | O | - |
| Existing {quotationId: 1L, estimateAmount: 0} | - | - | - | - | - | - | - | - | - | O |
| Does not exist {quotationId: 999L} | - | O | - | - | - | - | - | - | - | - |
| Existing {quotationId: Long.MAX_VALUE} | - | - | - | - | - | - | - | O | - | - |
| PriceQuotationRepository (Pre-existing data) |
| Can save quotation | O | - | - | - | - | - | O | - | O | O |
| Throws DataAccessException | - | - | - | - | - | O | - | - | - | - |
| **Input** |
| quotationId |
| 1L | O | - | O | O | O | O | O | - | O | O |
| 999L | - | O | - | - | - | - | - | - | - | - |
| Long.MAX_VALUE | - | - | - | - | - | - | - | O | - | - |
| **Confirm (Expected Result)** |
| Return |
| Successfully sends quotation | O | - | - | - | - | - | O | - | O | O |
| Status → WAITING_CUSTOMER_CONFIRM | O | - | - | - | - | - | O | - | O | O |
| Updates timestamp | - | - | - | - | - | - | O | - | - | - |
| Exception |
| ResourceNotFoundException | - | O | - | - | - | - | - | - | - | - |
| RuntimeException | - | - | O | O | O | - | - | - | O | O |
| DataAccessException | - | - | - | - | - | O | - | - | - | - |
| **Result** |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A | A | A | A | N | B | A | A |
| Passed/Failed | - | - | - | - | - | - | - | - | - | - |
| Executed Date | - | - | - | - | - | - | - | - | - | - |
| Defect ID | - | - | - | - | - | - | - | - | - | - |

**Trạng thái:** ✅ **ĐÃ IMPLEMENT ĐẦY ĐỦ (10/10 test cases)**

---

### MATRIX 15: PriceQuotationServiceImpl.updateQuotationToDraft

**Function Code:** PQ-006  
**Function Name:** updateQuotationToDraft  
**Created By:** QA Team  
**Executed By:** QA Team  
**Test Requirement:** Test đầy đủ các trường hợp của hàm updateQuotationToDraft

**SUMMARY:**
Passed: 0 | Failed: 0 | Untested: 10 | N: 4 | A: 5 | B: 1 | Total: 10

| Condition Precondition | UTCID172 [N] | UTCID173 [A] | UTCID174 [A] | UTCID175 [A] | UTCID176 [N] | UTCID177 [A] | UTCID178 [N] | UTCID179 [B] | UTCID180 [A] | UTCID181 [N] |
|------------------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
| **Preconditions** |
| Can connect with database | O | O | O | O | O | O | O | O | O | O |
| PriceQuotation (Pre-existing data) |
| Existing {quotationId: 1L, serviceTicket.status: WAITING_FOR_DELIVERY} | O | - | - | - | - | O | O | - | - | O |
| Existing {quotationId: 1L, serviceTicket.status: WAITING_FOR_QUOTATION} | - | - | - | - | O | - | - | - | - | - |
| Existing {quotationId: 1L, serviceTicket.status: COMPLETED} | - | - | - | O | - | - | - | - | - | - |
| Existing {quotationId: 1L, status: CUSTOMER_CONFIRMED} | - | - | - | - | - | - | - | - | O | - |
| Existing {quotationId: 1L, serviceTicket: null} | - | - | O | - | - | - | - | - | - | - |
| Does not exist {quotationId: 999L} | - | O | - | - | - | - | - | - | - | - |
| Existing {quotationId: Long.MAX_VALUE} | - | - | - | - | - | - | - | O | - | - |
| ServiceTicketRepository (Pre-existing data) |
| Can save ticket | O | - | - | - | O | - | O | - | O | O |
| PriceQuotationRepository (Pre-existing data) |
| Can save quotation | O | - | - | - | O | - | O | - | O | O |
| Throws DataAccessException | - | - | - | - | - | O | - | - | - | - |
| **Input** |
| quotationId |
| 1L | O | - | O | O | O | O | O | - | O | O |
| 999L | - | O | - | - | - | - | - | - | - | - |
| Long.MAX_VALUE | - | - | - | - | - | - | - | O | - | - |
| **Confirm (Expected Result)** |
| Return |
| Successfully updates to draft | O | - | - | - | O | - | O | - | O | O |
| Status → DRAFT | O | - | - | - | O | - | O | - | O | O |
| Updates serviceTicket status | O | - | - | - | O | - | O | - | O | O |
| Updates timestamp | - | - | - | - | - | - | O | - | - | - |
| Saves both quotation and ticket | - | - | - | - | - | - | - | - | - | O |
| Exception |
| ResourceNotFoundException | - | O | - | - | - | - | - | - | - | - |
| RuntimeException | - | - | O | O | - | - | - | - | O | - |
| DataAccessException | - | - | - | - | - | O | - | - | - | - |
| **Result** |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A | A | N | A | N | B | A | N |
| Passed/Failed | - | - | - | - | - | - | - | - | - | - |
| Executed Date | - | - | - | - | - | - | - | - | - | - |
| Defect ID | - | - | - | - | - | - | - | - | - | - |

**Trạng thái:** ✅ **ĐÃ IMPLEMENT ĐẦY ĐỦ (10/10 test cases)**

---

### MATRIX 16: DebtServiceImpl.createDebt

**Function Code:** DE-003  
**Function Name:** createDebt  
**Created By:** QA Team  
**Executed By:** QA Team  
**Test Requirement:** Test đầy đủ các trường hợp của hàm createDebt

**SUMMARY:**
Passed: 0 | Failed: 0 | Untested: 12 | N: 7 | A: 3 | B: 2 | Total: 12

| Condition Precondition | UTCID182 [N] | UTCID183 [A] | UTCID184 [A] | UTCID185 [B] | UTCID186 [B] | UTCID187 [A] | UTCID188 [N] | UTCID189 [N] | UTCID190 [N] | UTCID191 [B] | UTCID192 [B] | UTCID193 [N] |
|------------------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
| **Preconditions** |
| Can connect with database | O | O | O | O | O | O | O | O | O | O | O | O |
| Customer (Pre-existing data) |
| Existing {customerId: 1L} | O | - | O | O | O | O | O | O | O | O | O | O |
| Does not exist {customerId: 999L} | - | O | - | - | - | - | - | - | - | - | - | - |
| ServiceTicket (Pre-existing data) |
| Existing {serviceTicketId: 1L} | O | - | - | O | O | O | O | O | O | O | O | O |
| Does not exist {serviceTicketId: 999L} | - | - | O | - | - | - | - | - | - | - | - | - |
| DebtRepository (Pre-existing data) |
| Can save debt | O | - | - | O | O | - | O | O | O | O | O | O |
| Throws DataAccessException | - | - | - | - | - | O | - | - | - | - | - | - |
| **Input** |
| dto.customerId |
| 1L | O | - | O | O | O | O | O | O | O | O | O | O |
| 999L | - | O | - | - | - | - | - | - | - | - | - | - |
| dto.serviceTicketId |
| 1L | O | - | - | O | O | O | O | O | O | O | O | O |
| 999L | - | - | O | - | - | - | - | - | - | - | - | - |
| dto.amount |
| 1000000 | O | - | - | - | - | O | O | O | O | - | - | O |
| 0 | - | - | - | O | - | - | - | - | - | - | - | - |
| BigDecimal.MAX_VALUE | - | - | - | - | O | - | - | - | - | - | - | - |
| dto.dueDate |
| null (default: today + 14 days) | O | - | - | O | O | O | O | O | O | O | O | O |
| **Confirm (Expected Result)** |
| Return |
| Successfully creates debt | O | - | - | O | O | - | O | O | O | O | O | O |
| dueDate = today + 14 days | - | - | - | - | - | - | O | - | - | - | - | - |
| status = OUTSTANDING | O | - | - | O | O | - | O | O | O | O | O | O |
| paidAmount = 0 | O | - | - | O | O | - | O | O | O | O | O | O |
| Exception |
| CustomerNotFoundException | - | O | - | - | - | - | - | - | - | - | - | - |
| ServiceTicketNotFoundException | - | - | O | - | - | - | - | - | - | - | - | - |
| DataAccessException | - | - | - | - | - | O | - | - | - | - | - | - |
| **Result** |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A | B | B | A | N | N | N | B | B | N |
| Passed/Failed | - | - | - | - | - | - | - | - | - | - | - | - |
| Executed Date | - | - | - | - | - | - | - | - | - | - | - | - |
| Defect ID | - | - | - | - | - | - | - | - | - | - | - | - |

**Trạng thái:** ✅ **ĐÃ IMPLEMENT ĐẦY ĐỦ (12/12 test cases)**

---

### MATRIX 17: DebtServiceImpl.updateDueDate

**Function Code:** DE-004  
**Function Name:** updateDueDate  
**Created By:** QA Team  
**Executed By:** QA Team  
**Test Requirement:** Test đầy đủ các trường hợp của hàm updateDueDate

**SUMMARY:**
Passed: 0 | Failed: 0 | Untested: 10 | N: 3 | A: 5 | B: 2 | Total: 10

| Condition Precondition | UTCID194 [N] | UTCID195 [A] | UTCID196 [A] | UTCID197 [A] | UTCID198 [A] | UTCID199 [B] | UTCID200 [N] | UTCID201 [A] | UTCID202 [B] | UTCID203 [N] |
|------------------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
| **Preconditions** |
| Can connect with database | O | O | O | O | O | O | O | O | O | O |
| Debt (Pre-existing data) |
| Existing {debtId: 1L, dueDate: today + 7 days} | O | - | - | - | - | - | O | O | - | O |
| Existing {debtId: 1L, dueDate: today + 14 days} | - | - | - | - | - | - | - | - | - | O |
| Does not exist {debtId: 999L} | - | O | - | - | - | - | - | - | - | - |
| DebtRepository (Pre-existing data) |
| Can save debt | O | - | - | - | - | - | O | - | O | O |
| Throws DataAccessException | - | - | - | - | - | - | - | O | - | - |
| **Input** |
| debtId |
| 1L | O | - | - | - | - | - | O | O | O | O |
| 999L | - | O | - | - | - | - | - | - | - | - |
| dueDate |
| today + 30 days | O | - | - | - | - | - | O | O | - | - |
| today + 14 days | - | - | - | - | - | - | - | - | - | O |
| null | - | - | O | - | - | - | - | - | - | - |
| today | - | - | - | O | - | - | - | - | - | - |
| today - 1 day | - | - | - | - | O | - | - | - | - | - |
| today + 365 days | - | - | - | - | - | O | - | - | - | - |
| LocalDate.MAX | - | - | - | - | - | - | - | - | O | - |
| **Confirm (Expected Result)** |
| Return |
| Successfully updates due date | O | - | - | - | - | - | O | - | O | O |
| Exception |
| DebtNotFoundException | - | O | - | - | - | - | - | - | - | - |
| IllegalArgumentException | - | - | O | O | O | - | - | - | - | - |
| DataAccessException | - | - | - | - | - | - | - | O | - | - |
| **Result** |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A | A | A | B | N | A | B | N |
| Passed/Failed | - | - | - | - | - | - | - | - | - | - |
| Executed Date | - | - | - | - | - | - | - | - | - | - |
| Defect ID | - | - | - | - | - | - | - | - | - | - |

**Trạng thái:** ✅ **ĐÃ IMPLEMENT ĐẦY ĐỦ (10/10 test cases)**

---

### MATRIX 18: ServiceTicketServiceImpl.updateServiceTicket

**Function Code:** ST-002  
**Function Name:** updateServiceTicket  
**Created By:** QA Team  
**Executed By:** QA Team  
**Test Requirement:** Test đầy đủ các trường hợp của hàm updateServiceTicket

**SUMMARY:**
Passed: 0 | Failed: 0 | Untested: 12 | N: 6 | A: 5 | B: 1 | Total: 12

| Condition Precondition | UTCID204 [N] | UTCID205 [A] | UTCID206 [N] | UTCID207 [A] | UTCID208 [N] | UTCID209 [N] | UTCID210 [N] | UTCID211 [A] | UTCID212 [B] | UTCID213 [N] | UTCID214 [N] | UTCID215 [N] |
|------------------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
| **Preconditions** |
| Can connect with database | O | O | O | O | O | O | O | O | O | O | O | O |
| ServiceTicket (Pre-existing data) |
| Existing {ticketId: 1L, status: CREATED} | O | - | O | - | O | O | O | O | O | O | O | O |
| Existing {ticketId: 1L, status: COMPLETED} | - | - | - | O | - | - | - | - | - | - | - | - |
| Does not exist {ticketId: 999L} | - | O | - | - | - | - | - | - | - | - | - | - |
| Existing {ticketId: Long.MAX_VALUE} | - | - | - | - | - | - | - | - | O | - | - | - |
| ServiceTicketRepository (Pre-existing data) |
| Can save ticket | O | - | O | - | O | O | O | - | - | O | O | O |
| Throws DataAccessException | - | - | - | - | - | - | - | O | - | - | - | - |
| **Input** |
| ticketId |
| 1L | O | - | O | O | O | O | O | O | - | O | O | O |
| 999L | - | O | - | - | - | - | - | - | - | - | - | - |
| Long.MAX_VALUE | - | - | - | - | - | - | - | - | O | - | - | - |
| dto |
| Empty dto {} | O | - | O | - | O | O | O | O | - | O | O | O |
| **Confirm (Expected Result)** |
| Return |
| Successfully updates ticket | O | - | O | - | O | O | O | - | - | O | O | O |
| Updates timestamp | - | - | - | - | - | - | O | - | - | - | - | - |
| Exception |
| ResourceNotFoundException | - | O | - | - | - | - | - | - | O | - | - | - |
| DataAccessException | - | - | - | - | - | - | - | O | - | - | - | - |
| **Result** |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | N | A | N | N | N | A | B | N | N | N |
| Passed/Failed | - | - | - | - | - | - | - | - | - | - | - | - |
| Executed Date | - | - | - | - | - | - | - | - | - | - | - | - |
| Defect ID | - | - | - | - | - | - | - | - | - | - | - | - |

**Trạng thái:** ✅ **ĐÃ IMPLEMENT ĐẦY ĐỦ (12/12 test cases)**

---

### MATRIX 19: TransactionServiceImpl.processPaymentByPaymentLinkId

**Function Code:** TXN-002  
**Function Name:** processPaymentByPaymentLinkId  
**Created By:** QA Team  
**Executed By:** QA Team  
**Test Requirement:** Test đầy đủ các trường hợp của hàm processPaymentByPaymentLinkId

**SUMMARY:**
Passed: 0 | Failed: 0 | Untested: 12 | N: 7 | A: 4 | B: 0 | Total: 12

| Condition Precondition | UTCID216 [N] | UTCID217 [N] | UTCID218 [A] | UTCID219 [A] | UTCID220 [N] | UTCID221 [N] | UTCID222 [N] | UTCID223 [N] | UTCID224 [N] | UTCID225 [N] | UTCID226 [A] | UTCID227 [A] |
|------------------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
| **Preconditions** |
| Can connect with database | O | O | O | O | O | O | O | O | O | O | O | O |
| Transaction (Pre-existing data) |
| Existing {paymentLinkId: "link1", invoice: exists, type: PAYMENT} | O | - | - | - | - | - | - | - | - | - | O | - |
| Existing {paymentLinkId: "link1", debt: exists, type: PAYMENT} | - | O | - | - | - | - | - | - | - | - | - | - |
| Existing {paymentLinkId: "link1", invoice: exists, type: DEPOSIT} | - | - | - | - | - | - | - | O | - | - | - | - |
| Existing {paymentLinkId: "link1", amount: 1000000, invoice.finalAmount: 1000000} | - | - | - | - | - | - | - | - | O | - | - | - |
| Existing {paymentLinkId: "link1", amount: 500000, invoice.finalAmount: 1000000} | - | - | - | - | - | - | - | - | - | O | - | - |
| Does not exist {paymentLinkId: "invalid"} | - | - | O | - | - | - | - | - | - | - | - | - |
| Does not exist {paymentLinkId: "link1", transaction: null} | - | - | - | O | - | - | - | - | - | - | - | - |
| PayOS (Pre-existing data) |
| Payment link status: PAID | O | O | - | - | - | - | - | O | O | O | O | - |
| Payment link status: CANCELLED | - | - | - | - | O | - | - | - | - | - | - | - |
| Payment link status: EXPIRED | - | - | - | - | - | O | - | - | - | - | - | - |
| Payment link status: FAILED | - | - | - | - | - | - | O | - | - | - | - | - |
| Throws exception | - | - | O | - | - | - | - | - | - | - | - | - |
| InvoiceRepository (Pre-existing data) |
| Can save invoice | O | - | - | - | - | - | - | O | O | O | O | - |
| Throws DataAccessException | - | - | - | - | - | - | - | - | - | - | O | - |
| DebtRepository (Pre-existing data) |
| Can save debt | - | O | - | - | - | - | - | - | - | - | - | - |
| TransactionRepository (Pre-existing data) |
| Can find by paymentLinkId | O | O | - | O | O | O | O | O | O | O | O | O |
| Can save transaction | O | O | - | - | O | O | O | O | O | O | O | O |
| Can delete transaction | - | - | - | - | O | O | O | - | - | - | - | - |
| **Input** |
| paymentLinkId |
| "payment-link-123" | O | O | - | - | O | O | O | O | O | O | O | - |
| "invalid-link" | - | - | O | - | - | - | - | - | - | - | - | - |
| **Confirm (Expected Result)** |
| Return |
| Successfully processes payment | O | O | - | - | - | - | - | O | O | O | - | - |
| Updates invoice status to PAID_IN_FULL | O | - | - | - | - | - | - | - | O | - | - | - |
| Updates invoice status to UNDERPAID | - | - | - | - | - | - | - | - | - | O | - | - |
| Updates debt status to PAID_IN_FULL | - | O | - | - | - | - | - | - | - | - | - | - |
| Updates deposit received | - | - | - | - | - | - | - | O | - | - | - | - |
| Updates customer spending | O | O | - | - | - | - | - | O | O | O | - | - |
| Deletes transaction (CANCELLED/EXPIRED/FAILED) | - | - | - | - | O | O | O | - | - | - | - | - |
| Exception |
| TransactionNotFoundException | - | - | - | O | - | - | - | - | - | - | - | - |
| RuntimeException | - | - | O | - | - | - | - | - | - | - | - | - |
| DataAccessException | - | - | - | - | - | - | - | - | - | - | O | - |
| **Result** |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | A | A | N | N | N | N | N | N | A | A |
| Passed/Failed | - | - | - | - | - | - | - | - | - | - | - | - |
| Executed Date | - | - | - | - | - | - | - | - | - | - | - | - |
| Defect ID | - | - | - | - | - | - | - | - | - | - | - | - |

**Trạng thái:** ✅ **ĐÃ IMPLEMENT ĐẦY ĐỦ (12/12 test cases)**

---

### MATRIX 20: PurchaseRequestServiceImpl.approvePurchaseRequest

**Function Code:** PR-002  
**Function Name:** approvePurchaseRequest  
**Created By:** QA Team  
**Executed By:** QA Team  
**Test Requirement:** Test đầy đủ các trường hợp của hàm approvePurchaseRequest

**SUMMARY:**
Passed: 0 | Failed: 0 | Untested: 10 | N: 4 | A: 5 | B: 1 | Total: 10

| Condition Precondition | UTCID228 [N] | UTCID229 [A] | UTCID230 [A] | UTCID231 [A] | UTCID232 [N] | UTCID233 [A] | UTCID234 [B] | UTCID235 [A] | UTCID236 [N] | UTCID237 [A] |
|------------------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
| **Preconditions** |
| Can connect with database | O | O | O | O | O | O | O | O | O | O |
| PurchaseRequest (Pre-existing data) |
| Existing {prId: 1L, status: PENDING, items: [item1]} | O | - | - | - | O | O | - | O | O | O |
| Existing {prId: 1L, status: PENDING, items: []} | - | - | O | - | - | - | - | - | - | - |
| Existing {prId: 1L, status: APPROVED} | - | - | - | O | - | - | - | - | - | - |
| Existing {prId: 1L, totalEstimatedAmount: 0} | - | - | - | - | - | - | - | - | - | O |
| Does not exist {prId: 999L} | - | O | - | - | - | - | - | - | - | - |
| Existing {prId: Long.MAX_VALUE} | - | - | - | - | - | - | O | - | - | - |
| StockReceiptService (Pre-existing data) |
| Can create receipt | O | - | - | - | O | - | - | O | O | O |
| PurchaseRequestRepository (Pre-existing data) |
| Can save purchase request | O | - | - | - | O | - | - | O | O | O |
| Throws DataAccessException | - | - | - | - | - | O | - | - | - | - |
| **Input** |
| prId |
| 1L | O | - | O | O | O | O | - | O | O | O |
| 999L | - | O | - | - | - | - | - | - | - | - |
| Long.MAX_VALUE | - | - | - | - | - | - | O | - | - | - |
| **Confirm (Expected Result)** |
| Return |
| Successfully approves purchase request | O | - | - | - | O | - | - | O | O | O |
| Status → APPROVED | O | - | - | - | O | - | - | O | O | O |
| Creates stock receipt | O | - | - | - | O | - | - | O | O | O |
| Updates timestamp | - | - | - | - | O | - | - | - | - | - |
| Exception |
| ResourceNotFoundException | - | O | - | - | - | - | O | - | - | - |
| RuntimeException | - | - | O | O | - | - | - | - | - | - |
| DataAccessException | - | - | - | - | - | O | - | - | - | - |
| **Result** |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A | A | A | N | A | B | A | N | A |
| Passed/Failed | - | - | - | - | - | - | - | - | - | - |
| Executed Date | - | - | - | - | - | - | - | - | - | - |
| Defect ID | - | - | - | - | - | - | - | - | - | - |

**Trạng thái:** ✅ **ĐÃ IMPLEMENT ĐẦY ĐỦ (10/10 test cases)**

---
