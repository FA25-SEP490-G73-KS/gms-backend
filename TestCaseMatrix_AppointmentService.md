# Test Case Matrix: Appointment Service Functions / Ma Trận Test Case: Các Chức Năng Appointment Service

## Test Case Matrix: Get Time Slots By Date

| Precondition | UTCID01 |
|--------------|---------|
| **Can connect with server** | O |
| **Time Slots** | | |
| Has time slots | O |
| **Appointments** | | |
| Has appointments for date | O |
| **Input Parameters** | | |
| **date** | | |
| LocalDate.now() (valid) | O |
| **Expected Outcome** | | |
| **Return** | | |
| Successfully (List<TimeSlotDto>) | O |
| booked count calculated | O |
| available calculated (maxCapacity - booked) | O |
| **Result** | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N |
| Passed/Failed | P | P |

---

## Test Case Matrix: Create Appointment

| Precondition | UTCID01 | UTCID02 | UTCID03 | UTCID04 |
|--------------|---------|---------|---------|---------|
| **Can connect with server** | O | O | O | O |
| **Customer** | | | | |
| Does not exist (phone: "0909000000") | O | | | |
| Existing (phone: "0909000000") | | O | O | |
| **Daily Limit** | | | | |
| Not exceeded (count: 0) | O | O | | |
| Exceeded (count: 1, max: 1) | | | O | |
| **Time Slot** | | | | |
| Valid index (1) | O | O | O | |
| Invalid index (5) | | | | O |
| **Discount Policy** | | | | |
| Default BRONZE exists | O | O | O | O |
| **Vehicle** | | | | |
| Does not exist (licensePlate) | O | O | | |
| **Service Types** | | | | |
| Valid (ids: 1, 2) | O | O | O | O |
| **Input Parameters** | | | | |
| **customerName** | | | | |
| "Nguyen Van A" | O | O | O | O |
| **phoneNumber** | | | | |
| "0909000000" | O | O | O | O |
| **licensePlate** | | | | |
| "30A-123.45" | O | O | O | O |
| **appointmentDate** | | | | |
| today | O | O | O | O |
| **timeSlotIndex** | | | | |
| 1 (valid) | O | O | O | |
| 5 (invalid) | | | | O |
| **serviceType** | | | | |
| [1, 2] (valid) | O | O | O | O |
| **Expected Outcome** | | | | |
| **Return** | | | | |
| Successfully (AppointmentResponseDto) | O | O | | |
| **Exception** | | | | |
| IllegalArgumentException (daily limit) | | | O | |
| IllegalArgumentException (invalid timeSlot) | | | | O |
| **Appointment** | | | | |
| Created with code (APT001) | O | O | | |
| Status: CONFIRMED | O | O | | |
| **Customer** | | | | |
| Created (new) | O | | | |
| Updated (existing) | | O | | |
| **Vehicle** | | | | |
| Created (new) | O | O | | |
| **Notification** | | | | |
| Sent (ZNS) | O | O | | |
| **Result** | | | | |
| Type (N: Normal, A: Abnormal, B: Boundary) | N | N | A | A |
| Passed/Failed | P | P | P | P |

## Test Case Descriptions / Mô Tả Test Case

### UTCID01: Get Time Slots By Date - Normal Flow
**English**: Test getting time slots by date successfully with booked count and availability calculated.

**Tiếng Việt**: Kiểm thử lấy các khung giờ theo ngày thành công với số lượng đã đặt và tính khả dụng.

**Preconditions**:
- Can connect with server: Yes
- Time Slots exist: Yes
- Appointments exist: Yes (has appointments for date)

**Input**:
- date: LocalDate.now()

**Expected**: 
- Successfully return List<TimeSlotDto>
- booked count calculated from appointments
- available = maxCapacity - booked
- isAvailable = available > 0

**Result**: Normal, Passed

---

### UTCID01: Create Appointment - Normal Flow with New Customer
**English**: Test creating appointment successfully with new customer and vehicle creation.

**Tiếng Việt**: Kiểm thử tạo lịch hẹn thành công với việc tạo khách hàng và xe mới.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: No (phone: "0909000000")
- Daily limit not exceeded: Yes (count: 0)
- Time Slot valid: Yes (index: 1)
- Default discount policy exists: Yes (BRONZE)
- Vehicle does not exist: Yes (licensePlate: "30A-123.45")
- Service Types valid: Yes (ids: 1, 2)

**Input**:
- customerName: "Nguyen Van A"
- phoneNumber: "0909000000"
- licensePlate: "30A-123.45"
- appointmentDate: today
- timeSlotIndex: 1
- serviceType: [1, 2]

**Expected**: 
- Successfully return AppointmentResponseDto
- New customer created with default BRONZE policy
- New vehicle created
- Appointment created with code "APT001"
- Status: CONFIRMED
- ZNS notification sent

**Result**: Normal, Passed

---

### UTCID02: Create Appointment - Normal Flow with Existing Customer
**English**: Test creating appointment successfully with existing customer.

**Tiếng Việt**: Kiểm thử tạo lịch hẹn thành công với khách hàng hiện có.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: Yes (phone: "0909000000")
- Daily limit not exceeded: Yes (count: 0)
- Time Slot valid: Yes (index: 1)
- Vehicle does not exist: Yes

**Input**:
- customerName: "Nguyen Van A"
- phoneNumber: "0909000000"
- licensePlate: "30A-123.45"
- appointmentDate: today
- timeSlotIndex: 1
- serviceType: [1, 2]

**Expected**: 
- Successfully return AppointmentResponseDto
- Customer updated (if needed)
- Appointment created

**Result**: Normal, Passed

---

### UTCID03: Create Appointment - Daily Limit Exceeded
**English**: Test creating appointment when daily limit exceeded should throw IllegalArgumentException.

**Tiếng Việt**: Kiểm thử tạo lịch hẹn khi vượt quá giới hạn hàng ngày sẽ ném IllegalArgumentException.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: Yes (phone: "0909000000")
- Daily limit exceeded: Yes (count: 1, max: 1)

**Input**:
- customerName: "Nguyen Van A"
- phoneNumber: "0909000000"
- licensePlate: "30A-123.45"
- appointmentDate: today
- timeSlotIndex: 1
- serviceType: [1, 2]

**Expected**: IllegalArgumentException ("Bạn chỉ được đặt tối đa...")

**Result**: Abnormal, Passed

---

### UTCID04: Create Appointment - Invalid Time Slot Index
**English**: Test creating appointment with invalid time slot index should throw IllegalArgumentException.

**Tiếng Việt**: Kiểm thử tạo lịch hẹn với chỉ số khung giờ không hợp lệ sẽ ném IllegalArgumentException.

**Preconditions**:
- Can connect with server: Yes
- Customer exists: Yes
- Time Slot invalid: Yes (index: 5, out of range)

**Input**:
- customerName: "Nguyen Van A"
- phoneNumber: "0909000000"
- licensePlate: "30A-123.45"
- appointmentDate: today
- timeSlotIndex: 5 (invalid)
- serviceType: [1, 2]

**Expected**: IllegalArgumentException (invalid time slot index)

**Result**: Abnormal, Passed

