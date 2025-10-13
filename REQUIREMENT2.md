# Requirements
- all query API requires
    - pagination
    - sort
- DB: mysql
- using 3-layer architecture, organize in folders: controller, service, repository
- service must be interface-based

## 1. Entity: Account
- account_id (PK)
- phone (ref -> Employee.phone)
- role_id (ref -> Role.role_id)
- password

## 1.1 API: Authentication
- login
- Forget Password

## 1.2 API: Account
- create an account
- update an account
- delete an account
- find an account by account_id
- list all account
- search account by keywork(phone)

## 2. Entity: Customer
- customer_id (PK)
- full_name
- phone
- zalo_id
- address
- customer_type (cá nhân/doanh nghiệp)
- loyalty_level (VIP/VVIP/Normal)

## 2.1 API: Customer
- create an customer
- update an customer
- delete an customer
- find an customer by customer_id
- list all customer
- search customer by keywork(phone, full_name)

## 3. Entity: Vehicle
- vehicle_id (PK)
- customer_id (ref -> Customer.customer_id)
- license_plate
- brand
- model
- year
- vin

## 3.1 API: Vehicle
- create an vehicle
- update an vehicle
- delete an vehicle
- find an vehicle by vehicle_id
- list all vehicle
- search vehicle by keywork(license_plate, brand)

## 4. Entity: Appointment
- appointment_id (PK)
- customer_id (ref -> Customer.customer_id)
- vehicle_id (ref -> Vehicle.vehicle_id)
- service_type
- appointment_date
- status (Đã đến/Đã hủy/Quá hạn)
- description
- image_url
- created_at

## 4.1 API: Appointment
- create an appointment
- update an appointment
- delete an appointment
- find an appointment by appointment_id
- list all appointment
- search appointment by keywork(appointment_date, customer_id, vehicle_id)


## 5. Entity: ServiceTicket
- service_ticket_id (PK)
- appointment_id (ref -> Appointment.appointment_id)
- employee_id (ref -> Employee.employee_id)
- customer_id (ref -> Customer.customer_id)
- vehicle_id (ref -> Vehicle.vehicle_id)
- status (chờ báo giá/duyệt/không duyệt/đang sửa/Chờ thanh toán/Chờ công nợ/hoàn thành/Hủy)
- notes
- created_at
- delivery_at

## 5.1 API: ServiceTicket
- create an serviceticket
- update an serviceticket
- delete an serviceticket
- find an serviceticket by service_ticket_id
- list all serviceticket
- search serviceticket by keywork(appointment_date, customer_id, vehicle_id)
## 5.2 API: ServiceTicket Actions
- update serviceticket status
- add note to serviceticket
- set delivery date

## 6. Entity: PriceQuotation
- price_quotation_id (PK)
- service_ticket_id (ref -> ServiceTicket.service_ticket_id)
- total_amount
- status (Duyệt/Không duyệt/Chờ duyệt)
- created_at

## 6.1 API
- create an price_quotation
- update an price_quotation
- delete an price_quotation
- find an price_quotation by price_quotation_id
- list all price_quotation
- search price_quotation by keywork(status, created_at)


## 7. Entity: PriceQuotationItem
- price_quotation_item_id (PK)
- price_quotation_id (ref -> PriceQuotation.price_quotation_id)
- part_id (ref -> Part.part_id, nullable)
- description
- quantity
- total_price
- part_status (Có sẵn/Order/Giá tạm tính)
- update_at
- account_id (ID người cập nhật)
- update_status (xác nhận/chờ/sửa lại)

## 7.1 API: PriceQuotationItem
- create an price_quotation_item
- update an price_quotation_item
- delete an price_quotation_item
- find an price_quotation_item by quotation_item_id
- list all price_quotation_item
- search price_quotation_item by keywork(price_quotation_id, part_status)


## 8. Entity: Part
- part_id (PK)
- part_name
- supplier
- cost_price
- sell_price
- quantity_in_stock
- status (Có sẵn/Order/Đã giữ)
- reorder_level
- last_updated
- category_id (ref -> Category.category_id)

## 8.1 API: Part
- create a part
- update a part
- delete a part
- find a part by part_id
- list all parts
- search part by keyword(name, supplier, status, category_id)


## 9. Entity: InventoryTransaction
- inventory_transaction_id (PK)
- part_id (ref -> Part.part_id)
- type (Nhập/Xuất/Điều chỉnh)
- quantity
- service_ticket_id (ref -> ServiceTicket.service_ticket_id)
- purchase_request_id (ref -> PurchaseRequest.purchase_request_id)
- created_at

## 9.1 API: InventoryTransaction
- create an inventory_transaction
- update an inventory_transaction
- delete an inventory_transaction
- find an inventory_transaction by inventory_transaction_id
- list all inventory_transaction
- search inventory_transaction by keyword(part_id, type, service_ticket_id, purchase_request_id, created_at)


