# Use Case: Delete Allowance / Xóa Phụ Cấp

## Primary Actor / Tác Nhân Chính:
**English**: User with payroll management permissions (Accountant, HR Manager, Admin, or employee with allowance deletion rights)

**Tiếng Việt**: Người dùng có quyền quản lý lương (Kế toán, Quản lý nhân sự, Admin, hoặc nhân viên có quyền xóa phụ cấp)

## Secondary Actors / Tác Nhân Phụ:
**English**:
- Employee Management System
- Payroll System

**Tiếng Việt**:
- Hệ thống quản lý nhân viên (Employee Management System)
- Hệ thống tính lương (Payroll System)

## Description / Mô Tả:
**English**: This use case allows authorized users to delete an existing allowance record from the system. When an allowance is deleted, it will no longer be included in payroll calculations. This is typically used to correct errors or remove allowances that were created incorrectly.

**Tiếng Việt**: Use case này cho phép người dùng có quyền xóa một bản ghi phụ cấp hiện có khỏi hệ thống. Khi một phụ cấp bị xóa, nó sẽ không còn được bao gồm trong tính toán lương. Điều này thường được sử dụng để sửa lỗi hoặc xóa các phụ cấp đã được tạo không chính xác.

## Preconditions / Điều Kiện Tiên Quyết:
**English**:
1. User is logged into the system
2. User has permission to delete allowances
3. Allowance with the provided allowanceId must exist in the system

**Tiếng Việt**:
1. Người dùng đã đăng nhập vào hệ thống
2. Người dùng có quyền xóa phụ cấp
3. Phụ cấp với allowanceId được cung cấp phải tồn tại trong hệ thống

## Postconditions / Điều Kiện Sau:
**English**:
1. The allowance record with the specified ID is permanently deleted from the system
2. The allowance is no longer associated with the employee
3. The allowance will no longer be included in payroll calculations for the associated month and year
4. The system returns a success message confirming the deletion
5. If payroll has already been calculated for the month/year, it may need to be recalculated

**Tiếng Việt**:
1. Bản ghi phụ cấp với ID được chỉ định bị xóa vĩnh viễn khỏi hệ thống
2. Phụ cấp không còn liên kết với nhân viên
3. Phụ cấp sẽ không còn được bao gồm trong tính toán lương cho tháng và năm liên quan
4. Hệ thống trả về thông báo thành công xác nhận việc xóa
5. Nếu lương đã được tính cho tháng/năm đó, có thể cần tính lại

## Normal Flow / Luồng Chính:
**English**:
1. User submits a request to delete an allowance by providing the allowanceId
2. System validates that the Allowance with allowanceId exists in the system
3. System retrieves the allowance record from the database
4. System deletes the allowance record from the database
5. System returns a success message confirming the deletion with HTTP status 200

**Tiếng Việt**:
1. Người dùng gửi yêu cầu xóa phụ cấp bằng cách cung cấp allowanceId
2. Hệ thống xác thực rằng Phụ cấp với allowanceId tồn tại trong hệ thống
3. Hệ thống lấy bản ghi phụ cấp từ cơ sở dữ liệu
4. Hệ thống xóa bản ghi phụ cấp khỏi cơ sở dữ liệu
5. Hệ thống trả về thông báo thành công xác nhận việc xóa với HTTP status 200

## Alternative Flows / Luồng Thay Thế:

### 2a. Allowance không tồn tại / Allowance Not Found
**English**:
- 2a.1. System throws ResourceNotFoundException with message "Allowance not found with ID: {allowanceId}"
- 2a.2. Use case ends with error, returns HTTP status 404
- 2a.3. No deletion is performed

**Tiếng Việt**:
- 2a.1. Hệ thống ném exception ResourceNotFoundException với thông báo "Không tìm thấy phụ cấp với ID: {allowanceId}"
- 2a.2. Use case kết thúc với lỗi, trả về HTTP status 404
- 2a.3. Không có thao tác xóa nào được thực hiện

### 1a. ID không hợp lệ / Invalid ID
**English**:
- 1a.1. If allowanceId is null or invalid format, system returns validation error
- 1a.2. Use case ends with error, returns HTTP status 400

**Tiếng Việt**:
- 1a.1. Nếu allowanceId là null hoặc định dạng không hợp lệ, hệ thống trả về lỗi validation
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
   - **English**: Deletion of an allowance is permanent and cannot be undone. The record is completely removed from the database
   - **Tiếng Việt**: Việc xóa phụ cấp là vĩnh viễn và không thể hoàn tác. Bản ghi được xóa hoàn toàn khỏi cơ sở dữ liệu

2. **Payroll Impact / Tác động đến lương**:
   - **English**: When an allowance is deleted, it immediately affects payroll calculations. If payroll has already been calculated for the month/year, it should be recalculated to reflect the correct total allowance
   - **Tiếng Việt**: Khi một phụ cấp bị xóa, nó ngay lập tức ảnh hưởng đến tính toán lương. Nếu lương đã được tính cho tháng/năm đó, nó nên được tính lại để phản ánh tổng phụ cấp chính xác

3. **Authorization / Phân quyền**:
   - **English**: Only users with appropriate permissions can delete allowances. This is typically restricted to accountants, HR managers, or administrators
   - **Tiếng Việt**: Chỉ người dùng có quyền phù hợp mới có thể xóa phụ cấp. Điều này thường bị hạn chế đối với kế toán, quản lý nhân sự hoặc quản trị viên

4. **Audit Trail / Dấu vết kiểm toán**:
   - **English**: The system should maintain an audit trail of deleted allowances for compliance and tracking purposes
   - **Tiếng Việt**: Hệ thống nên duy trì dấu vết kiểm toán của các phụ cấp đã xóa cho mục đích tuân thủ và theo dõi

5. **Cascade Considerations / Xem xét cascade**:
   - **English**: If the allowance is referenced in finalized payroll records, the system should handle the deletion appropriately, potentially requiring payroll recalculation
   - **Tiếng Việt**: Nếu phụ cấp được tham chiếu trong các bản ghi lương đã hoàn tất, hệ thống nên xử lý việc xóa một cách phù hợp, có thể yêu cầu tính lại lương

6. **Validation Before Deletion / Xác thực trước khi xóa**:
   - **English**: The system must verify that the allowance exists before attempting deletion to provide clear error messages
   - **Tiếng Việt**: Hệ thống phải xác minh rằng phụ cấp tồn tại trước khi cố gắng xóa để cung cấp thông báo lỗi rõ ràng


