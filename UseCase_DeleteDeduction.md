# Use Case: Delete Deduction / Xóa Khấu Trừ

## Primary Actor / Tác Nhân Chính:
**English**: User with payroll management permissions (Accountant, HR Manager, Admin, or employee with deduction deletion rights)

**Tiếng Việt**: Người dùng có quyền quản lý lương (Kế toán, Quản lý nhân sự, Admin, hoặc nhân viên có quyền xóa khấu trừ)

## Secondary Actors / Tác Nhân Phụ:
**English**:
- Employee Management System
- Payroll System

**Tiếng Việt**:
- Hệ thống quản lý nhân viên (Employee Management System)
- Hệ thống tính lương (Payroll System)

## Description / Mô Tả:
**English**: This use case allows authorized users to delete an existing deduction record from the system. When a deduction is deleted, it will no longer be included in payroll calculations. This is typically used to correct errors or remove deductions that were created incorrectly.

**Tiếng Việt**: Use case này cho phép người dùng có quyền xóa một bản ghi khấu trừ hiện có khỏi hệ thống. Khi một khấu trừ bị xóa, nó sẽ không còn được bao gồm trong tính toán lương. Điều này thường được sử dụng để sửa lỗi hoặc xóa các khấu trừ đã được tạo không chính xác.

## Preconditions / Điều Kiện Tiên Quyết:
**English**:
1. User is logged into the system
2. User has permission to delete deductions
3. Deduction with the provided deductionId must exist in the system

**Tiếng Việt**:
1. Người dùng đã đăng nhập vào hệ thống
2. Người dùng có quyền xóa khấu trừ
3. Khấu trừ với deductionId được cung cấp phải tồn tại trong hệ thống

## Postconditions / Điều Kiện Sau:
**English**:
1. The deduction record with the specified ID is permanently deleted from the system
2. The deduction is no longer associated with the employee
3. The deduction will no longer be included in payroll calculations for the associated month and year
4. The system returns a success message confirming the deletion
5. If payroll has already been calculated for the month/year, it may need to be recalculated

**Tiếng Việt**:
1. Bản ghi khấu trừ với ID được chỉ định bị xóa vĩnh viễn khỏi hệ thống
2. Khấu trừ không còn liên kết với nhân viên
3. Khấu trừ sẽ không còn được bao gồm trong tính toán lương cho tháng và năm liên quan
4. Hệ thống trả về thông báo thành công xác nhận việc xóa
5. Nếu lương đã được tính cho tháng/năm đó, có thể cần tính lại

## Normal Flow / Luồng Chính:
**English**:
1. User submits a request to delete a deduction by providing the deductionId
2. System validates that the Deduction with deductionId exists in the system
3. System retrieves the deduction record from the database
4. System deletes the deduction record from the database
5. System returns a success message confirming the deletion with HTTP status 200

**Tiếng Việt**:
1. Người dùng gửi yêu cầu xóa khấu trừ bằng cách cung cấp deductionId
2. Hệ thống xác thực rằng Khấu trừ với deductionId tồn tại trong hệ thống
3. Hệ thống lấy bản ghi khấu trừ từ cơ sở dữ liệu
4. Hệ thống xóa bản ghi khấu trừ khỏi cơ sở dữ liệu
5. Hệ thống trả về thông báo thành công xác nhận việc xóa với HTTP status 200

## Alternative Flows / Luồng Thay Thế:

### 2a. Deduction không tồn tại / Deduction Not Found
**English**:
- 2a.1. System throws ResourceNotFoundException with message "Deduction not found with ID: {deductionId}"
- 2a.2. Use case ends with error, returns HTTP status 404
- 2a.3. No deletion is performed

**Tiếng Việt**:
- 2a.1. Hệ thống ném exception ResourceNotFoundException với thông báo "Không tìm thấy khấu trừ với ID: {deductionId}"
- 2a.2. Use case kết thúc với lỗi, trả về HTTP status 404
- 2a.3. Không có thao tác xóa nào được thực hiện

### 1a. ID không hợp lệ / Invalid ID
**English**:
- 1a.1. If deductionId is null or invalid format, system returns validation error
- 1a.2. Use case ends with error, returns HTTP status 400

