# Use Case: Create Deduction / Tạo Khấu Trừ

## Primary Actor / Tác Nhân Chính:
**English**: User with payroll management permissions (Accountant, HR Manager, Admin, or employee with deduction creation rights)

**Tiếng Việt**: Người dùng có quyền quản lý lương (Kế toán, Quản lý nhân sự, Admin, hoặc nhân viên có quyền tạo khấu trừ)

## Secondary Actors / Tác Nhân Phụ:
**English**:
- Employee Management System
- Payroll System

**Tiếng Việt**:
- Hệ thống quản lý nhân viên (Employee Management System)
- Hệ thống tính lương (Payroll System)

## Description / Mô Tả:
**English**: This use case allows authorized users to create a new deduction record for an employee. Deductions are amounts subtracted from an employee's salary (such as penalties for damage, penalties for violations, etc.). The system automatically records the deduction date, reason, and creator information.

**Tiếng Việt**: Use case này cho phép người dùng có quyền tạo một bản ghi khấu trừ mới cho nhân viên. Khấu trừ là các khoản tiền bị trừ từ lương của nhân viên (như phạt bồi thường hỏng hóc, phạt vi phạm, v.v.). Hệ thống tự động ghi lại ngày khấu trừ, lý do và thông tin người tạo.

## Preconditions / Điều Kiện Tiên Quyết:
**English**:
1. User is logged into the system
2. User has permission to create deductions
3. Employee with the provided employeeId must exist in the system
4. The creator (employee who creates the deduction) must exist in the system

**Tiếng Việt**:
1. Người dùng đã đăng nhập vào hệ thống
2. Người dùng có quyền tạo khấu trừ
3. Nhân viên với employeeId được cung cấp phải tồn tại trong hệ thống
4. Người tạo (nhân viên tạo khấu trừ) phải tồn tại trong hệ thống

## Postconditions / Điều Kiện Sau:
**English**:
1. A new deduction record is successfully created in the system with:
   - Auto-generated ID by the system
   - Employee association
   - Deduction type (DAMAGE - Damage compensation, PENALTY - Penalty, OTHER - Other)
   - Reason/content description
   - Amount specified
   - Auto-set date to current date (LocalDate.now())
   - Creator information (createdBy) set to the creator's full name
2. The new deduction information is returned to the user as DeductionDto
3. The deduction will be included in the employee's payroll calculation for the month and year of the deduction date

**Tiếng Việt**:
1. Một bản ghi khấu trừ mới được tạo thành công trong hệ thống với:
   - ID được hệ thống tự động sinh
   - Liên kết với nhân viên
   - Loại khấu trừ (DAMAGE - Bồi thường hỏng hóc, PENALTY - Phạt, OTHER - Khác)
   - Mô tả lý do/nội dung
   - Số tiền được chỉ định
   - Ngày được tự động thiết lập thành ngày hiện tại (LocalDate.now())
   - Thông tin người tạo (createdBy) được đặt bằng tên đầy đủ của người tạo
2. Thông tin khấu trừ mới được trả về cho người dùng dưới dạng DeductionDto
3. Khấu trừ sẽ được bao gồm trong tính toán lương của nhân viên cho tháng và năm của ngày khấu trừ

## Normal Flow / Luồng Chính:
**English**:
1. User submits a request to create a new deduction with the following information:
   - employeeId (employee ID) - required
   - type (deduction type: DAMAGE, PENALTY, OTHER) - required
   - content (reason/description) - required, must not be blank
   - amount (deduction amount) - required, must be >= 0
2. System validates that the Employee with employeeId exists
3. System validates that the Creator (createdBy employee) exists
4. System automatically sets the deduction date to current date (LocalDate.now())
5. System sets createdBy to the creator's full name
6. System creates Deduction entity with all validated and calculated information
7. System saves Deduction to database
8. System returns the newly created Deduction information as DeductionDto with HTTP status 200

**Tiếng Việt**:
1. Người dùng gửi yêu cầu tạo khấu trừ mới với thông tin sau:
   - employeeId (ID nhân viên) - bắt buộc
   - type (loại khấu trừ: DAMAGE - Bồi thường hỏng hóc, PENALTY - Phạt, OTHER - Khác) - bắt buộc
   - content (lý do/mô tả) - bắt buộc, không được để trống
   - amount (số tiền khấu trừ) - bắt buộc, phải >= 0
2. Hệ thống xác thực rằng Nhân viên với employeeId tồn tại
3. Hệ thống xác thực rằng Người tạo (nhân viên createdBy) tồn tại
4. Hệ thống tự động đặt ngày khấu trừ thành ngày hiện tại (LocalDate.now())
5. Hệ thống đặt createdBy bằng tên đầy đủ của người tạo
6. Hệ thống tạo entity Deduction với tất cả thông tin đã được xác thực và tính toán
7. Hệ thống lưu Deduction vào cơ sở dữ liệu
8. Hệ thống trả về thông tin Deduction mới được tạo dưới dạng DeductionDto với HTTP status 200

