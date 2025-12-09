# Test Case Matrix: Price Quotation Service Functions / Ma Trận Test Case: Các Chức Năng Price Quotation Service

## Test Case Matrix: Find All Quotations

| Precondition | UTCID01 |
|--------------|---------|
| **Can connect with server** | O |
| **Price Quotations** | | |
| Has quotations | O |
| **Input Parameters** | | |
| **pageable** | | |
| Page 0, size 5 | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (Page<PriceQuotationResponseDto>) | O |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N |
| Passed/Failed | P | P |

---

## Test Case Matrix: Create Quotation

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Service Ticket** | | | |
| Existing {id: 100, status: CREATED} | O | |
| Does not exist {id: 999} | | O |
| **Customer** | | | |
| Has discount policy | O | |
| **Input Parameters** | | | |
| **serviceTicketId** | | | |
| "100" (valid) | O | |
| "999" (not exist) | | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (ServiceTicketResponseDto) | O | |
| **Exception** | | | |
| ResourceNotFoundException | | O |
| **Service Ticket Status** | | | |
| Updated to WAITING_FOR_QUOTATION | O | |
| **Price Quotation** | | | |
| Created and linked to service ticket | O | |
| Code generated (QT-2025-00001) | O | |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |

---

## Test Case Matrix: Recalculate Estimate Amount

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Price Quotation** | | | |
| Existing {id: 1, has items} | O | |
| Does not exist {id: 999} | | O |
| **Items** | | | |
| Has items with totalPrice | O | |
| **Input Parameters** | | | |
| **quotationId** | | | |
| "1" (valid) | O | |
| "999" (not exist) | | O |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (PriceQuotationResponseDto) | O | |
| **Exception** | | | |
| ResourceNotFoundException | | O |
| **Estimate Amount** | | | |
| Recalculated from items totalPrice | O | |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |

---

## Test Case Matrix: Update Quotation Items

| Precondition | UTCID01 | UTCID02 |
|--------------|---------|---------|
| **Can connect with server** | O | O |
| **Price Quotation** | | | |
| Existing {id: 1, has items} | O | |
| Does not exist {id: 999} | | O |
| **Items** | | | |
| Has existing items | O | |
| **Input Parameters** | | | |
| **quotationId** | | | |
| "1" (valid) | O | |
| "999" (not exist) | | O |
| **items** | | | |
| List of items to update | O | |
| **Expected Outcome** | | | |
| **Return** | | | |
| Successfully (PriceQuotationResponseDto) | O | |
| **Exception** | | | |
| ResourceNotFoundException | | O |
| **Items** | | | |
| Updated or created | O | |
| Estimate amount recalculated | O | |
| **Result** | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | A |
| Passed/Failed | P | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Find All Quotations - Normal Flow
**English**: Test getting all price quotations with pagination successfully.

**Tiếng Việt**: Kiểm thử lấy tất cả báo giá với phân trang thành công.

**Preconditions**:
- Can connect with server: Yes
- Price Quotations exist: Yes

**Input**:
- pageable: Page 0, size 5

**Expected**: Successfully return Page<PriceQuotationResponseDto>

**Result**: Normal, Passed

---

### UTCID01: Create Quotation - Normal Flow
**English**: Test creating quotation successfully. Service ticket status updated and quotation linked.

**Tiếng Việt**: Kiểm thử tạo báo giá thành công. Trạng thái phiếu dịch vụ được cập nhật và báo giá được liên kết.

**Preconditions**:
- Can connect with server: Yes
- Service Ticket exists: Yes (id: 100, status: CREATED)
- Customer has discount policy: Yes

**Input**:
- serviceTicketId: "100"

**Expected**: 
- Successfully return ServiceTicketResponseDto
- Service ticket status updated to WAITING_FOR_QUOTATION
- Price quotation created with code "QT-2025-00001"
- Quotation linked to service ticket

**Result**: Normal, Passed

---

### UTCID02: Create Quotation - Service Ticket Not Found
**English**: Test creating quotation when service ticket does not exist should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử tạo báo giá khi phiếu dịch vụ không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Service Ticket exists: No (id: 999)

**Input**:
- serviceTicketId: "999"

**Expected**: ResourceNotFoundException

**Result**: Abnormal, Passed

---

### UTCID01: Recalculate Estimate Amount - Normal Flow
**English**: Test recalculating estimate amount from items' totalPrice successfully.

**Tiếng Việt**: Kiểm thử tính lại số tiền ước tính từ totalPrice của các items thành công.

**Preconditions**:
- Can connect with server: Yes
- Price Quotation exists: Yes (id: 1, has items)
- Items have totalPrice: Yes

**Input**:
- quotationId: "1"

**Expected**: 
- Successfully return PriceQuotationResponseDto
- estimateAmount recalculated as sum of all items' totalPrice

**Result**: Normal, Passed

---

### UTCID01: Update Quotation Items - Normal Flow
**English**: Test updating quotation items successfully. Items updated/created and estimate amount recalculated.

**Tiếng Việt**: Kiểm thử cập nhật items báo giá thành công. Items được cập nhật/tạo và số tiền ước tính được tính lại.

**Preconditions**:
- Can connect with server: Yes
- Price Quotation exists: Yes (id: 1, has items)

**Input**:
- quotationId: "1"
- items: List of items to update

**Expected**: 
- Successfully return PriceQuotationResponseDto
- Items updated or created
- Estimate amount recalculated

**Result**: Normal, Passed

