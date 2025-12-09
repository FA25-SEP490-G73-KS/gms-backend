# Test Case Matrix Index / Mục Lục Ma Trận Test Case

## Tổng Quan / Overview

Tài liệu này liệt kê tất cả các ma trận test case đã được tạo cho các function trong dự án GMS Backend.

This document lists all test case matrices created for functions in the GMS Backend project.

---

## 1. Allowance Service / Dịch Vụ Phụ Cấp

### Create Allowance / Tạo Phụ Cấp
- **File**: `TestCaseMatrix_CreateAllowance.md`
- **Test Cases**: 6 test cases
- **Coverage**: Normal flow, invalid type, boundary amount, invalid amount, employee not found

### Delete Allowance / Xóa Phụ Cấp
- **File**: `TestCaseMatrix_DeleteAllowance.md`
- **Test Cases**: 5 test cases
- **Coverage**: Normal flow, not found, invalid ID, payroll impact

---

## 2. Deduction Service / Dịch Vụ Khấu Trừ

### Create Deduction / Tạo Khấu Trừ
- **File**: `TestCaseMatrix_CreateDeduction.md`
- **Test Cases**: 7 test cases
- **Coverage**: Normal flow, invalid type/content, boundary amount, employee/creator not found

### Delete Deduction / Xóa Khấu Trừ
- **File**: `TestCaseMatrix_DeleteDeduction.md`
- **Test Cases**: 5 test cases
- **Coverage**: Normal flow, not found, invalid ID, payroll impact

---

## 3. Part Service / Dịch Vụ Linh Kiện

### Create Part / Tạo Linh Kiện
- **File**: `TestCaseMatrix_CreatePart.md`
- **Test Cases**: 8 test cases
- **Coverage**: Normal flow, universal part, missing dependencies, invalid price, category not found

### Part Service Additional Functions / Các Chức Năng Bổ Sung
- **File**: `TestCaseMatrix_PartService_Additional.md`
- **Functions Covered**:
  - Get All Parts (5 test cases)
  - Get Part By ID (2 test cases)
  - Update Part (3 test cases)
  - Get Part By Category (1 test case)
- **Coverage**: Filtering, pagination, partial/full update, not found scenarios

---

## 4. Purchase Request Service / Dịch Vụ Yêu Cầu Mua Hàng

### Create Purchase Request From Quotation / Tạo Yêu Cầu Mua Hàng Từ Báo Giá
- **Coverage**: Implemented in service but test cases commented out in test file

### Approve Purchase Request / Phê Duyệt Yêu Cầu Mua Hàng
- **File**: `TestCaseMatrix_ApprovePurchaseRequest.md`
- **Test Cases**: 5 test cases
- **Coverage**: Normal flow, stock receipt creation failed, no items, already approved, not found

### Get Purchase Requests / Lấy Danh Sách Yêu Cầu Mua Hàng
- **File**: `TestCaseMatrix_GetPurchaseRequests.md`
- **Test Cases**: 5 test cases
- **Coverage**: No filters, keyword filters, empty result

### Get Purchase Request Detail / Lấy Chi Tiết Yêu Cầu Mua Hàng
- **File**: `TestCaseMatrix_GetPurchaseRequestDetail.md`
- **Test Cases**: 2 test cases
- **Coverage**: Normal flow, not found

---

## 5. Payroll Service / Dịch Vụ Tính Lương

### Get Payroll Preview / Xem Trước Lương
- **File**: `TestCaseMatrix_GetPayrollPreview.md`
- **Test Cases**: 2 test cases
- **Coverage**: Normal flow with multiple employees, empty employee list

### Approve Payroll / Phê Duyệt Lương
- **File**: `TestCaseMatrix_ApprovePayroll.md`
- **Test Cases**: 2 test cases
- **Coverage**: Normal flow, not found

### Create Salary Payment Voucher / Tạo Phiếu Chi Lương
- **Coverage**: Test exists but matrix not yet created (can be added if needed)

---

## 6. Stock Receipt Service / Dịch Vụ Phiếu Nhập Kho