**Tiếng Việt**:
- 1a.1. Nếu deductionId là null hoặc định dạng không hợp lệ, hệ thống trả về lỗi validation
- 1a.2. Use case kết thúc với lỗi, trả về HTTP status 400

### 4a. Payroll đã được tính / Payroll Already Calculated
**English**:
- 4a.1. If payroll for the associated month/year has already been finalized, system may show a warning
- 4a.2. System proceeds with deletion but may require payroll recalculation
- 4a.3. System may log the deletion for audit purposes

**Tiếng Việt**:
- 4a.1. Nếu lương cho tháng/năm liên quan đã được hoàn tất, hệ thống có thể hiển thị cảnh báo
- 4a.2. Hệ thống tiếp tục xóa nhưng có thể yêu cầu tính lại lương
- 4a.3. Hệ thống có thể ghi log việc xóa cho mục đích kiểm toán

## Business Rules / Quy Tắc Nghiệp Vụ:

1. **Permanent Deletion / Xóa vĩnh viễn**:
   - **English**: Deletion of a deduction is permanent and cannot be undone. The record is completely removed from the database
   - **Tiếng Việt**: Việc xóa khấu trừ là vĩnh viễn và không thể hoàn tác. Bản ghi được xóa hoàn toàn khỏi cơ sở dữ liệu

2. **Payroll Impact / Tác động đến lương**:
   - **English**: When a deduction is deleted, it immediately affects payroll calculations. If payroll has already been calculated for the month/year, it should be recalculated to reflect the correct total deduction. This may result in a higher net salary for the employee
   - **Tiếng Việt**: Khi một khấu trừ bị xóa, nó ngay lập tức ảnh hưởng đến tính toán lương. Nếu lương đã được tính cho tháng/năm đó, nó nên được tính lại để phản ánh tổng khấu trừ chính xác. Điều này có thể dẫn đến lương thực lãnh cao hơn cho nhân viên

3. **Authorization / Phân quyền**:
   - **English**: Only users with appropriate permissions can delete deductions. This is typically restricted to accountants, HR managers, or administrators
   - **Tiếng Việt**: Chỉ người dùng có quyền phù hợp mới có thể xóa khấu trừ. Điều này thường bị hạn chế đối với kế toán, quản lý nhân sự hoặc quản trị viên

4. **Audit Trail / Dấu vết kiểm toán**:
   - **English**: The system should maintain an audit trail of deleted deductions for compliance and tracking purposes. This is important for financial audits and payroll reconciliation
   - **Tiếng Việt**: Hệ thống nên duy trì dấu vết kiểm toán của các khấu trừ đã xóa cho mục đích tuân thủ và theo dõi. Điều này quan trọng cho kiểm toán tài chính và đối chiếu lương

5. **Cascade Considerations / Xem xét cascade**:
   - **English**: If the deduction is referenced in finalized payroll records, the system should handle the deletion appropriately, potentially requiring payroll recalculation. The deletion should not break referential integrity
   - **Tiếng Việt**: Nếu khấu trừ được tham chiếu trong các bản ghi lương đã hoàn tất, hệ thống nên xử lý việc xóa một cách phù hợp, có thể yêu cầu tính lại lương. Việc xóa không nên phá vỡ tính toàn vẹn tham chiếu

6. **Validation Before Deletion / Xác thực trước khi xóa**:
   - **English**: The system must verify that the deduction exists before attempting deletion to provide clear error messages
   - **Tiếng Việt**: Hệ thống phải xác minh rằng khấu trừ tồn tại trước khi cố gắng xóa để cung cấp thông báo lỗi rõ ràng

7. **Financial Impact / Tác động tài chính**:
   - **English**: Deleting a deduction increases the employee's net salary. The system should ensure that this change is properly reflected in all related financial records
   - **Tiếng Việt**: Xóa một khấu trừ làm tăng lương thực lãnh của nhân viên. Hệ thống nên đảm bảo rằng thay đổi này được phản ánh đúng trong tất cả các bản ghi tài chính liên quan


