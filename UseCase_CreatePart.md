# Use Case: Create Part / Tạo Linh Kiện

## Primary Actor / Tác Nhân Chính:
**English**: User with part management permissions (Warehouse Manager, Admin, or employee with part creation rights)

**Tiếng Việt**: Người dùng có quyền quản lý linh kiện (Warehouse Manager, Admin, hoặc nhân viên có quyền tạo linh kiện)

## Secondary Actors / Tác Nhân Phụ:
**English**:
- Category Management System
- Supplier Management System
- Unit Management System
- Market Management System
- Vehicle Model Management System
- SKU Generator

**Tiếng Việt**:
- Hệ thống quản lý danh mục (Category Management System)
- Hệ thống quản lý nhà cung cấp (Supplier Management System)
- Hệ thống quản lý đơn vị tính (Unit Management System)
- Hệ thống quản lý thị trường (Market Management System)
- Hệ thống quản lý mẫu xe (Vehicle Model Management System)
- Hệ thống sinh mã SKU (SKU Generator)

## Description / Mô Tả:
**English**: This use case allows users to create a new part in the warehouse management system. The system will automatically calculate the selling price, discount rate, and generate a SKU code for the new part. Parts can be universal (applicable to multiple vehicle models) or specific to a particular vehicle model.

**Tiếng Việt**: Use case này cho phép người dùng tạo một linh kiện (Part) mới trong hệ thống quản lý kho. Hệ thống sẽ tự động tính toán giá bán, tỷ lệ chiết khấu, và sinh mã SKU cho linh kiện mới. Linh kiện có thể là linh kiện phổ thông (universal) hoặc linh kiện dành riêng cho một mẫu xe cụ thể.

## Preconditions / Điều Kiện Tiên Quyết:
**English**:
1. User is logged into the system
2. User has permission to create parts
3. The following entities must exist in the system:
   - Market with the provided ID
   - Unit with the provided ID
   - Supplier with the provided ID
4. If the part is not universal (universal = false), VehicleModel with the provided ID must exist
5. If categoryId is provided, Category with that ID must exist

**Tiếng Việt**:
1. Người dùng đã đăng nhập vào hệ thống
2. Người dùng có quyền tạo linh kiện
3. Các thực thể sau phải tồn tại trong hệ thống:
   - Market (Thị trường) với ID được cung cấp
   - Unit (Đơn vị tính) với ID được cung cấp
   - Supplier (Nhà cung cấp) với ID được cung cấp
4. Nếu linh kiện không phải là universal (universal = false), VehicleModel (Mẫu xe) với ID được cung cấp phải tồn tại
5. Nếu categoryId được cung cấp, Category (Danh mục) với ID đó phải tồn tại

## Postconditions / Điều Kiện Sau:
**English**:
1. A new part is successfully created in the system with:
   - Auto-generated ID by the system
   - Auto-generated SKU by SKU Generator
   - Auto-calculated selling price = purchase price * 1.10
   - Default discount rate set to 10.0%
   - Initial stock status (if applicable) will be calculated based on quantityInStock and reorderLevel
2. The new part information is returned to the user as PartReqDto

**Tiếng Việt**:
1. Một linh kiện mới được tạo thành công trong hệ thống với:
   - ID được hệ thống tự động sinh
   - SKU được tự động sinh bởi SKU Generator
   - Giá bán được tự động tính = giá nhập * 1.10
   - Tỷ lệ chiết khấu được đặt mặc định = 10.0%
   - Trạng thái tồn kho ban đầu (nếu có) sẽ được tính toán dựa trên quantityInStock và reorderLevel
2. Thông tin linh kiện mới được trả về cho người dùng dưới dạng PartReqDto

## Normal Flow / Luồng Chính:
**English**:
1. User submits a request to create a new part with the following information:
   - name (part name) - required
   - marketId (market ID) - required
   - unitId (unit ID) - required
   - supplierId (supplier ID) - required
   - purchasePrice (purchase price) - required, must be > 0
   - categoryId (category ID) - optional
   - vehicleModelId (vehicle model ID) - required if universal = false
   - universal (universal part) - default false
   - specialPart (special part) - default false
   - reorderLevel (minimum stock level) - optional
   - note (notes) - optional
2. System validates Market with the provided marketId
3. System validates Unit with the provided unitId
4. System validates Supplier with the provided supplierId
5. If categoryId is provided, system validates Category with categoryId
6. If universal = false, system validates VehicleModel with the provided vehicleModelId
7. System automatically calculates selling price: sellingPrice = purchasePrice * 1.10
8. System sets discountRate = 10.0
9. System automatically generates SKU code using SKU Generator
10. System creates Part object with validated and calculated information
11. System saves Part to database
12. System returns the newly created Part information as PartReqDto with HTTP status 201

