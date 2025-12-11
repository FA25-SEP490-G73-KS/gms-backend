# DDL Statements for Garage Management System
# Generated from JPA Entities
#
# ============================================
# Database Setup
# ============================================

# Drop database if exists

DROP DATABASE IF EXISTS gms;

-- Create database
CREATE DATABASE gms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use database
USE gms;

-- ============================================
-- Base Tables (No Dependencies)
-- ============================================

-- Account Table
CREATE TABLE account (
                         account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         phone VARCHAR(20) UNIQUE,
                         role VARCHAR(50) NOT NULL,
                         password VARCHAR(100),
                         is_active BOOLEAN
);

-- Brand Table
CREATE TABLE brand (
                       brand_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

-- Unit Table
CREATE TABLE unit (
                      unit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255)
);

-- Market Table
CREATE TABLE market (
                        market_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255)
);

-- ServiceType Table
CREATE TABLE service_type (
                              service_type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(100) NOT NULL UNIQUE
);

-- PartCategory Table
CREATE TABLE part_category (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               name VARCHAR(100) NOT NULL
);

-- DiscountPolicy Table
CREATE TABLE discount_policy (
                                 discount_policy_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 loyalty_level VARCHAR(50) NOT NULL UNIQUE,
                                 discount_rate DECIMAL(5,2) NOT NULL,
                                 required_spending DECIMAL(18,2),
                                 description VARCHAR(255)
);

-- TimeSlot Table
CREATE TABLE time_slot (
                           time_slot_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           label VARCHAR(255) NOT NULL UNIQUE,
                           start_time TIME NOT NULL,
                           end_time TIME NOT NULL,
                           max_capacity INT NOT NULL DEFAULT 3
);

-- CodeSequence Table
CREATE TABLE code_sequence (
                               prefix VARCHAR(10) PRIMARY KEY,
                               year INT NOT NULL,
                               current_value BIGINT NOT NULL
);

-- Role Table
CREATE TABLE role (
                      role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      role_name VARCHAR(50)
);

-- ============================================
-- Employee and Customer Tables
-- ============================================

-- Employee Table
CREATE TABLE employee (
                          employee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          full_name VARCHAR(100) NOT NULL,
                          gender VARCHAR(10),
                          date_of_birth DATE,
                          phone VARCHAR(20) UNIQUE,
                          address VARCHAR(200),
                          hire_date DATETIME,
                          termination_date DATETIME,
                          daily_salary DECIMAL(18,2),
                          is_active TINYINT(1),
                          account_id BIGINT,
                          FOREIGN KEY (account_id) REFERENCES account(account_id)
);

-- Customer Table
CREATE TABLE customer (
                          customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          full_name VARCHAR(255),
                          phone VARCHAR(255),
                          address VARCHAR(200),
                          customer_type VARCHAR(30),
                          discount_policy_id BIGINT,
                          total_spending DECIMAL(18,2) DEFAULT 0,
                          is_active BOOLEAN DEFAULT TRUE,
                          FOREIGN KEY (discount_policy_id) REFERENCES discount_policy(discount_policy_id),
                          UNIQUE KEY uk_customer_phone_active (phone, is_active)
);

-- ============================================
-- Vehicle Related Tables
-- ============================================

-- VehicleModel Table
CREATE TABLE vehicle_model (
                               vehicle_model_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               name VARCHAR(100) NOT NULL UNIQUE,
                               brand_id BIGINT NOT NULL,
                               FOREIGN KEY (brand_id) REFERENCES brand(brand_id)
);

-- Vehicle Table
CREATE TABLE vehicle (
                         vehicle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         customer_id BIGINT NOT NULL,
                         license_plate VARCHAR(20) UNIQUE,
                         vehicle_model_id BIGINT,
                         year INT,
                         vin VARCHAR(50),
                         FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
                         FOREIGN KEY (vehicle_model_id) REFERENCES vehicle_model(vehicle_model_id)
);

-- ============================================
-- Supplier and Part Tables
-- ============================================

-- Supplier Table
CREATE TABLE suppliers (
                           supplier_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(150) NOT NULL,
                           phone VARCHAR(20),
                           email VARCHAR(120),
                           address VARCHAR(255),
                           is_active BOOLEAN
);

