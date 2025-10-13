# Requirements
- all query API requires
  - pagination
  - sort
- DB: mysql
- using 3-layer architecture, organize in folders: controller, service, repository
- service must be interface-based

## Entity: Account
- account_id (PK)
- phone (ref -> Employee.phone) (Phone number must be 10 digits)
- role_id (ref -> Role.role_id)
- password (at least 8 characters)
## Entity: Role
- role_id (PK)
- role_name (Service Advisor,Accountant,Manager,Warehouse Staff)
-
## API: Authentication
- login
- Forget Password

## API: Account
- create an account
- update an account
- delete an account
- find an account by account_id
- list all account
- search account by keywork(phone)

# Appointment
## Entity: Customer
- customer_id (PK)
- full_name
- phone (Phone number must be 10 digits)
- zalo_id
- address 
- customer_type (cá nhân/ doanh nghiệp)
- loyalty_level (VIP/VVIP/Normal)

## Entity: Appointment
- appointment_id (PK)
- customer_id (ref -> Customer.customer_id)
- vehicle_id (ref -> Vehicle.vehicle_id)
- service_type (Sơn, Thay thế phụ tùng, Bảo dưỡng)
- appointment_date
- status (Xác nhận/ Đã đến/ Quá hạn/ Đã hủy/)
- description
- image_url
- created_at

## Entity: Vehicle
- vehicle_id (PK)
- customer_id (ref -> Customer.customer_id)
- license_plate ()
- brand 
- model
- year
- vin 

## API: Appointment
- update appointment_date Appointment
- update Status Appointment (Trừ trạng thái Quá hạn)
- list all Appointment