### Get Receipt Items / Lấy Danh Sách Mặt Hàng Phiếu Nhập Kho
- **File**: `TestCaseMatrix_GetReceiptItems.md`
- **Test Cases**: 2 test cases
- **Coverage**: Normal flow, receipt not found

### Other Functions
- Get Receipts, Get Receipt Detail, Create Receipt From Purchase Request, etc.
- **Coverage**: Test cases exist but matrices not yet created (can be added if needed)

---

## 7. Employee Service / Dịch Vụ Nhân Viên

### Employee Service Functions / Các Chức Năng Employee Service
- **File**: `TestCaseMatrix_EmployeeService.md`
- **Functions Covered**:
  - Find All Employee Is Technicians Active (1 test case)
  - Find Employee Info By Phone (1 test case)
  - Find All Employees (1 test case)
- **Coverage**: Normal flows, status computation

---

## 8. Customer Service / Dịch Vụ Khách Hàng

### Customer Service Functions / Các Chức Năng Customer Service
- **File**: `TestCaseMatrix_CustomerService.md`
- **Functions Covered**:
  - Search Customers By Phone (3 test cases)
  - Get Customer Detail By ID (2 test cases)
  - Get All Customers (1 test case)
  - Get Customer By Phone (2 test cases)
- **Coverage**: Normal flows, not found, null/empty inputs

---

## 9. Debt Service / Dịch Vụ Công Nợ

### Debt Service Functions / Các Chức Năng Debt Service
- **File**: `TestCaseMatrix_DebtService.md`
- **Functions Covered**:
  - Get All Debts Summary (2 test cases)
  - Get Debts By Customer (3 test cases)
  - Create Debt (3 test cases)
  - Pay Debt (5 test cases)
  - Get Debt Detail By Service Ticket ID (2 test cases)
- **Coverage**: Normal flows, not found, filtering, sorting, payment methods, partial/over payment

---

## 10. Invoice Service / Dịch Vụ Hóa Đơn

### Invoice Service Functions / Các Chức Năng Invoice Service
- **File**: `TestCaseMatrix_InvoiceService.md`
- **Functions Covered**:
  - Create Invoice (4 test cases)
  - Get Invoice List (1 test case)
  - Get Invoice Detail (2 test cases)
  - Pay Invoice (2 test cases)
- **Coverage**: Normal flows, not found, calculation (discount + debt), payment

---

## 11. Price Quotation Service / Dịch Vụ Báo Giá

### Price Quotation Service Functions / Các Chức Năng Price Quotation Service
- **File**: `TestCaseMatrix_PriceQuotationService.md`
- **Functions Covered**:
  - Find All Quotations (1 test case)
  - Create Quotation (2 test cases)
  - Recalculate Estimate Amount (2 test cases)
  - Update Quotation Items (2 test cases)
- **Coverage**: Normal flows, not found, service ticket status update, amount recalculation

---

## 12. Service Ticket Service / Dịch Vụ Phiếu Dịch Vụ

### Service Ticket Service Functions / Các Chức Năng Service Ticket Service
- **File**: `TestCaseMatrix_ServiceTicketService.md`
- **Functions Covered**:
  - Create Service Ticket (2 test cases)
- **Coverage**: Normal flows with existing/new customer and vehicle

---

## 13. Warehouse Quotation Service / Dịch Vụ Báo Giá Kho

### Warehouse Quotation Service Functions / Các Chức Năng Warehouse Quotation Service
- **File**: `TestCaseMatrix_WarehouseQuotationService.md`
- **Functions Covered**:
  - Get Pending Quotations (1 test case)
  - Confirm Item During Warehouse Review (3 test cases)
  - Reject Item During Warehouse Review (2 test cases)
  - Create Part During Warehouse Review (3 test cases)
- **Coverage**: Normal flows, filtering, status updates, part creation, not found scenarios

---

## 14. Manual Voucher Service / Dịch Vụ Phiếu Thủ Công

### Manual Voucher Service Functions / Các Chức Năng Manual Voucher Service
- **File**: `TestCaseMatrix_ManualVoucherService.md`
- **Functions Covered**:
  - Approve Voucher (3 test cases)
  - Create Voucher (2 test cases)