-- Part Table
CREATE TABLE part (
                      part_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      sku VARCHAR(50) UNIQUE,
                      part_name VARCHAR(100) NOT NULL,
                      category_id BIGINT,
                      vehicle_model BIGINT,
                      market BIGINT,
                      purchase_price DECIMAL(12,2),
                      selling_price DECIMAL(12,2),
                      discount_rate DECIMAL(5,2),
                      quantity_in_stock DOUBLE DEFAULT 0,
                      unit BIGINT,
                      reserved_quantity DOUBLE DEFAULT 0,
                      reorder_level DOUBLE DEFAULT 0,
                      supplier BIGINT,
                      is_universal BOOLEAN NOT NULL DEFAULT FALSE,
                      special_part BOOLEAN NOT NULL DEFAULT FALSE,
                      note VARCHAR(100),
                      status VARCHAR(50) NOT NULL,
                      FOREIGN KEY (category_id) REFERENCES part_category(id),
                      FOREIGN KEY (vehicle_model) REFERENCES vehicle_model(vehicle_model_id),
                      FOREIGN KEY (market) REFERENCES market(market_id),
                      FOREIGN KEY (unit) REFERENCES unit(unit_id),
                      FOREIGN KEY (supplier) REFERENCES suppliers(supplier_id)
);

-- PartOrigin Table
CREATE TABLE part_origin (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             country VARCHAR(255),
                             part_id BIGINT,
                             FOREIGN KEY (part_id) REFERENCES part(part_id)
);

-- ============================================
-- Appointment and Service Tables
-- ============================================

-- Appointment Table
CREATE TABLE appointment (
                             appointment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             appointment_code VARCHAR(20) NOT NULL UNIQUE,
                             customer_id BIGINT NOT NULL,
                             customer_name VARCHAR(30),
                             vehicle_id BIGINT NOT NULL,
                             time_slot_id BIGINT NOT NULL,
                             appointment_date DATE NOT NULL,
                             status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                             description NVARCHAR(255),
                             created_at DATETIME NOT NULL,
                             confirmed_at DATETIME,
                             is_reminder_sent BOOLEAN DEFAULT FALSE,
                             FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
                             FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id),
                             FOREIGN KEY (time_slot_id) REFERENCES time_slot(time_slot_id)
);

-- Appointment Service Type Junction Table
CREATE TABLE appointment_service_type (
                                          appointment_id BIGINT NOT NULL,
                                          service_type_id BIGINT NOT NULL,
                                          PRIMARY KEY (appointment_id, service_type_id),
                                          FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id),
                                          FOREIGN KEY (service_type_id) REFERENCES service_type(service_type_id)
);

-- ============================================
-- Price Quotation Tables
-- ============================================

-- PriceQuotation Table
CREATE TABLE price_quotation (
                                 price_quotation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 code VARCHAR(50) UNIQUE,
                                 discount DECIMAL(18,2),
                                 estimate_amount DECIMAL(18,2),
                                 status VARCHAR(50) DEFAULT 'DRAFT',
                                 created_by BIGINT,
                                 updated_by BIGINT,
                                 created_at DATETIME,
                                 updated_at DATETIME
);

-- PriceQuotationItem Table
CREATE TABLE price_quotation_item (
                                      price_quotation_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      quotation_id BIGINT,
                                      part_id BIGINT,
                                      part_name VARCHAR(100),
                                      unit_price DECIMAL(18,2),
                                      quantity DOUBLE,
                                      unit VARCHAR(20),
                                      total_price DECIMAL(18,2),
                                      exported_quantity DOUBLE,
                                      item_type VARCHAR(50) NOT NULL,
                                      status VARCHAR(50),
                                      warehouse_review_status VARCHAR(50),
                                      warehouse_note VARCHAR(255),
                                      created_by BIGINT,
                                      updated_by BIGINT,
                                      created_at DATETIME,
                                      updated_at DATETIME,
                                      FOREIGN KEY (quotation_id) REFERENCES price_quotation(price_quotation_id),
                                      FOREIGN KEY (part_id) REFERENCES part(part_id)
);

-- ServiceTicket Table
CREATE TABLE service_ticket (
                                service_ticket_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                service_ticket_code VARCHAR(20) NOT NULL UNIQUE,
                                appointment_id BIGINT UNIQUE,
                                customer_id BIGINT,
                                customer_name VARCHAR(255),
                                customer_phone VARCHAR(255),
                                vehicle_id BIGINT,
                                vehicle_license_plate VARCHAR(255),
                                quotation_id BIGINT,
                                status VARCHAR(50),
                                receive_condition TEXT,
                                notes TEXT,
                                created_at DATETIME,
                                updated_at DATETIME,
                                delivery_at DATE,
                                created_by_employee_id BIGINT,
                                FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id),
                                FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
                                FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id),
                                FOREIGN KEY (quotation_id) REFERENCES price_quotation(price_quotation_id),
                                FOREIGN KEY (created_by_employee_id) REFERENCES employee(employee_id)
);

