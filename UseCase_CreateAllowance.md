# Use Case: Create Allowance / Tạo Phụ Cấp

## Primary Actor / Tác Nhân Chính:
**English**: User with payroll management permissions (Accountant, HR Manager, Admin, or employee with allowance creation rights)

**Tiếng Việt**: Người dùng có quyền quản lý lương (Kế toán, Quản lý nhân sự, Admin, hoặc nhân viên có quyền tạo phụ cấp)

## Secondary Actors / Tác Nhân Phụ:
**English**:
- Employee Management System
- Payroll System

**Tiếng Việt**:
- Hệ thống quản lý nhân viên (Employee Management System)
- Hệ thống tính lương (Payroll System)

## Description / Mô Tả:
**English**: This use case allows authorized users to create a new allowance record for an employee. Allowances are additional payments given to employees (such as meal allowance, overtime allowance, bonuses, etc.) that are added to their base salary. The system automatically records the creation date, month, year, and creator information.

**Tiếng Việt**: Use case này cho phép người dùng có quyền tạo một bản ghi phụ cấp mới cho nhân viên. Phụ cấp là các khoản thanh toán bổ sung được trao cho nhân viên (như phụ cấp ăn trưa, phụ cấp tăng ca, thưởng, v.v.) được cộng vào lương cơ bản của họ. Hệ thống tự động ghi lại ngày tạo, tháng, năm và thông tin người tạo.

## Preconditions / Điều Kiện Tiên Quyết:
**English**:
1. User is logged into the system
2. User has permission to create allowances
3. Employee with the provided employeeId must exist in the system
4. The creator (accountant) must exist in the system

**Tiếng Việt**:
1. Người dùng đã đăng nhập vào hệ thống
2. Người dùng có quyền tạo phụ cấp
3. Nhân viên với employeeId được cung cấp phải tồn tại trong hệ thống
4. Người tạo (kế toán) phải tồn tại trong hệ thống

## Postconditions / Điều Kiện Sau:
**English**:
1. A new allowance record is successfully created in the system with:
   - Auto-generated ID by the system
   - Employee association
   - Allowance type (MEAL, OVERTIME, BONUS, OTHER)
   - Amount specified
   - Auto-set creation date and time (createdAt)
   - Auto-set month and year based on current date
   - Creator information (createdBy) set to the accountant's full name
2. The new allowance information is returned to the user as AllowanceDto
3. The allowance will be included in the employee's payroll calculation for the specified month and year

**Tiếng Việt**:
1. Một bản ghi phụ cấp mới được tạo thành công trong hệ thống với:
   - ID được hệ thống tự động sinh
   - Liên kết với nhân viên
   - Loại phụ cấp (MEAL - Phụ cấp ăn trưa, OVERTIME - Phụ cấp tăng ca, BONUS - Thưởng, OTHER - Khác)
   - Số tiền được chỉ định
   - Ngày và giờ tạo được tự động thiết lập (createdAt)
   - Tháng và năm được tự động thiết lập dựa trên ngày hiện tại
   - Thông tin người tạo (createdBy) được đặt bằng tên đầy đủ của kế toán
2. Thông tin phụ cấp mới được trả về cho người dùng dưới dạng AllowanceDto
3. Phụ cấp sẽ được bao gồm trong tính toán lương của nhân viên cho tháng và năm được chỉ định

## Normal Flow / Luồng Chính:
**English**:
1. User submits a request to create a new allowance with the following information:
   - employeeId (employee ID) - required
   - type (allowance type: MEAL, OVERTIME, BONUS, OTHER) - required
   - amount (allowance amount) - required, must be >= 0
2. System validates that the Employee with employeeId exists
3. System retrieves the creator (accountant) information from the current user context
4. System automatically sets the creation date and time (createdAt) to current LocalDateTime
5. System automatically sets month to current month (LocalDate.now().getMonthValue())
6. System automatically sets year to current year (LocalDate.now().getYear())
7. System sets createdBy to the accountant's full name
8. System creates Allowance entity with all validated and calculated information
9. System saves Allowance to database
10. System returns the newly created Allowance information as AllowanceDto with HTTP status 200

**Tiếng Việt**:
1. Người dùng gửi yêu cầu tạo phụ cấp mới với thông tin sau:
   - employeeId (ID nhân viên) - bắt buộc
   - type (loại phụ cấp: MEAL - Phụ cấp ăn trưa, OVERTIME - Phụ cấp tăng ca, BONUS - Thưởng, OTHER - Khác) - bắt buộc
   - amount (số tiền phụ cấp) - bắt buộc, phải >= 0