## 10. Entity: PurchaseRequest
- purchase_request_id (PK)
- price_quotation_id (ref -> PriceQuotation.price_quotation_id)
- part_id (ref -> Part.part_id)
- supplier
- expected_date
- status (Đã gửi/Đã xác nhận)
- created_at

## 10.1 API: PurchaseRequest
- create a purchase_request
- update a purchase_request
- delete a purchase_request
- find a purchase_request by purchase_request_id
- list all purchase_request
- search purchase_request by keyword(price_quotation_id, part_id, supplier, status, expected_date)

## 11. Entity: Payment
- payment_id (PK)
- ticket_id (ref -> ServiceTicket.service_ticket_id)
- payment_method (Tiền mặt/Chuyển khoản/Thẻ)
- amount
- payment_date
- status (Đã thanh toán/Công nợ)
- reference_code

## 11.1 API: Payment
- create a payment
- update a payment
- delete a payment
- find a payment by payment_id
- list all payment
- search payment by keyword(ticket_id, payment_method, status, payment_date)


## 12. Entity: Debt
- debt_id (PK)
- customer_id (ref -> Customer.customer_id)
- service_ticket_id (ref -> ServiceTicket.service_ticket_id)
- amount_due
- due_date
- paid_amount
- status (Còn nợ/Đã tất toán)
- updated_at

## 12.1 API: Debt
- create a debt
- update a debt
- delete a debt
- find a debt by debt_id
- list all debt
- search debt by keyword(customer_id, service_ticket_id, status, due_date)


## 13. Entity: Employee
- employee_id (PK)
- full_name
- position (Manager/Technician/Staff/...)
- phone
- salary_base
- paid_amount
- hire_date
- status (Active/Inactive)

## 13.1 API: Employee
- create an employee
- update an employee
- delete an employee
- find an employee by employee_id
- list all employee
- search employee by keyword(phone, full_name, position, status)


## 14. Entity: Assignment
- assignment_id (PK)
- service_ticket_id (ref -> ServiceTicket.service_ticket_id)
- employee_id (ref -> Employee.employee_id)
- start_time
- end_time
- note

## 14.1 API: Assignment
- create an assignment
- update an assignment
- delete an assignment
- find an assignment by assignment_id
- list all assignment
- search assignment by keyword(service_ticket_id, employee_id, start_time, end_time)


## 15. Entity: Attendance
- attendance_id (PK)
- employee_id (ref -> Employee.employee_id)
- date
- is_present_am
- is_present_pm
- note
- recorded_by (account/administrator id)
- recorded_at

## 15.1 API: Attendance
- create an attendance
- update an attendance
- delete an attendance
- find an attendance by attendance_id
- list all attendance
- search attendance by keyword(employee_id, date, recorded_by)


## 16. Entity: Payroll
- payroll_id (PK)
- employee_id (ref -> Employee.employee_id)
- month
- year
- total_salary
- advance_deduction
- warranty_deduction
- salary_bonus
- net_salary
- create_at
- status (Đã phát/Chưa phát/Hủy)

## 16.1 API: Payroll
- create a payroll
- update a payroll
- delete a payroll
- find a payroll by payroll_id
- list all payroll
- search payroll by keyword(employee_id, month, year, status)


## 17. Entity: Expense
- expense_id (PK)
- type
- description
- amount
- employee_id (ref -> Employee.employee_id)
- expense_date
- created_at

## 17.1 API: Expense
- create an expense
- update an expense
- delete an expense
- find an expense by expense_id
- list all expense
- search expense by keyword(type, employee_id, expense_date)


## 18. Entity: Warranty
- warranty_id (PK)
- service_ticket_id (ref -> ServiceTicket.service_ticket_id)
- customer_id (ref -> Customer.customer_id)
- type (Trong nước/Nhà cung cấp/Nội bộ)
- cost_dn
- cost_tech
- cost_customer
- approved_by (ref -> Employee.employee_id)
- supplier_status
- status (Mới/Đang xử lý/Hoàn tất)
- created_at
- approved_at
- description
- attachment_url

## 18.1 API: Warranty
- create a warranty
- update a warranty
- delete a warranty
- find a warranty by warranty_id
- list all warranty
- search warranty by keyword(service_ticket_id, customer_id, status, type, approved_by)


## 19. Entity: WarrantyItem
- warranty_item_id (PK)
- warranty_id (ref -> Warranty.warranty_id)
- part_id (ref -> Part.part_id, nullable)
- description
- cost
- note

## 19.1 API: WarrantyItem
- create a warranty_item
- update a warranty_item
- delete a warranty_item
- find a warranty_item by warranty_item_id
- list all warranty_item
- search warranty_item by keyword(warranty_id, part_id, description)


## 20. Entity: Role
- role_id (PK)
- role_name

## 20.1 API: Role
- create a role
- update a role
- delete a role
- find a role by role_id
- list all role
- search role by keyword(role_name)


## 21. Entity: Category
- category_id (PK)
- category_name
- brand

## 21.1 API: Category
- create a category
- update a category
- delete a category
- find a category by category_id
- list all category
- search category by keyword(category_name, brand)

