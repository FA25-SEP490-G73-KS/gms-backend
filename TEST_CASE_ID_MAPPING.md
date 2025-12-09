# Test Case ID Mapping Guide / Hướng Dẫn Mapping Test Case ID

Tài liệu này cung cấp mapping giữa Test Case ID trong Test Case Matrix và các test methods trong code.

This document provides mapping between Test Case IDs in Test Case Matrix and test methods in code.

---

## Format Comment / Định Dạng Comment

Mỗi test method nên có comment theo format:
Each test method should have a comment in the format:

```java
// TCXXX: [Description] - [Type: Normal/Abnormal/Boundary]
@Test
void testMethodName() {
    // Test implementation
}
```

---

## Mapping Table / Bảng Mapping

### PartService Tests

| TC ID | Test Method | Description |
|-------|-------------|-------------|
| TC001 | `createPart_ShouldResolveAllDependenciesAndSave` | Create part with all dependencies |
| TC002 | (Not implemented yet) | Create part with category and vehicleModel |
| TC003 | `createPart_ShouldAllowNullCategoryAndUniversalVehicle` | Create universal part |
| TC004 | (Not implemented yet) | Create part with override sellingPrice |
| TC005 | `createPart_ShouldCreateSuccessfully_WithMinimumPurchasePrice` | Create part with minimum purchasePrice |
| TC006 | `createPart_ShouldThrow_WhenCategoryNotFound` | Category not found |
| TC007 | `createPart_ShouldCreateSuccessfully_WithAllOptionalFields` | Create part with all optional fields |
| TC008 | `createPart_ShouldCreateSuccessfully_WithOnlyRequiredFields` | Create part with only required fields |
| TC009 | `createPart_ShouldCreateSuccessfully_WithMaximumPurchasePrice` | Create part with maximum purchasePrice |
| TC010 | `createPart_ShouldThrow_WhenMarketNotFound` | Market not found |
| TC011 | `updatePart_ShouldUpdateFieldsAndSave` | Update part - Full update |
| TC012-TC019 | (Various updatePart tests) | Update part scenarios |
| TC020 | `updatePart_ShouldThrow_WhenPartNotFound` | Update part - Part not found |
| TC021 | `getAllPart_ShouldReturnPagedDtos` | Get all parts - Normal flow |
| TC022-TC023 | (Various getAllPart tests) | Get all parts scenarios |
| TC024 | `getPartById_ShouldReturnDto_WhenFound` | Get part by ID - Found |
| TC025 | `getPartById_ShouldReturnNullDto_WhenNotFound` | Get part by ID - Not found |
| TC026 | `getPartByCategory_ShouldUseRepositoryAndMapper` | Get parts by category |

### AllowanceService Tests

| TC ID | Test Method | Description |
|-------|-------------|-------------|
| TC031 | `createAllowance_ShouldReturnDto_WhenEmployeeExists` | Create allowance with MEAL type |
| TC032 | `createAllowance_ShouldReturnDto_WhenOVERTIMEType` | Create allowance with OVERTIME type |
| TC033 | `createAllowance_ShouldReturnDto_WhenBONUSType` | Create allowance with BONUS type |
| TC034 | (Not implemented yet) | Create allowance with null type |
| TC035 | (Not implemented yet) | Create allowance with valid amount |
| TC036 | `createAllowance_ShouldReturnDto_WhenAmountIsZero` | Create allowance with boundary amount (0) |
| TC037 | `createAllowance_ShouldHandleMinimumValidAmount` | Create allowance with minimum valid amount |
| TC038 | `createAllowance_ShouldHandleMaximumValidAmount` | Create allowance with maximum valid amount |
| TC039 | `createAllowance_ShouldCreateSuccessfully_WithMinimumAmount` | Create allowance with minimum amount |
| TC040 | `createAllowance_ShouldThrowResourceNotFoundException_WhenEmployeeNotFound` | Employee not found |

### DeductionService Tests

| TC ID | Test Method | Description |
|-------|-------------|-------------|
| TC041 | `createDeduction_ShouldCreateAndReturnDto_WhenPENALTYType` | Create deduction with PENALTY type |
| TC042 | `createDeduction_ShouldCreateAndReturnDto_WhenOTHERType` | Create deduction with OTHER type |
| TC043 | `createDeduction_ShouldCreateAndReturnDto` | Create deduction with DAMAGE type |
| TC044 | (Not implemented yet) | Create deduction with null type |
| TC045 | `createDeduction_ShouldCreateAndReturnDto_WhenAmountIsZero` | Create deduction with boundary amount (0) |
| TC046 | (Not implemented yet) | Create deduction with valid content and amount |
| TC047 | `createDeduction_ShouldHandleMaximumValidAmount` | Create deduction with maximum valid amount |
| TC048 | `createDeduction_ShouldHandleMinimumValidAmount` | Create deduction with minimum valid amount |
| TC049 | `createDeduction_ShouldHandleLongContent` | Create deduction with long content |
| TC050 | `createDeduction_ShouldThrow_WhenEmployeeNotFound` | Employee not found |
| TC051 | `createDeduction_ShouldThrow_WhenCreatorNotFound` | Creator not found |

---

## Additional Test Cases (TC201-TC232)

Các test case từ TC201 đến TC232 là các test case bổ sung cho các scenarios đặc biệt như:
- Concurrency & Race Conditions
- Transaction Rollback
- Null Safety
- External Service Failures
- Data Consistency
- Security
- Boundary Conditions

Xem chi tiết trong `TestCaseMatrix_Additional_Missing.md`

---

## Notes / Ghi Chú

1. Một số test case có thể chưa được implement trong code
2. Một số test method có thể cover nhiều test case
3. Format comment: `// TCXXX: [Description]`
4. Nếu test case chưa được implement, comment nên ghi: `// TCXXX: [Description] - TODO: Not implemented yet`

---

## How to Use / Cách Sử Dụng

1. Tìm test method trong code
2. Thêm comment với format: `// TCXXX: [Description]`
3. Nếu test case chưa có trong code, có thể tạo mới hoặc đánh dấu TODO

---

## References / Tài Liệu Tham Khảo

- `TestCaseMatrix_Comprehensive_Design.md` - Test case matrix cho 200 test cases chính
- `TestCaseMatrix_Additional_Missing.md` - Test case matrix cho 32 test cases bổ sung
- Các file TestCaseMatrix_*.md - Test case matrix cho từng service cụ thể