2. Hệ thống xác thực rằng Nhân viên với employeeId tồn tại
3. Hệ thống lấy thông tin người tạo (kế toán) từ ngữ cảnh người dùng hiện tại
4. Hệ thống tự động đặt ngày và giờ tạo (createdAt) thành LocalDateTime hiện tại
5. Hệ thống tự động đặt tháng thành tháng hiện tại (LocalDate.now().getMonthValue())
6. Hệ thống tự động đặt năm thành năm hiện tại (LocalDate.now().getYear())
7. Hệ thống đặt createdBy bằng tên đầy đủ của kế toán
8. Hệ thống tạo entity Allowance với tất cả thông tin đã được xác thực và tính toán
9. Hệ thống lưu Allowance vào cơ sở dữ liệu
10. Hệ thống trả về thông tin Allowance mới được tạo dưới dạng AllowanceDto với HTTP status 200

## Alternative Flows / Luồng Thay Thế:

### 2a. Employee không tồn tại / Employee Not Found
**English**:
- 2a.1. System throws ResourceNotFoundException with message "Không tìm thấy nhân viên"
- 2a.2. Use case ends with error, returns HTTP status 404
- 2a.3. No allowance record is created

**Tiếng Việt**:
- 2a.1. Hệ thống ném exception ResourceNotFoundException với thông báo "Không tìm thấy nhân viên"
- 2a.2. Use case kết thúc với lỗi, trả về HTTP status 404
- 2a.3. Không có bản ghi phụ cấp nào được tạo

### 1a. Dữ liệu đầu vào không hợp lệ / Invalid Input Data
**English**:
- 1a.1. If employeeId is null, system returns validation error
- 1a.2. If type is null, system returns validation error
- 1a.3. If amount is null or < 0, system returns validation error with message "Amount must be >= 0"
- 1a.4. Use case ends with error, returns HTTP status 400

**Tiếng Việt**:
- 1a.1. Nếu employeeId là null, hệ thống trả về lỗi validation
- 1a.2. Nếu type là null, hệ thống trả về lỗi validation
- 1a.3. Nếu amount là null hoặc < 0, hệ thống trả về lỗi validation với thông báo "Amount must be >= 0"
- 1a.4. Use case kết thúc với lỗi, trả về HTTP status 400

## Business Rules / Quy Tắc Nghiệp Vụ:

1. **Automatic Date Assignment / Gán ngày tự động**:
   - **English**: The creation date (createdAt), month, and year are automatically set based on the current system date when the allowance is created
   - **Tiếng Việt**: Ngày tạo (createdAt), tháng và năm được tự động thiết lập dựa trên ngày hệ thống hiện tại khi phụ cấp được tạo

2. **Allowance Types / Loại phụ cấp**:
   - **English**: The system supports four types of allowances:
     - MEAL: Meal allowance (Phụ cấp ăn trưa)
     - OVERTIME: Overtime allowance (Phụ cấp tăng ca)
     - BONUS: Bonus (Thưởng)
     - OTHER: Other types (Khác)
   - **Tiếng Việt**: Hệ thống hỗ trợ bốn loại phụ cấp:
     - MEAL: Phụ cấp ăn trưa
     - OVERTIME: Phụ cấp tăng ca
     - BONUS: Thưởng
     - OTHER: Khác

3. **Amount Validation / Validation số tiền**:
   - **English**: The allowance amount must be greater than or equal to 0 (non-negative)
   - **Tiếng Việt**: Số tiền phụ cấp phải lớn hơn hoặc bằng 0 (không âm)

4. **Creator Tracking / Theo dõi người tạo**:
   - **English**: The system automatically records who created the allowance by storing the creator's full name in the createdBy field
   - **Tiếng Việt**: Hệ thống tự động ghi lại ai đã tạo phụ cấp bằng cách lưu tên đầy đủ của người tạo trong trường createdBy

5. **Payroll Integration / Tích hợp tính lương**:
   - **English**: Allowances are automatically included in payroll calculations for the specified month and year. The total allowance for an employee in a month is calculated by summing all allowances for that employee in that month
   - **Tiếng Việt**: Phụ cấp tự động được bao gồm trong tính toán lương cho tháng và năm được chỉ định. Tổng phụ cấp của một nhân viên trong một tháng được tính bằng cách cộng tất cả các phụ cấp của nhân viên đó trong tháng đó

6. **Month and Year Association / Liên kết tháng và năm**:
   - **English**: Each allowance is associated with a specific month and year, allowing the system to calculate monthly totals for payroll purposes
   - **Tiếng Việt**: Mỗi phụ cấp được liên kết với một tháng và năm cụ thể, cho phép hệ thống tính tổng hàng tháng cho mục đích tính lương