-- Service Ticket Service Type Junction Table
CREATE TABLE ticket_service_type (
                                     service_ticket_id BIGINT NOT NULL,
                                     service_type_id BIGINT NOT NULL,
                                     PRIMARY KEY (service_ticket_id, service_type_id),
                                     FOREIGN KEY (service_ticket_id) REFERENCES service_ticket(service_ticket_id),
                                     FOREIGN KEY (service_type_id) REFERENCES service_type(service_type_id)
);

-- Service Ticket Technicians Junction Table
CREATE TABLE service_ticket_technicians (
                                            service_ticket_id BIGINT NOT NULL,
                                            employee_id BIGINT NOT NULL,
                                            PRIMARY KEY (service_ticket_id, employee_id),
                                            FOREIGN KEY (service_ticket_id) REFERENCES service_ticket(service_ticket_id),
                                            FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- ============================================
-- Stock Receipt Tables
-- ============================================

-- PurchaseRequest Table
CREATE TABLE purchase_request (
                                  purchase_request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  code VARCHAR(50) NOT NULL UNIQUE,
                                  quotation_id BIGINT,
                                  total_estimated_amount DECIMAL(18,2),
                                  review_status VARCHAR(30) DEFAULT 'PENDING',
                                  reason VARCHAR(255),
                                  created_by BIGINT,
                                  updated_by BIGINT,
                                  created_at DATETIME,
                                  updated_at DATETIME,
                                  FOREIGN KEY (quotation_id) REFERENCES price_quotation(price_quotation_id)
);

-- PurchaseRequestItem Table
CREATE TABLE purchase_request_item (
                                       purchase_request_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       purchase_request_id BIGINT,
                                       quotation_item_id BIGINT,
                                       part_id BIGINT,
                                       part_name VARCHAR(255) NOT NULL,
                                       quantity DOUBLE NOT NULL,
                                       unit VARCHAR(255),
                                       estimated_purchase_price DECIMAL(18,2) NOT NULL,
                                       quantity_received DOUBLE DEFAULT 0,
                                       review_status VARCHAR(30) DEFAULT 'PENDING',
                                       note VARCHAR(255),
                                       created_by BIGINT,
                                       updated_by BIGINT,
                                       created_at DATETIME,
                                       updated_at DATETIME,
                                       FOREIGN KEY (purchase_request_id) REFERENCES purchase_request(purchase_request_id),
                                       FOREIGN KEY (quotation_item_id) REFERENCES price_quotation_item(price_quotation_item_id),
                                       FOREIGN KEY (part_id) REFERENCES part(part_id)
);

-- StockReceipt Table
CREATE TABLE stock_receipt (
                               receipt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               code VARCHAR(50) NOT NULL UNIQUE,
                               purchase_request_id BIGINT,
                               supplier_id BIGINT,
                               created_by VARCHAR(255),
                               created_at DATETIME NOT NULL,
                               received_by VARCHAR(255),
                               received_at DATETIME,
                               status VARCHAR(50) NOT NULL,
                               total_amount DECIMAL(18,2),
                               note VARCHAR(255),
                               FOREIGN KEY (purchase_request_id) REFERENCES purchase_request(purchase_request_id),
                               FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- StockReceiptItem Table
CREATE TABLE stock_receipt_item (
                                    stock_receipt_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    stock_receipt_id BIGINT NOT NULL,
                                    purchase_request_item_id BIGINT,
                                    requested_quantity DOUBLE,
                                    quantity_received DOUBLE,
                                    actual_unit_price DECIMAL(18,2),
                                    actual_total_price DECIMAL(18,2),
                                    attachment_url VARCHAR(255),
                                    note VARCHAR(255),
                                    received_at DATETIME,
                                    received_by VARCHAR(255),
                                    status VARCHAR(50) NOT NULL,
                                    FOREIGN KEY (stock_receipt_id) REFERENCES stock_receipt(receipt_id),
                                    FOREIGN KEY (purchase_request_item_id) REFERENCES purchase_request_item(purchase_request_item_id)
);

-- StockReceiptItemHistory Table
CREATE TABLE stock_receipt_item_history (
                                            history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            stock_receipt_item_id BIGINT NOT NULL,
                                            quantity DOUBLE NOT NULL,
                                            unit_price DECIMAL(18,2),
                                            total_price DECIMAL(18,2),
                                            attachment_url VARCHAR(255),
                                            received_at DATETIME NOT NULL,
                                            received_by VARCHAR(255),
                                            note VARCHAR(255),
                                            payment_status VARCHAR(50) DEFAULT 'UNPAID',
                                            amount_paid DECIMAL(18,2) DEFAULT 0,
                                            payment_attachment VARCHAR(255),
                                            FOREIGN KEY (stock_receipt_item_id) REFERENCES stock_receipt_item(stock_receipt_item_id)
);

-- ============================================
-- Stock Export Tables
-- ============================================

-- StockExport Table
CREATE TABLE stock_export (
                              export_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              code VARCHAR(255) UNIQUE,
                              quotation_id BIGINT,
                              export_reason VARCHAR(255),
                              status VARCHAR(50) NOT NULL,
                              created_by VARCHAR(255),
                              exported_by VARCHAR(255),
                              approved_by VARCHAR(255),
                              created_at DATETIME,
                              exported_at DATETIME,
                              approved_at DATETIME,
                              FOREIGN KEY (quotation_id) REFERENCES price_quotation(price_quotation_id)
);

-- StockExportItem Table
CREATE TABLE stock_export_item (
                                   export_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   export_id BIGINT NOT NULL,
                                   quotation_item_id BIGINT,
                                   part_id BIGINT NOT NULL,
                                   quantity DOUBLE NOT NULL,
                                   quantity_exported DOUBLE,
                                   receiver_id BIGINT,
                                   exported_at DATETIME,
                                   status VARCHAR(50) NOT NULL,
                                   note VARCHAR(255),
                                   FOREIGN KEY (export_id) REFERENCES stock_export(export_id),
                                   FOREIGN KEY (quotation_item_id) REFERENCES price_quotation_item(price_quotation_item_id),
                                   FOREIGN KEY (part_id) REFERENCES part(part_id),
                                   FOREIGN KEY (receiver_id) REFERENCES employee(employee_id)
);

-- StockExportItemHistory Table
CREATE TABLE stock_export_item_history (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           export_item_id BIGINT NOT NULL,
                                           quantity DOUBLE NOT NULL,
                                           exported_at DATETIME NOT NULL,
                                           exported_by BIGINT,
                                           FOREIGN KEY (export_item_id) REFERENCES stock_export_item(export_item_id),
                                           FOREIGN KEY (exported_by) REFERENCES employee(employee_id)
);

-- ============================================
-- Part Reservation Table
-- ============================================

-- PartReservation Table
CREATE TABLE part_reservation (
                                  reservation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  part_id BIGINT NOT NULL,
                                  quotation_item_id BIGINT NOT NULL,
                                  reserved_quantity DOUBLE NOT NULL,
                                  reserved_at DATETIME,
                                  active BOOLEAN DEFAULT TRUE,
                                  FOREIGN KEY (part_id) REFERENCES part(part_id),
                                  FOREIGN KEY (quotation_item_id) REFERENCES price_quotation_item(price_quotation_item_id)
);

-- ============================================
-- Invoice and Transaction Tables
-- ============================================

-- Invoice Table
CREATE TABLE invoice (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         code VARCHAR(255),
                         price_quotation_id BIGINT,
                         service_ticket_id BIGINT,
                         deposit_received DECIMAL(18,2),
                         final_amount DECIMAL(18,2),
                         status VARCHAR(50) DEFAULT 'PENDING',
                         created_at DATETIME,
                         updated_at DATETIME,
                         created_by VARCHAR(255),
                         FOREIGN KEY (price_quotation_id) REFERENCES price_quotation(price_quotation_id),
                         FOREIGN KEY (service_ticket_id) REFERENCES service_ticket(service_ticket_id)
);

-- Debt Table
CREATE TABLE debt (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      customer_id BIGINT,
                      service_ticket_id BIGINT,
                      amount DECIMAL(18,2),
                      due_date DATE,
                      paid_amount DECIMAL(18,2) DEFAULT 0,
                      status VARCHAR(30) DEFAULT 'OUTSTANDING',
                      created_at DATETIME,
                      updated_at DATETIME,
                      FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
                      FOREIGN KEY (service_ticket_id) REFERENCES service_ticket(service_ticket_id)
);

-- Transaction Table
CREATE TABLE transaction (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             payment_link_id VARCHAR(255),
                             invoice_id BIGINT,
                             debt_id BIGINT,
                             customer_full_name VARCHAR(255) NOT NULL,
                             customer_phone VARCHAR(255) NOT NULL,
                             method VARCHAR(30),
                             type VARCHAR(30),
                             amount BIGINT,
                             created_at DATETIME,
                             is_active BOOLEAN,
                             FOREIGN KEY (invoice_id) REFERENCES invoice(id),
                             FOREIGN KEY (debt_id) REFERENCES debt(id)
);

-- ============================================
-- Discount Table
-- ============================================

-- Discount Table
CREATE TABLE discount (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          code VARCHAR(255) NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          type VARCHAR(50) NOT NULL,
                          value DECIMAL(18,2) NOT NULL,
                          expired_at DATETIME NOT NULL
);

-- ============================================
-- Employee Management Tables
-- ============================================

-- Attendance Table
CREATE TABLE attendance (
                            attendance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            employee_id BIGINT,
                            date DATE,
                            is_present BOOLEAN,
                            note VARCHAR(200),
                            recorded_by BIGINT,
                            recorded_at DATETIME,
                            FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
                            UNIQUE KEY uk_attendance_employee_date (employee_id, date)
);

-- Payroll Table
CREATE TABLE payroll (
                         payroll_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         employee_id BIGINT NOT NULL,
                         month INT,
                         year INT,
                         base_salary DECIMAL(18,2),
                         total_allowance DECIMAL(18,2),
                         total_deduction DECIMAL(18,2),
                         total_advance DECIMAL(18,2),
                         working_days INT,
                         net_salary DECIMAL(18,2),
                         status VARCHAR(50),
                         approved_by BIGINT,
                         approved_at DATETIME,
                         paid_by BIGINT,
                         paid_at DATETIME,
                         created_at DATETIME,
                         FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
                         FOREIGN KEY (approved_by) REFERENCES employee(employee_id),
                         FOREIGN KEY (paid_by) REFERENCES employee(employee_id),
                         UNIQUE KEY uk_payroll_employee_month_year (employee_id, month, year)
);

-- Allowance Table
CREATE TABLE allowance (
                           allowance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           employee_id BIGINT NOT NULL,
                           type VARCHAR(50),
                           amount DECIMAL(18,2) NOT NULL,
                           note VARCHAR(255),
                           month INT,
                           year INT,
                           created_at DATETIME,
                           created_by VARCHAR(255),
                           FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- Deduction Table
CREATE TABLE deduction (
                           deduction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           employee_id BIGINT NOT NULL,
                           type VARCHAR(50),
                           amount DECIMAL(18,2) NOT NULL,
                           reason VARCHAR(255),
                           date DATE,
                           created_by VARCHAR(255),
                           FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- ============================================
-- Ledger Voucher Table
-- ============================================

-- LedgerVoucher Table
CREATE TABLE ledger_voucher (
                                ledger_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                code VARCHAR(50) NOT NULL UNIQUE,
                                type VARCHAR(100) NOT NULL,
                                amount DECIMAL(18,2) NOT NULL,
                                related_employee_id BIGINT,
                                related_supplier_id BIGINT,
                                description VARCHAR(255),
                                created_at DATETIME,
                                approved_at DATETIME,
                                created_by_employee_id BIGINT,
                                approved_by_employee_id BIGINT,
                                attachment_url VARCHAR(255),
                                status VARCHAR(20) NOT NULL,
                                receipt_history_id BIGINT,
                                FOREIGN KEY (created_by_employee_id) REFERENCES employee(employee_id),
                                FOREIGN KEY (approved_by_employee_id) REFERENCES employee(employee_id),
                                FOREIGN KEY (receipt_history_id) REFERENCES stock_receipt_item_history(history_id)
);

-- ============================================
-- Service Rating Table
-- ============================================

-- ServiceRating Table
CREATE TABLE service_rating (
                                rating_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                service_ticket_id BIGINT NOT NULL,
                                customer_id BIGINT NOT NULL,
                                stars INT NOT NULL,
                                feedback VARCHAR(500),
                                created_at DATETIME NOT NULL,
                                FOREIGN KEY (service_ticket_id) REFERENCES service_ticket(service_ticket_id),
                                FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

-- ============================================
-- Notification Table
-- ============================================

-- Notification Table
CREATE TABLE notification (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              receiver_id BIGINT,
                              type VARCHAR(50),
                              title VARCHAR(255),
                              message VARCHAR(500),
                              reference_id BIGINT,
                              reference_type VARCHAR(255),
                              action_path VARCHAR(255),
                              status VARCHAR(50),
                              created_at DATETIME,
                              FOREIGN KEY (receiver_id) REFERENCES employee(employee_id)
);

-- ============================================
-- Access Token Table
-- ============================================

-- AccessToken Table
CREATE TABLE zalo_access_token (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   access_token VARCHAR(1000),
                                   refresh_token VARCHAR(1000),
                                   created_at TIMESTAMP,
                                   updated_at TIMESTAMP
);

-- ============================================
-- OTP Verification Table
-- ============================================

-- OtpVerification Table
CREATE TABLE otp_verification (
                                  otp_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  phone VARCHAR(20) NOT NULL,
                                  otp_code VARCHAR(6) NOT NULL,
                                  expires_at DATETIME NOT NULL,
                                  is_verified BOOLEAN NOT NULL DEFAULT FALSE,
                                  purpose VARCHAR(50),
                                  created_at DATETIME NOT NULL
);

# ============================================
# # ADD DATA SAMPLE
# ============================================

# TimeSlot
USE gms;
INSERT INTO time_slot(label, start_time, end_time, max_capacity)
VALUES
    ('07:30-09:30','07:30:00','09:30:00',3),
    ('09:30-11:30','09:30:00','11:30:00',3),
    ('13:30-15:30','13:30:00','15:30:00',3),
    ('15:30-17:30','15:30:00','17:30:00',3);

-- === SERVICE TYPES ===
INSERT INTO service_type (name) VALUES ('Sơn');
INSERT INTO service_type (name) VALUES ('Sửa chữa');
INSERT INTO service_type (name) VALUES ('Bảo dưỡng');
INSERT INTO service_type (name) VALUES ('Bảo hành');

INSERT INTO role (role_id, role_name)
VALUES
    (1, 'ADMIN'),
    (2, 'USER');

INSERT INTO account (account_id, phone, password, role, is_active)
VALUES
    (1, '0909123456', '$2a$10$1405F5A.2xsmn98bZYT3GeeIUcKvpaCfAK.9iqJaOQElN9y33Tagy', 'SERVICE_ADVISOR', 1),
    (2, '0987123456', '$2a$10$1405F5A.2xsmn98bZYT3GeeIUcKvpaCfAK.9iqJaOQElN9y33Tagy', 'ACCOUNTANT', 1),
    (3, '0909988776', '$2a$10$1405F5A.2xsmn98bZYT3GeeIUcKvpaCfAK.9iqJaOQElN9y33Tagy', 'MANAGER', 1),
    (4, '0123456789', '$2a$10$1405F5A.2xsmn98bZYT3GeeIUcKvpaCfAK.9iqJaOQElN9y33Tagy', 'WAREHOUSE', 1);


INSERT INTO employee (
    employee_id, full_name, gender, date_of_birth, phone, address, hire_date, account_id, daily_salary, is_active
) VALUES
      (1, 'Nguyễn Văn An', 'Nam', '1985-04-10', '0909123456', '123 Đường A, Quận 1', '2023-05-10 08:00:00', 1, 200000.00, 1),
      (2, 'Trần Minh Đức', 'Nam', '1990-07-15', '0912345678', '456 Đường B, Quận 2', '2023-06-01 08:00:00', 2, 200000.00, 1),
      (3, 'Phạm Thị Hoa', 'Nữ', '1992-01-20', '0987123456', '789 Đường C, Quận 3', '2024-01-15 08:00:00', 3, 200000.00, 1),
      (4, 'Nguyễn Minh Đức', 'Nam', '1992-01-20', '0123456789', '789 Đường C, Quận 3', '2024-01-15 08:00:00', 4, 200000.00, 1),
      (5, 'Nguyễn Tuấn Anh', 'Nam', '1992-01-20', '0999999999', '789 Đường C, Quận 3', '2024-01-15 08:00:00', null, 200000.00, 1),
      (6, 'Nguyễn Sơn Lâm', 'Nam', '1992-01-20', '0888888888', '789 Đường C, Quận 3', '2024-01-15 08:00:00', null, 200000.00, 1);

INSERT INTO part_category (name) VALUES
                                     ('Động cơ'),
                                     ('Truyền động – Hộp số'),
                                     ('Hệ thống treo – khung gầm'),
                                     ('Hệ thống phanh'),
                                     ('Hệ thống làm mát'),
                                     ('Điều hòa – HVAC'),
                                     ('Điện – Điện tử'),
                                     ('Thân vỏ – Ngoại thất'),
                                     ('Nội thất'),
                                     ('Dầu – Hóa chất'),
                                     ('Vật tư tiêu hao');

INSERT INTO market (name)
VALUES
    ('VN'),
    ('JP'),
    ('US'),
    ('EU'),
    ('OTHER');

INSERT INTO brand (name) VALUES
                             ('Toyota'),
                             ('Honda'),
                             ('Ford');

INSERT INTO vehicle_model (brand_id, name) VALUES
                                               (1, 'Vios'),
                                               (1, 'Corolla'),
                                               (2, 'Civic'),
                                               (2, 'City'),
                                               (3, 'Focus'),
                                               (3, 'Ranger');


INSERT INTO unit (name)
VALUES ('cái'),
       ('bộ'),
       ('lít'),
       ('cặp');

INSERT INTO suppliers (name, phone, email, address, is_active) VALUES
                                                                   ('Toyota Genuine Parts', '0901122334', 'sale@toyota-parts.vn', 'Số 1 Phạm Văn Đồng, Hà Nội', TRUE),
                                                                   ('Honda Việt Nam', '0912345678', 'contact@honda.vn', 'Phúc Thắng, Phúc Yên, Vĩnh Phúc', TRUE),
                                                                   ('Phutungoto88', '0988776655', 'support@phutungoto88.vn', 'Hoàng Quốc Việt, Cầu Giấy, Hà Nội', TRUE),
                                                                   ('An Phú Auto Parts', '0905566778', 'info@anphuauto.vn', 'Quận 7, TP.HCM', TRUE),
                                                                   ('Ngọc Hùng Auto', '0977334455', 'ngochungauto@gmail.com', 'Ba Đình, Hà Nội', TRUE),
                                                                   ('Nippon Japan Parts', '0966123456', 'sales@nipponparts.jp', 'Tokyo, Japan', TRUE),
                                                                   ('Thai Motor Parts', '0822233344', 'order@thaimotorparts.co.th', 'Bangkok, Thailand', TRUE),
                                                                   ('GS Battery Supplier', '0944667788', 'gsdealer@gsbattery.com', 'KCN Tân Bình, TP.HCM', TRUE),
                                                                   ('Autotech USA', '+1-202-777-8899', 'contact@autotechusa.com', 'Washington DC, USA', TRUE),
                                                                   ('Euro Auto Supply', '+49-175-233-2211', 'euro@supply.eu', 'Berlin, Germany', TRUE);

INSERT INTO part (
    part_name,
    category_id,
    market,
    is_universal,
    purchase_price,
    selling_price,
    discount_rate,
    quantity_in_stock,
    reserved_quantity,
    reorder_level,
    special_part,
    vehicle_model,
    sku,
    supplier,
    unit,
    status
) VALUES
      ('Lọc nhớt động cơ', 10, 1, false, 80000, 120000, 0.00, 50, 0, 10, false, 1,
       'LOCNHODONGCO-VIOS-VN', 1, 1, 'IN_STOCK'),

      ('Lọc nhớt động cơ', 10, 2, false, 80000, 120000, 0.00, 50, 0, 10, false, 2,
       'LOCNHODONGCO-COROLLA-JP', 2, 1, 'IN_STOCK'),

      ('Dầu động cơ 10W-40', 10, 1, true, 250000, 320000, 5.00, 100, 5, 20, false, 3,
       'DAUDONGCO10W40-CIVIC-VN', 3, 1, 'IN_STOCK'),

      ('Bugi đánh lửa', 1, 2, false, 40000, 80000, 0.00, 80, 2, 15, false, 4,
       'BUGIDANHLUA-CITY-JP', 4, 1, 'IN_STOCK'),

      ('Phanh đĩa trước', 4, 2, false, 300000, 450000, 0.00, 30, 0, 10, false, 5,
       'PHANHDIATRUOC-FOCUS-JP', 5, 1, 'IN_STOCK'),

      ('Ắc quy GS 12V-45Ah', 7, 2, true, 950000, 1200000, 3.00, 20, 1, 5, false, 1,
       'ACQUYGS12V45AH-VIOS-JP', 6, 1, 'IN_STOCK'),

      ('Dây curoa tổng', 1, 2, false, 180000, 250000, 0.00, 40, 0, 10, false, 2,
       'DAYCUROATONG-COROLLA-JP', 7, 1, 'IN_STOCK'),

      ('Lọc gió điều hoà', 6, 3, true, 50000, 90000, 0.00, 60, 0, 15, false, 3,
       'LOCGIODIEUHOA-CIVIC-US', 8, 1, 'IN_STOCK'),

      ('Gạt mưa trước', 8, 1, true, 30000, 60000, 0.00, 120, 0, 30, false, 4,
       'GATMUATRUOC-CITY-VN', 9, 1, 'IN_STOCK'),

      ('Đèn pha LED', 8, 3, false, 700000, 950000, 0.00, 15, 0, 5, false, 5,
       'DENPHALED-FOCUS-US', 10, 1, 'IN_STOCK'),

      ('Cảm biến ABS', 7, 4, false, 450000, 600000, 0.00, 10, 0, 3, false, 1,
       'CAMBIENABS-VIOS-EU',1, 1, 'IN_STOCK');


INSERT INTO discount_policy (loyalty_level, discount_rate, required_spending, description)
VALUES
    ('BRONZE', 0.00,   0.00,  'Mức cơ bản'),
    ('SLIVER', 5.00,  5000.00, 'Chi tiêu trên 5 triệu'),
    ('GOLD', 10.00, 10000.00, 'Chi tiêu trên 10 triệu');

INSERT INTO customer (full_name, phone, address, customer_type, discount_policy_id)
VALUES
    ('Nguyễn Văn A', '84123456789', '123 Nguyễn Trãi, Hà Nội', 'CA_NHAN', 1),
    ('Trần Thị B', '84123987654', '45 Lê Lợi, TP.HCM', 'CA_NHAN', 1),
    ('Lê Văn C', '84987654321', '78 Hai Bà Trưng, Đà Nẵng', 'CA_NHAN', 1);


INSERT INTO vehicle (customer_id, vehicle_model_id, license_plate, vin, year)
VALUES
    (1, 2, '30A-1004', 'VIN1004', 2021),
    (1, 1, '30A-1001', 'VIN1001', 2021),
    (2, 1, '30A-1002', 'VIN1002', 2022),
    (3, 1, '30A-1003', 'VIN1003', 2023);

INSERT INTO zalo_access_token (access_token, refresh_token, created_at)
VALUES (
           'K3u4Gmj77qvMD0PB3qaRJbKm0qiDL1uXDNjqNZv82HCUDc4Q8WKxQ7yk8Jjk1qzSKtKDBK9eT29kG6fQRLaDBKrY4YLk7rTjR0qj2aTrKMrBMniZP2LdMM8NUrr3CH4u6bPvTJLj9YyQ2Zj_0tCYC0HtUdqDKHiB1486Obj3QXXuSnPB875EAIPUNoCWGZGk1dn49mDrBduuMZOCEsTEV3KLBXWeE6jFDJzs4MWaQtP263Dx7XnE7XDaLriaJpHM9NnO1dLu45nUUGal1abX8JTWBN08Jq8LJNScNsXdPZf8N0joOtHJ9dXKGtbOV3ryLczjR0n_7I1NSc9CQM4R1MTuUaitKpOc37X9N5ncBLHoGNnRO4C6Ar1bV58FG6OVLtrvQdjO9W9cOa9pU7XIBo4A8LiJDcGKLrXeIYaz35eUIG',
           'kz4pC4Uy-Z-zsp4uGxB-FxxVD3X1hwes_uLyNs6BwIkTfb5w2uh88ewPPcemlQe3XzGr4IAwldIYr3yR0lwsQPVLVcLlu_KjzjWwLaopzZp9W6XMSBdmBl2jUcPoY_aLaECoRGsBl2hnZGv_Jvcs4jc_4dadaejpYQyA6ZA7WqIdg2Wo4BgbPuQK20fyeRbWvj8rDLoxhMNvt30FJkFc1zE9N7HLWTK-xQSNNd6Be0d9dG5HKQ2C7SsS5aWlePa-qeipJ6sCaZRZjoTm7BQO9O6mEc0qt9PiYlWTEn3_d4xjmYusHiEnNV6a9ojsYunPr9y0D6YImaJzbdfCSOd28jUsI59kgV1OoA1iK2go_3MzWaL08uYD2O_xF0Cobe1zjgGQEYsuhsU3XIOpBwsDTB6W0IbBprxxFq2X-pW',
           '2024-12-10 10:00:00'
       );