- **Coverage**: Normal flows, not found, status validation

---

## 15. Appointment Service / Dịch Vụ Lịch Hẹn

### Appointment Service Functions / Các Chức Năng Appointment Service
- **File**: `TestCaseMatrix_AppointmentService.md`
- **Functions Covered**:
  - Get Time Slots By Date (1 test case)
  - Create Appointment (4 test cases)
- **Coverage**: Normal flows, daily limit validation, time slot validation, new/existing customer

---

## 16. Transaction Service / Dịch Vụ Giao Dịch

### Transaction Service Functions / Các Chức Năng Transaction Service
- **File**: `TestCaseMatrix_TransactionService.md`
- **Functions Covered**:
  - Create Transaction (2 test cases)
  - Handle Callback (3 test cases)
- **Coverage**: Cash/Bank transfer methods, PayOS integration, callback handling, invoice/debt updates

---

## 17. Payroll Service Additional / Payroll Service Bổ Sung

### Payroll Service Additional Functions / Các Chức Năng Bổ Sung Payroll Service
- **File**: `TestCaseMatrix_PayrollService_Additional.md`
- **Functions Covered**:
  - Create Salary Payment Voucher (3 test cases)
  - Submit Payroll (2 test cases)
- **Coverage**: Normal flows, status validation, duplicate prevention

---

## Tổng Kết / Summary

### Đã Tạo / Created
- **Total Matrices**: 22 files
- **Total Test Cases Covered**: ~120+ test cases
- **Services Covered**: 17 main services

### Danh Sách Đầy Đủ Các File Matrix / Complete List of Matrix Files

1. `TestCaseMatrix_CreateAllowance.md`
2. `TestCaseMatrix_DeleteAllowance.md`
3. `TestCaseMatrix_CreateDeduction.md`
4. `TestCaseMatrix_DeleteDeduction.md`
5. `TestCaseMatrix_CreatePart.md`
6. `TestCaseMatrix_PartService_Additional.md`
7. `TestCaseMatrix_ApprovePurchaseRequest.md`
8. `TestCaseMatrix_GetPurchaseRequests.md`
9. `TestCaseMatrix_GetPurchaseRequestDetail.md`
10. `TestCaseMatrix_GetPayrollPreview.md`
11. `TestCaseMatrix_ApprovePayroll.md`
12. `TestCaseMatrix_PayrollService_Additional.md`
13. `TestCaseMatrix_GetReceiptItems.md`
14. `TestCaseMatrix_EmployeeService.md`
15. `TestCaseMatrix_CustomerService.md`
16. `TestCaseMatrix_DebtService.md`
17. `TestCaseMatrix_InvoiceService.md`
18. `TestCaseMatrix_PriceQuotationService.md`
19. `TestCaseMatrix_ServiceTicketService.md`
20. `TestCaseMatrix_WarehouseQuotationService.md`
21. `TestCaseMatrix_ManualVoucherService.md`
22. `TestCaseMatrix_AppointmentService.md`
23. `TestCaseMatrix_TransactionService.md`
24. `TestCaseMatrix_Index.md` (this file)

---

## Hướng Dẫn Sử Dụng / Usage Guide

1. Mỗi file matrix chứa:
   - Ma trận test case với các điều kiện và kết quả mong đợi
   - Mô tả chi tiết từng test case
   - Preconditions, Input Parameters, Expected Outcomes, Results

2. Format chuẩn:
   - Precondition: Điều kiện tiên quyết
   - Input Parameters: Tham số đầu vào
   - Expected Outcome: Kết quả mong đợi
   - Result: Loại test (N/A/B), Passed/Failed

3. Các test case được đánh dấu bằng "O" trong ma trận để chỉ ra điều kiện nào áp dụng.

---

## Ghi Chú / Notes

- Tất cả các matrix đều có cả tiếng Anh và tiếng Việt
- Các matrix được thiết kế dựa trên code test case thực tế trong dự án
- Có thể mở rộng thêm các matrix cho các function khác khi cần

