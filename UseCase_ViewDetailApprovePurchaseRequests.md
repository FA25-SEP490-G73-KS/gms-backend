# Use Case: View Detail Approve Purchase Requests / Xem Chi Tiết Phê Duyệt Yêu Cầu Mua Hàng

## Primary Actor / Tác Nhân Chính:
**English**: Manager or authorized user with purchase request approval permissions (Manager, Admin, or employee with approval rights)

**Tiếng Việt**: Quản lý hoặc người dùng có quyền phê duyệt yêu cầu mua hàng (Manager, Admin, hoặc nhân viên có quyền phê duyệt)

## Secondary Actors / Tác Nhân Phụ:
**English**:
- Purchase Request Management System
- Stock Receipt System
- Inventory Management System
- Price Quotation System

**Tiếng Việt**:
- Hệ thống quản lý yêu cầu mua hàng (Purchase Request Management System)
- Hệ thống phiếu nhập kho (Stock Receipt System)
- Hệ thống quản lý kho (Inventory Management System)
- Hệ thống báo giá (Price Quotation System)

## Description / Mô Tả:
**English**: This use case allows authorized managers to view detailed information of a purchase request and approve it. When a purchase request is approved, the system automatically creates a stock receipt for receiving the purchased items. The use case involves two main steps: first viewing the purchase request details (including all items, quantities, estimated prices, and related quotation information), and then approving the request which triggers the creation of a stock receipt.

**Tiếng Việt**: Use case này cho phép quản lý có quyền xem thông tin chi tiết của một yêu cầu mua hàng và phê duyệt nó. Khi một yêu cầu mua hàng được phê duyệt, hệ thống tự động tạo phiếu nhập kho để nhận các mặt hàng đã mua. Use case bao gồm hai bước chính: đầu tiên xem chi tiết yêu cầu mua hàng (bao gồm tất cả các mặt hàng, số lượng, giá ước tính và thông tin báo giá liên quan), sau đó phê duyệt yêu cầu để kích hoạt việc tạo phiếu nhập kho.

## Preconditions / Điều Kiện Tiên Quyết:
**English**:
1. User is logged into the system
2. User has permission to view and approve purchase requests
3. Purchase Request with the provided purchaseRequestId must exist in the system
4. Purchase Request must have at least one item (PurchaseRequestItem)
5. Purchase Request status should be PENDING (Chờ duyệt) for approval to be meaningful

**Tiếng Việt**:
1. Người dùng đã đăng nhập vào hệ thống
2. Người dùng có quyền xem và phê duyệt yêu cầu mua hàng
3. Yêu cầu mua hàng với purchaseRequestId được cung cấp phải tồn tại trong hệ thống
4. Yêu cầu mua hàng phải có ít nhất một mặt hàng (PurchaseRequestItem)
5. Trạng thái yêu cầu mua hàng nên là PENDING (Chờ duyệt) để việc phê duyệt có ý nghĩa

## Postconditions / Điều Kiện Sau:
**English**:
1. User has viewed the detailed information of the purchase request including:
   - Purchase request code, reason, and status
   - Related quotation code and customer information
   - Creator information and creation date
   - List of all items with details (SKU, part name, quantity, unit, estimated price)
2. If approved:
   - Purchase Request status is updated to APPROVED (Đã duyệt)
   - A new Stock Receipt is automatically created from the approved purchase request
   - The stock receipt is ready for receiving the purchased items
   - The purchase request detail information is returned to the user

**Tiếng Việt**:
1. Người dùng đã xem thông tin chi tiết của yêu cầu mua hàng bao gồm:
   - Mã yêu cầu mua hàng, lý do và trạng thái
   - Mã báo giá liên quan và thông tin khách hàng
   - Thông tin người tạo và ngày tạo
   - Danh sách tất cả các mặt hàng với chi tiết (SKU, tên linh kiện, số lượng, đơn vị, giá ước tính)
2. Nếu được phê duyệt:
   - Trạng thái yêu cầu mua hàng được cập nhật thành APPROVED (Đã duyệt)
   - Một phiếu nhập kho mới được tự động tạo từ yêu cầu mua hàng đã phê duyệt
   - Phiếu nhập kho sẵn sàng để nhận các mặt hàng đã mua
   - Thông tin chi tiết yêu cầu mua hàng được trả về cho người dùng

