# Test Case Matrix: Create Part / Ma Trận Test Case: Tạo Linh Kiện

## Test Case Matrix

| Precondition | UTCID01 | UTCID02 | UTCID03 | UTCID04 | UTCID05 | UTCID06 | UTCID07 | UTCID08 |
|--------------|---------|---------|---------|---------|---------|---------|---------|---------|
| **Can connect with server** | O | O | O | O | O | O | O | O |
| **Market** | | | | | | | | |
| Existing {marketId: 2} | O | O | O | O | O | O | O | O |
| Does not exist {marketId: 999} | | | | | | | | |
| **Unit** | | | | | | | | |
| Existing {unitId: 3} | O | O | O | O | O | O | O | O |
| Does not exist {unitId: 999} | | | | | | | | |
| **Supplier** | | | | | | | | |
| Existing {supplierId: 5} | O | O | O | O | O | O | O | O |
| Does not exist {supplierId: 999} | | | | | | | | |
| **Category** | | | | | | | | |
| Existing {categoryId: 1} | O | O | O | O | O | O | O | |
| Does not exist {categoryId: 999} | | | | | | | | O |
| null (optional) | | | | | | | | |
| **VehicleModel** | | | | | | | | |
| Existing {vehicleModelId: 4} | O | O | | | | | | |
| Does not exist {vehicleModelId: 999} | | | O | | | | | |
| Not required (universal = true) | | | | O | O | O | O | O |
| **Input Parameters** | | | | | | | | |
| **name** | | | | | | | | |
| "Oil 5W30" (valid) | O | O | O | O | O | O | O | O |
| null | | | | | | | | |
| **marketId** | | | | | | | | |
| "2" (valid) | O | O | O | O | O | O | O | O |
| "999" (not exist) | | | | | | | | |
| null | | | | | | | | |
| **unitId** | | | | | | | | |
| "3" (valid) | O | O | O | O | O | O | O | O |
| "999" (not exist) | | | | | | | | |
| null | | | | | | | | |
| **supplierId** | | | | | | | | |
| "5" (valid) | O | O | O | O | O | O | O | O |
| "999" (not exist) | | | | | | | | |
| null | | | | | | | | |
| **categoryId** | | | | | | | | |
| "1" (valid) | O | O | O | O | O | O | O | |
| "999" (not exist) | | | | | | | | O |
| null (optional) | | | | | | | | |
| **vehicleModelId** | | | | | | | | |
| "4" (valid) | O | O | | | | | | |
| "999" (not exist) | | | O | | | | | |
| null (when universal = true) | | | | O | O | O | O | O |
| **universal** | | | | | | | | |
| false | O | O | O | | | | | |
| true | | | | O | O | O | O | O |
| **purchasePrice** | | | | | | | | |
| "100000" (valid, > 0) | O | O | O | O | O | O | O | O |
| "0" (invalid) | | | | | O | | | |
| "-10000" (invalid) | | | | | | O | | |
| null | | | | | | | O | |
| **Expected Outcome** | | | | | | | | |
| **Return** | | | | | | | |
| Successfully (PartReqDto) | O | O | | O | | | | |
| **Exception** | | | | | | | | |
| ResourceNotFoundException ("Không tìm thấy thị trường!") | | | | | | | | |
| ResourceNotFoundException ("Không tìm thấy đơn vị tính!") | | | | | | | | |
| ResourceNotFoundException ("Không tìm thấy nhà cung cấp") | | | | | | | | |
| ResourceNotFoundException ("Không tìm thấy danh mục") | | | | | | | | O |
| ResourceNotFoundException ("Không tìm thấy mẫu xe!") | | | O | | | | | |
| ValidationException | | | | | O | O | O | |
| **Log message** | | | | | | | | |
| Created part id={} name={} | O | O | | O | | | | |
| **Result** | | | | | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | A | N | A | A | A | A |
| Passed/Failed | P | P | P | P | P | P | P | P |
| Executed Date | | | | | | | | |
| Defect ID | | | | | | | | |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Create Part - Normal Flow with All Required Fields
**English**: Test creating a part successfully with all required fields: market, unit, supplier, category, vehicleModel (non-universal), and valid purchase price.

**Tiếng Việt**: Kiểm thử tạo linh kiện thành công với tất cả các trường bắt buộc: thị trường, đơn vị, nhà cung cấp, danh mục, mẫu xe (không phổ thông) và giá nhập hợp lệ.

**Preconditions**:
- Can connect with server: Yes
- Market exists: Yes (marketId: 2)
- Unit exists: Yes (unitId: 3)
- Supplier exists: Yes (supplierId: 5)
- Category exists: Yes (categoryId: 1)
- VehicleModel exists: Yes (vehicleModelId: 4)

**Input**:
- name: "Oil 5W30"
- marketId: "2"
- unitId: "3"
- supplierId: "5"
- categoryId: "1"
- vehicleModelId: "4"
- universal: false
- purchasePrice: "100000"

**Expected**: Successfully return PartReqDto with auto-calculated sellingPrice (110000) and discountRate (10.0)

**Result**: Normal, Passed

---

### UTCID02: Create Part - Normal Flow with Universal Part
**English**: Test creating a universal part (applicable to all vehicle models) successfully.

**Tiếng Việt**: Kiểm thử tạo linh kiện phổ thông (áp dụng cho tất cả mẫu xe) thành công.