**Tiếng Việt**:
1. Người dùng gửi yêu cầu tạo linh kiện mới với thông tin:
   - name (tên linh kiện) - bắt buộc
   - marketId (ID thị trường) - bắt buộc
   - unitId (ID đơn vị tính) - bắt buộc
   - supplierId (ID nhà cung cấp) - bắt buộc
   - purchasePrice (giá nhập) - bắt buộc, phải > 0
   - categoryId (ID danh mục) - tùy chọn
   - vehicleModelId (ID mẫu xe) - bắt buộc nếu universal = false
   - universal (linh kiện phổ thông) - mặc định false
   - specialPart (linh kiện đặc biệt) - mặc định false
   - reorderLevel (mức tồn kho tối thiểu) - tùy chọn
   - note (ghi chú) - tùy chọn
2. Hệ thống kiểm tra và xác thực Market với marketId được cung cấp
3. Hệ thống kiểm tra và xác thực Unit với unitId được cung cấp
4. Hệ thống kiểm tra và xác thực Supplier với supplierId được cung cấp
5. Nếu categoryId được cung cấp, hệ thống kiểm tra và xác thực Category với categoryId
6. Nếu universal = false, hệ thống kiểm tra và xác thực VehicleModel với vehicleModelId được cung cấp
7. Hệ thống tính toán giá bán tự động: sellingPrice = purchasePrice * 1.10
8. Hệ thống đặt discountRate = 10.0
9. Hệ thống sinh mã SKU tự động bằng SKU Generator
10. Hệ thống tạo đối tượng Part với các thông tin đã được xác thực và tính toán
11. Hệ thống lưu Part vào cơ sở dữ liệu
12. Hệ thống trả về thông tin Part mới được tạo dưới dạng PartReqDto với HTTP status 201

## Alternative Flows / Luồng Thay Thế:

### 3a. Market không tồn tại / Market Not Found
**English**:
- 3a.1. System throws ResourceNotFoundException with message "Market not found!"
- 3a.2. Use case ends with error, returns HTTP status 404

**Tiếng Việt**:
- 3a.1. Hệ thống ném exception ResourceNotFoundException với thông báo "Không tìm thấy thị trường!"
- 3a.2. Use case kết thúc với lỗi, trả về HTTP status 404

### 4a. Unit không tồn tại / Unit Not Found
**English**:
- 4a.1. System throws ResourceNotFoundException with message "Unit not found!"
- 4a.2. Use case ends with error, returns HTTP status 404

**Tiếng Việt**:
- 4a.1. Hệ thống ném exception ResourceNotFoundException với thông báo "Không tìm thấy đơn vị tính!"
- 4a.2. Use case kết thúc với lỗi, trả về HTTP status 404

### 5a. Supplier không tồn tại / Supplier Not Found
**English**:
- 5a.1. System throws ResourceNotFoundException with message "Supplier not found: {supplierId}"
- 5a.2. Use case ends with error, returns HTTP status 404

**Tiếng Việt**:
- 5a.1. Hệ thống ném exception ResourceNotFoundException với thông báo "Không tìm thấy nhà cung cấp {supplierId}"
- 5a.2. Use case kết thúc với lỗi, trả về HTTP status 404

### 5b. Category không tồn tại / Category Not Found (when categoryId is provided)
**English**:
- 5b.1. System throws ResourceNotFoundException with message "Category not found with ID: {categoryId}"
- 5b.2. Use case ends with error, returns HTTP status 404

**Tiếng Việt**:
- 5b.1. Hệ thống ném exception ResourceNotFoundException với thông báo "Không tìm thấy danh mục với ID: {categoryId}"
- 5b.2. Use case kết thúc với lỗi, trả về HTTP status 404

### 6a. VehicleModel không tồn tại / VehicleModel Not Found (when universal = false)
**English**:
- 6a.1. System throws ResourceNotFoundException with message "Vehicle model not found!"
- 6a.2. Use case ends with error, returns HTTP status 404

**Tiếng Việt**:
- 6a.1. Hệ thống ném exception ResourceNotFoundException với thông báo "Không tìm thấy mẫu xe!"
- 6a.2. Use case kết thúc với lỗi, trả về HTTP status 404

### 1a. Dữ liệu đầu vào không hợp lệ / Invalid Input Data
**English**:
- 1a.1. If purchasePrice <= 0, system returns validation error with message "Purchase price must be greater than 0"
- 1a.2. If sellingPrice is provided and <= 0, system returns validation error with message "Selling price must be greater than 0"
- 1a.3. Use case ends with error, returns HTTP status 400