## Normal Flow / Luồng Chính:

### Phase 1: View Purchase Request Detail / Giai Đoạn 1: Xem Chi Tiết Yêu Cầu Mua Hàng

**English**:
1. User requests to view purchase request details by providing the purchaseRequestId
2. System validates that the Purchase Request with purchaseRequestId exists
3. System retrieves the Purchase Request entity from the database
4. System retrieves all related PurchaseRequestItems
5. System retrieves related information:
   - Related PriceQuotation and its code
   - Customer information from ServiceTicket
   - Creator information
6. System maps the entity to PurchaseRequestDetailDto including:
   - Purchase request ID, code, reason
   - Quotation code
   - Customer name
   - Creator name (createdBy)
   - Formatted creation date (dd/MM/yyyy HH:mm)
   - Formatted review status (Chờ duyệt / Đã duyệt / Từ chối)
   - List of items with details (SKU, part name, quantity, unit, estimated purchase price, total)
7. System returns PurchaseRequestDetailDto to the user with HTTP status 200

**Tiếng Việt**:
1. Người dùng yêu cầu xem chi tiết yêu cầu mua hàng bằng cách cung cấp purchaseRequestId
2. Hệ thống xác thực rằng Yêu cầu mua hàng với purchaseRequestId tồn tại
3. Hệ thống lấy entity Purchase Request từ cơ sở dữ liệu
4. Hệ thống lấy tất cả các PurchaseRequestItem liên quan
5. Hệ thống lấy thông tin liên quan:
   - PriceQuotation liên quan và mã của nó
   - Thông tin khách hàng từ ServiceTicket
   - Thông tin người tạo
6. Hệ thống ánh xạ entity sang PurchaseRequestDetailDto bao gồm:
   - ID yêu cầu mua hàng, mã, lý do
   - Mã báo giá
   - Tên khách hàng
   - Tên người tạo (createdBy)
   - Ngày tạo đã định dạng (dd/MM/yyyy HH:mm)
   - Trạng thái duyệt đã định dạng (Chờ duyệt / Đã duyệt / Từ chối)
   - Danh sách các mặt hàng với chi tiết (SKU, tên linh kiện, số lượng, đơn vị, giá mua ước tính, tổng)
7. Hệ thống trả về PurchaseRequestDetailDto cho người dùng với HTTP status 200

### Phase 2: Approve Purchase Request / Giai Đoạn 2: Phê Duyệt Yêu Cầu Mua Hàng

**English**:
8. User reviews the purchase request details and decides to approve
9. User submits approval request by providing the purchaseRequestId
10. System validates that the Purchase Request with purchaseRequestId exists
11. System validates that the Purchase Request has at least one item (items list is not null or empty)
12. System updates the Purchase Request reviewStatus to APPROVED
13. System saves the updated Purchase Request to database
14. System automatically creates a Stock Receipt from the approved Purchase Request by calling StockReceiptService.createReceiptFromPurchaseRequest()
15. System logs the creation of stock receipt
16. System returns the updated Purchase Request information with HTTP status 200

**Tiếng Việt**:
8. Người dùng xem xét chi tiết yêu cầu mua hàng và quyết định phê duyệt
9. Người dùng gửi yêu cầu phê duyệt bằng cách cung cấp purchaseRequestId
10. Hệ thống xác thực rằng Yêu cầu mua hàng với purchaseRequestId tồn tại
11. Hệ thống xác thực rằng Yêu cầu mua hàng có ít nhất một mặt hàng (danh sách items không null hoặc rỗng)
12. Hệ thống cập nhật reviewStatus của Yêu cầu mua hàng thành APPROVED
13. Hệ thống lưu Yêu cầu mua hàng đã cập nhật vào cơ sở dữ liệu
14. Hệ thống tự động tạo Phiếu nhập kho từ Yêu cầu mua hàng đã phê duyệt bằng cách gọi StockReceiptService.createReceiptFromPurchaseRequest()
15. Hệ thống ghi log việc tạo phiếu nhập kho
16. Hệ thống trả về thông tin Yêu cầu mua hàng đã cập nhật với HTTP status 200