**Preconditions**:
- Can connect with server: Yes
- Market exists: Yes (marketId: 2)
- Unit exists: Yes (unitId: 3)
- Supplier exists: Yes (supplierId: 5)
- Category exists: Yes (categoryId: 1)
- VehicleModel: Not required (universal = true)

**Input**:
- name: "Oil 5W30"
- marketId: "2"
- unitId: "3"
- supplierId: "5"
- categoryId: "1"
- vehicleModelId: null
- universal: true
- purchasePrice: "100000"

**Expected**: Successfully return PartReqDto

**Result**: Normal, Passed

---

### UTCID03: Create Part - VehicleModel Not Found (when universal = false)
**English**: Test creating a non-universal part with non-existent vehicleModel should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử tạo linh kiện không phổ thông với vehicleModel không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Market exists: Yes (marketId: 2)
- Unit exists: Yes (unitId: 3)
- Supplier exists: Yes (supplierId: 5)
- Category exists: Yes (categoryId: 1)
- VehicleModel exists: No (vehicleModelId: 999)

**Input**:
- name: "Oil 5W30"
- marketId: "2"
- unitId: "3"
- supplierId: "5"
- categoryId: "1"
- vehicleModelId: "999"
- universal: false
- purchasePrice: "100000"

**Expected**: ResourceNotFoundException ("Không tìm thấy mẫu xe!")

**Result**: Abnormal, Passed

---

### UTCID04: Create Part - Normal Flow without Category (Optional)
**English**: Test creating a part successfully without category (category is optional).

**Tiếng Việt**: Kiểm thử tạo linh kiện thành công không có danh mục (danh mục là tùy chọn).

**Preconditions**:
- Can connect with server: Yes
- Market exists: Yes (marketId: 2)
- Unit exists: Yes (unitId: 3)
- Supplier exists: Yes (supplierId: 5)
- Category: null (optional)
- VehicleModel: Not required (universal = true)

**Input**:
- name: "Oil 5W30"
- marketId: "2"
- unitId: "3"
- supplierId: "5"
- categoryId: null
- vehicleModelId: null
- universal: true
- purchasePrice: "100000"

**Expected**: Successfully return PartReqDto with category = null

**Result**: Normal, Passed

---

### UTCID05: Create Part - Invalid Purchase Price (0)
**English**: Test creating a part with purchasePrice = 0 should throw validation exception.

**Tiếng Việt**: Kiểm thử tạo linh kiện với purchasePrice = 0 sẽ ném exception validation.

**Preconditions**:
- Can connect with server: Yes
- Market exists: Yes (marketId: 2)
- Unit exists: Yes (unitId: 3)
- Supplier exists: Yes (supplierId: 5)
- Category: null
- VehicleModel: Not required (universal = true)

**Input**:
- name: "Oil 5W30"
- marketId: "2"
- unitId: "3"
- supplierId: "5"
- categoryId: null
- vehicleModelId: null
- universal: true
- purchasePrice: "0"

**Expected**: ValidationException ("Giá nhập phải lớn hơn 0")

**Result**: Abnormal, Passed

---

### UTCID06: Create Part - Invalid Purchase Price (negative)
**English**: Test creating a part with negative purchasePrice should throw validation exception.

**Tiếng Việt**: Kiểm thử tạo linh kiện với purchasePrice âm sẽ ném exception validation.

**Preconditions**:
- Can connect with server: Yes
- Market exists: Yes (marketId: 2)
- Unit exists: Yes (unitId: 3)
- Supplier exists: Yes (supplierId: 5)
- Category: null
- VehicleModel: Not required (universal = true)

**Input**:
- name: "Oil 5W30"
- marketId: "2"
- unitId: "3"
- supplierId: "5"
- categoryId: null
- vehicleModelId: null
- universal: true
- purchasePrice: "-10000"

**Expected**: ValidationException ("Giá nhập phải lớn hơn 0")

**Result**: Abnormal, Passed

---

### UTCID07: Create Part - Null Purchase Price
**English**: Test creating a part with null purchasePrice should throw validation exception.

**Tiếng Việt**: Kiểm thử tạo linh kiện với purchasePrice null sẽ ném exception validation.

**Preconditions**:
- Can connect with server: Yes
- Market exists: Yes (marketId: 2)
- Unit exists: Yes (unitId: 3)
- Supplier exists: Yes (supplierId: 5)
- Category: null
- VehicleModel: Not required (universal = true)

**Input**:
- name: "Oil 5W30"
- marketId: "2"
- unitId: "3"
- supplierId: "5"
- categoryId: null
- vehicleModelId: null
- universal: true
- purchasePrice: null

**Expected**: ValidationException

**Result**: Abnormal, Passed

---

### UTCID08: Create Part - Category Not Found
**English**: Test creating a part with non-existent categoryId should throw ResourceNotFoundException.

**Tiếng Việt**: Kiểm thử tạo linh kiện với categoryId không tồn tại sẽ ném ResourceNotFoundException.

**Preconditions**:
- Can connect with server: Yes
- Market exists: Yes (marketId: 2)
- Unit exists: Yes (unitId: 3)
- Supplier exists: Yes (supplierId: 5)
- Category exists: No (categoryId: 999)
- VehicleModel: Not required (universal = true)

**Input**:
- name: "Oil 5W30"
- marketId: "2"
- unitId: "3"
- supplierId: "5"
- categoryId: "999"
- vehicleModelId: null
- universal: true
- purchasePrice: "100000"

**Expected**: ResourceNotFoundException ("Không tìm thấy danh mục với ID: 999")

**Result**: Abnormal, Passed