**Tiếng Việt**:
- 1a.1. Nếu purchasePrice <= 0, hệ thống trả về lỗi validation với thông báo "Giá nhập phải lớn hơn 0"
- 1a.2. Nếu sellingPrice được cung cấp và <= 0, hệ thống trả về lỗi validation với thông báo "Giá bán phải lớn hơn 0"
- 1a.3. Use case kết thúc với lỗi, trả về HTTP status 400

### 1b. Linh kiện là universal / Part is Universal (universal = true)
**English**:
- 1b.1. Skip step 6 (no need to validate VehicleModel)
- 1b.2. vehicleModelId can be null or not provided
- 1b.3. Continue with step 7

**Tiếng Việt**:
- 1b.1. Bỏ qua bước 6 (không cần kiểm tra VehicleModel)
- 1b.2. vehicleModelId có thể là null hoặc không được cung cấp
- 1b.3. Tiếp tục với bước 7

### 1c. Category không được cung cấp / Category Not Provided (categoryId = null)
**English**:
- 1c.1. Skip step 5 (no need to validate Category)
- 1c.2. Part is created with category = null
- 1c.3. Continue with step 6 or 7

**Tiếng Việt**:
- 1c.1. Bỏ qua bước 5 (không cần kiểm tra Category)
- 1c.2. Part được tạo với category = null
- 1c.3. Tiếp tục với bước 6 hoặc 7

## Business Rules / Quy Tắc Nghiệp Vụ:

1. **Automatic Selling Price Calculation / Tính toán giá bán tự động**:
   - **English**: The selling price (sellingPrice) is always automatically calculated from the purchase price (purchasePrice) using the formula: sellingPrice = purchasePrice * 1.10 (10% markup)
   - **Tiếng Việt**: Giá bán (sellingPrice) luôn được tính tự động từ giá nhập (purchasePrice) với công thức: sellingPrice = purchasePrice * 1.10 (tăng 10%)

2. **Default Discount Rate / Tỷ lệ chiết khấu mặc định**:
   - **English**: The discount rate (discountRate) is always set to a default value of 10.0% when creating a new part
   - **Tiếng Việt**: Tỷ lệ chiết khấu (discountRate) luôn được đặt mặc định là 10.0% khi tạo linh kiện mới

3. **Automatic SKU Generation / Sinh mã SKU tự động**:
   - **English**: The SKU code is automatically generated by SKU Generator based on the part information
   - **Tiếng Việt**: Mã SKU được tự động sinh bởi SKU Generator dựa trên thông tin của linh kiện

4. **Universal Parts / Linh kiện phổ thông (Universal)**:
   - **English**: 
     - If universal = true, the part can be used for multiple different vehicle models
     - vehicleModelId is not required when universal = true
     - If universal = false, vehicleModelId is required and must exist in the system
   - **Tiếng Việt**: 
     - Nếu universal = true, linh kiện có thể dùng cho nhiều mẫu xe khác nhau
     - vehicleModelId không bắt buộc khi universal = true
     - Nếu universal = false, vehicleModelId là bắt buộc và phải tồn tại trong hệ thống

5. **Special Parts / Linh kiện đặc biệt (Special Part)**:
   - **English**: The specialPart flag allows marking a part as special, which can be used for other business purposes
   - **Tiếng Việt**: Cờ specialPart cho phép đánh dấu linh kiện là đặc biệt, có thể được sử dụng cho các mục đích nghiệp vụ khác

6. **Optional Category / Danh mục tùy chọn**:
   - **English**: Category is optional; parts can be created without being assigned to a specific category
   - **Tiếng Việt**: Category (danh mục) là tùy chọn, linh kiện có thể được tạo mà không cần gán vào danh mục cụ thể

7. **Minimum Stock Level / Mức tồn kho tối thiểu**:
   - **English**: reorderLevel is optional and is used to alert when stock quantity is low
   - **Tiếng Việt**: reorderLevel là tùy chọn, được sử dụng để cảnh báo khi số lượng tồn kho thấp

8. **Price Validation / Validation giá**:
   - **English**: purchasePrice must be greater than 0 (if provided)
   - **Tiếng Việt**: purchasePrice phải lớn hơn 0 (nếu được cung cấp)

9. **Transaction / Giao dịch**:
   - **English**: The entire part creation process is performed within a transaction, ensuring data consistency
   - **Tiếng Việt**: Toàn bộ quá trình tạo linh kiện được thực hiện trong một giao dịch, đảm bảo tính nhất quán dữ liệu