## Alternative Flows / Luồng Thay Thế:

### 2a. Purchase Request không tồn tại / Purchase Request Not Found (View Detail)
**English**:
- 2a.1. System throws ResourceNotFoundException with message "Không tìm thấy yêu cầu mua hàng"
- 2a.2. Use case ends with error, returns HTTP status 404
- 2a.3. No detail information is returned

**Tiếng Việt**:
- 2a.1. Hệ thống ném exception ResourceNotFoundException với thông báo "Không tìm thấy yêu cầu mua hàng"
- 2a.2. Use case kết thúc với lỗi, trả về HTTP status 404
- 2a.3. Không có thông tin chi tiết nào được trả về

### 10a. Purchase Request không tồn tại / Purchase Request Not Found (Approve)
**English**:
- 10a.1. System throws ResourceNotFoundException with message "Không tìm thấy yêu cầu mua hàng"
- 10a.2. Use case ends with error, returns HTTP status 404
- 10a.3. No approval is performed

**Tiếng Việt**:
- 10a.1. Hệ thống ném exception ResourceNotFoundException với thông báo "Không tìm thấy yêu cầu mua hàng"
- 10a.2. Use case kết thúc với lỗi, trả về HTTP status 404
- 10a.3. Không có thao tác phê duyệt nào được thực hiện

### 11a. Purchase Request không có items / Purchase Request Has No Items
**English**:
- 11a.1. System throws RuntimeException with message "Phiếu yêu cầu mua hàng không có item"
- 11a.2. Use case ends with error, returns HTTP status 400
- 11a.3. No approval is performed
- 11a.4. Purchase Request status remains unchanged

**Tiếng Việt**:
- 11a.1. Hệ thống ném exception RuntimeException với thông báo "Phiếu yêu cầu mua hàng không có item"
- 11a.2. Use case kết thúc với lỗi, trả về HTTP status 400
- 11a.3. Không có thao tác phê duyệt nào được thực hiện
- 11a.4. Trạng thái Yêu cầu mua hàng không thay đổi

### 1a. ID không hợp lệ / Invalid ID
**English**:
- 1a.1. If purchaseRequestId is null or invalid format, system returns validation error
- 1a.2. Use case ends with error, returns HTTP status 400

**Tiếng Việt**:
- 1a.1. Nếu purchaseRequestId là null hoặc định dạng không hợp lệ, hệ thống trả về lỗi validation
- 1a.2. Use case kết thúc với lỗi, trả về HTTP status 400

### 8a. User quyết định không phê duyệt / User Decides Not to Approve
**English**:
- 8a.1. User reviews the purchase request details
- 8a.2. User decides not to approve (may reject later or leave as pending)
- 8a.3. Use case ends without approval
- 8a.4. Purchase Request status remains PENDING

**Tiếng Việt**:
- 8a.1. Người dùng xem xét chi tiết yêu cầu mua hàng
- 8a.2. Người dùng quyết định không phê duyệt (có thể từ chối sau hoặc để ở trạng thái chờ duyệt)
- 8a.3. Use case kết thúc mà không phê duyệt
- 8a.4. Trạng thái Yêu cầu mua hàng vẫn là PENDING

### 14a. Lỗi khi tạo Stock Receipt / Error Creating Stock Receipt
**English**:
- 14a.1. If StockReceiptService.createReceiptFromPurchaseRequest() fails, system may throw an exception
- 14a.2. The approval may still be saved, but stock receipt creation fails
- 14a.3. System logs the error
- 14a.4. User may need to manually create stock receipt or retry

**Tiếng Việt**:
- 14a.1. Nếu StockReceiptService.createReceiptFromPurchaseRequest() thất bại, hệ thống có thể ném exception
- 14a.2. Việc phê duyệt vẫn có thể được lưu, nhưng việc tạo phiếu nhập kho thất bại
- 14a.3. Hệ thống ghi log lỗi
- 14a.4. Người dùng có thể cần tạo phiếu nhập kho thủ công hoặc thử lại

