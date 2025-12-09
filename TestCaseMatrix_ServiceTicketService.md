# Test Case Matrix: Service Ticket Service Functions / Ma Trận Test Case: Các Chức Năng Service Ticket Service

## Test Case Matrix: Create Service Ticket

| Precondition | UTCID01 | UTCID02 | UTCID03 |
|--------------|---------|---------|---------|
| **Can connect with server** | O | O | O |
| **Customer** | | | |
| Existing {customerId: 1} | O | | |
| Does not exist (customerId: null) | | O | |
| **Vehicle** | | | |
| Existing {vehicleId: 1} | O | | |
| Does not exist (licensePlate: "30A-12345") | | O | |
| **Brand** | | | |
| Existing {brandId: 1} | O | O | |
| **Vehicle Model** | | | |
| Existing {modelId: 1} | O | O | |
| **Discount Policy** | | | |
| Default BRONZE policy exists | | O | |
| **Input Parameters** | | | | |
| **customer.customerId** | | | | |
| "1" (valid) | O | | |
| null (new customer) | | O | |
| **vehicle.vehicleId** | | | | |
| "1" (valid) | O | | |
| null (new vehicle) | | O | |
| **customer.fullName** | | | | |
| "Customer" | O | | |
| "New Customer" | | O | |
| **vehicle.licensePlate** | | | | |
| "30A-12345" | O | O | |
| **Expected Outcome** | | | | |
| **Return** | | | | |
| Successfully (ServiceTicketResponseDto) | O | O | |
| **Exception** | | | | |
| | | | |
| **Service Ticket** | | | | |
| Created with code (STK-2025-00001) | O | O | |
| Status: CREATED | O | O | |
| **Customer** | | | | |
| Updated (address) | O | | |
| Created (new customer) | | O | |
| **Vehicle** | | | | |
| Linked to ticket | O | | |
| Created (new vehicle) | | O | |
| **Result** | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | |
| Passed/Failed | P | P | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Create Service Ticket - Normal Flow with Existing Customer and Vehicle
**English**: Test creating service ticket successfully with existing customer and vehicle.

**Tiếng Việt**: Kiểm thử tạo phiếu dịch vụ thành công với khách hàng và xe hiện có.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: Yes (customerId: 1)
- Vehicle exists: Yes (vehicleId: 1)
- Brand exists: Yes (brandId: 1)
- Vehicle Model exists: Yes (modelId: 1)

**Input**:
- customer: {customerId: 1, address: "New Address"}
- vehicle: {vehicleId: 1, licensePlate: "30A-12345", brandId: 1, modelId: 1}

**Expected**: 
- Successfully return ServiceTicketResponseDto
- Service ticket created with code "STK-2025-00001"
- Status: CREATED
- Customer address updated
- Vehicle linked to ticket

**Result**: Normal, Passed

---

### UTCID02: Create Service Ticket - Normal Flow with New Customer and Vehicle
**English**: Test creating service ticket successfully with new customer and vehicle creation.

**Tiếng Việt**: Kiểm thử tạo phiếu dịch vụ thành công với việc tạo khách hàng và xe mới.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: No (customerId: null)
- Vehicle exists: No (licensePlate: "30A-12345" not found)
- Brand exists: Yes (brandId: 1)
- Vehicle Model exists: Yes (modelId: 1)
- Default discount policy exists: Yes (BRONZE)

**Input**:
- customer: {customerId: null, fullName: "New Customer", phone: "0912345678", customerType: CA_NHAN}
- vehicle: {licensePlate: "30A-12345", brandName: "Toyota", modelName: "Camry", year: 2020, vin: "VIN123"}

**Expected**: 
- Successfully return ServiceTicketResponseDto
- New customer created with default BRONZE discount policy
- New vehicle created with brand and model
- Service ticket created and linked

**Result**: Normal, Passed