## Alternative Flows / Luồng Thay Thế:

### 2a. Employee không tồn tại / Employee Not Found
**English**:
- 2a.1. System throws RuntimeException with message "Không tìm thấy nhân viên"
- 2a.2. Use case ends with error, returns HTTP status 404
- 2a.3. No deduction record is created

**Tiếng Việt**:
- 2a.1. Hệ thống ném exception RuntimeException với thông báo "Không tìm thấy nhân viên"
- 2a.2. Use case kết thúc với lỗi, trả về HTTP status 404
- 2a.3. Không có bản ghi khấu trừ nào được tạo

### 3a. Creator không tồn tại / Creator Not Found
**English**:
- 3a.1. System throws RuntimeException with message "Không tìm thấy người tạo"
- 3a.2. Use case ends with error, returns HTTP status 404
- 3a.3. No deduction record is created

**Tiếng Việt**:
- 3a.1. Hệ thống ném exception RuntimeException với thông báo "Không tìm thấy người tạo"
- 3a.2. Use case kết thúc với lỗi, trả về HTTP status 404
- 3a.3. Không có bản ghi khấu trừ nào được tạo

### 1a. Dữ liệu đầu vào không hợp lệ / Invalid Input Data
**English**:
- 1a.1. If employeeId is null, system returns validation error
- 1a.2. If type is null, system returns validation error
- 1a.3. If content is null or blank, system returns validation error with message "Content must not be blank"
- 1a.4. If amount is null or < 0, system returns validation error with message "Amount must be >= 0"
- 1a.5. Use case ends with error, returns HTTP status 400

**Tiếng Việt**:
- 1a.1. Nếu employeeId là null, hệ thống trả về lỗi validation
- 1a.2. Nếu type là null, hệ thống trả về lỗi validation
- 1a.3. Nếu content là null hoặc trống, hệ thống trả về lỗi validation với thông báo "Content must not be blank"
- 1a.4. Nếu amount là null hoặc < 0, hệ thống trả về lỗi validation với thông báo "Amount must be >= 0"
- 1a.5. Use case kết thúc với lỗi, trả về HTTP status 400

## Business Rules / Quy Tắc Nghiệp Vụ:

1. **Automatic Date Assignment / Gán ngày tự động**:
   - **English**: The deduction date is automatically set to the current system date when the deduction is created
   - **Tiếng Việt**: Ngày khấu trừ được tự động thiết lập thành ngày hệ thống hiện tại khi khấu trừ được tạo

2. **Deduction Types / Loại khấu trừ**:
   - **English**: The system supports three types of deductions:
     - DAMAGE: Damage compensation (Bồi thường hỏng hóc)
     - PENALTY: Penalty (Phạt)
     - OTHER: Other types (Khác)
   - **Tiếng Việt**: Hệ thống hỗ trợ ba loại khấu trừ:
     - DAMAGE: Bồi thường hỏng hóc
     - PENALTY: Phạt
     - OTHER: Khác

3. **Reason/Content Requirement / Yêu cầu lý do/nội dung**:
   - **English**: Every deduction must have a reason/content description explaining why the deduction is being made. This field cannot be blank
   - **Tiếng Việt**: Mỗi khấu trừ phải có mô tả lý do/nội dung giải thích tại sao khấu trừ được thực hiện. Trường này không được để trống

4. **Amount Validation / Validation số tiền**:
   - **English**: The deduction amount must be greater than or equal to 0 (non-negative)
   - **Tiếng Việt**: Số tiền khấu trừ phải lớn hơn hoặc bằng 0 (không âm)

5. **Creator Tracking / Theo dõi người tạo**:
   - **English**: The system automatically records who created the deduction by storing the creator's full name in the createdBy field. The creator must exist in the system
   - **Tiếng Việt**: Hệ thống tự động ghi lại ai đã tạo khấu trừ bằng cách lưu tên đầy đủ của người tạo trong trường createdBy. Người tạo phải tồn tại trong hệ thống

6. **Payroll Integration / Tích hợp tính lương**:
   - **English**: Deductions are automatically included in payroll calculations for the month and year of the deduction date. The total deduction for an employee in a month is calculated by summing all deductions for that employee in that month
   - **Tiếng Việt**: Khấu trừ tự động được bao gồm trong tính toán lương cho tháng và năm của ngày khấu trừ. Tổng khấu trừ của một nhân viên trong một tháng được tính bằng cách cộng tất cả các khấu trừ của nhân viên đó trong tháng đó

7. **Date-Based Calculation / Tính toán dựa trên ngày**:
   - **English**: Deductions are associated with a specific date, and the system uses the month and year of that date to determine which payroll period the deduction belongs to
   - **Tiếng Việt**: Khấu trừ được liên kết với một ngày cụ thể, và hệ thống sử dụng tháng và năm của ngày đó để xác định kỳ lương mà khấu trừ thuộc về