## Business Rules / Quy Tắc Nghiệp Vụ:

1. **Approval Requirement / Yêu cầu phê duyệt**:
   - **English**: A purchase request must have at least one item before it can be approved. Empty purchase requests cannot be approved
   - **Tiếng Việt**: Một yêu cầu mua hàng phải có ít nhất một mặt hàng trước khi có thể được phê duyệt. Yêu cầu mua hàng trống không thể được phê duyệt

2. **Automatic Stock Receipt Creation / Tự động tạo phiếu nhập kho**:
   - **English**: When a purchase request is approved, the system automatically creates a stock receipt. This receipt is ready for receiving the purchased items into inventory
   - **Tiếng Việt**: Khi một yêu cầu mua hàng được phê duyệt, hệ thống tự động tạo một phiếu nhập kho. Phiếu này sẵn sàng để nhận các mặt hàng đã mua vào kho

3. **Status Management / Quản lý trạng thái**:
   - **English**: Purchase requests have three possible review statuses:
     - PENDING: Awaiting approval (Chờ duyệt)
     - APPROVED: Approved by manager (Đã duyệt)
     - REJECTED: Rejected by manager (Từ chối)
   - **Tiếng Việt**: Yêu cầu mua hàng có ba trạng thái duyệt có thể:
     - PENDING: Đang chờ phê duyệt (Chờ duyệt)
     - APPROVED: Đã được quản lý phê duyệt (Đã duyệt)
     - REJECTED: Đã bị quản lý từ chối (Từ chối)

4. **Transaction Integrity / Tính toàn vẹn giao dịch**:
   - **English**: The approval process is performed within a transaction to ensure data consistency. If any step fails, the entire operation is rolled back
   - **Tiếng Việt**: Quá trình phê duyệt được thực hiện trong một giao dịch để đảm bảo tính nhất quán dữ liệu. Nếu bất kỳ bước nào thất bại, toàn bộ thao tác sẽ được rollback

5. **Detail Information Display / Hiển thị thông tin chi tiết**:
   - **English**: The detail view includes comprehensive information:
     - Purchase request basic information (code, reason, status)
     - Related quotation and customer information
     - Creator and creation date
     - Complete list of items with SKU, part name, quantity, unit, estimated price, and calculated total
   - **Tiếng Việt**: Chế độ xem chi tiết bao gồm thông tin toàn diện:
     - Thông tin cơ bản của yêu cầu mua hàng (mã, lý do, trạng thái)
     - Thông tin báo giá và khách hàng liên quan
     - Người tạo và ngày tạo
     - Danh sách đầy đủ các mặt hàng với SKU, tên linh kiện, số lượng, đơn vị, giá ước tính và tổng đã tính

6. **Authorization / Phân quyền**:
   - **English**: Only users with appropriate permissions (typically managers or administrators) can approve purchase requests
   - **Tiếng Việt**: Chỉ người dùng có quyền phù hợp (thường là quản lý hoặc quản trị viên) mới có thể phê duyệt yêu cầu mua hàng

7. **Date Formatting / Định dạng ngày**:
   - **English**: Creation dates are formatted as "dd/MM/yyyy HH:mm" for display purposes
   - **Tiếng Việt**: Ngày tạo được định dạng là "dd/MM/yyyy HH:mm" cho mục đích hiển thị

8. **Price Calculation / Tính toán giá**:
   - **English**: For each item, the total is calculated as: estimatedPurchasePrice × quantity. The total is displayed in the item list
   - **Tiếng Việt**: Đối với mỗi mặt hàng, tổng được tính là: estimatedPurchasePrice × quantity. Tổng được hiển thị trong danh sách mặt hàng

9. **Stock Receipt Relationship / Mối quan hệ với phiếu nhập kho**:
   - **English**: Once approved, a purchase request is linked to a stock receipt. The stock receipt contains the items from the purchase request and is used to track the actual receipt of goods
   - **Tiếng Việt**: Sau khi được phê duyệt, một yêu cầu mua hàng được liên kết với một phiếu nhập kho. Phiếu nhập kho chứa các mặt hàng từ yêu cầu mua hàng và được sử dụng để theo dõi việc nhận hàng thực tế


