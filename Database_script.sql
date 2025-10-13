DROP DATABASE IF EXISTS GarageManagement;
CREATE DATABASE GarageManagement CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE GarageManagement;

CREATE TABLE Customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    phone VARCHAR(50),
    zalo_id VARCHAR(50),
    address VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    customer_type VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    loyalty_level VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);

CREATE TABLE Category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    brand VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);

CREATE TABLE Vehicle (
    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    license_plate VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    brand VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    model VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    year INT,
    vin VARCHAR(50),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);

CREATE TABLE Employee (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    position VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    phone VARCHAR(20),
    salary_base DECIMAL(18,2),
    paid_amount DECIMAL(18,2),
    hire_date DATETIME,
    status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);

CREATE TABLE Role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);

CREATE TABLE Account (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20),
    role_id INT NOT NULL,
    password VARCHAR(20),
    FOREIGN KEY (role_id) REFERENCES Role(role_id)
);

CREATE TABLE Part (
    part_id INT AUTO_INCREMENT PRIMARY KEY,
    part_name VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    supplier VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    cost_price DECIMAL(18,2),
    sell_price DECIMAL(18,2),
    quantity_in_stock INT,
    status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    reorder_level INT,
    last_updated DATETIME,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES Category(category_id)
);

CREATE TABLE Appointment (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    vehicle_id INT,
    service_type VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    appointment_date DATETIME,
    status VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    image_url VARCHAR(300),
    created_at DATETIME,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)
);

CREATE TABLE ServiceTicket (
    service_ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT,
    customer_id INT,
    vehicle_id INT,
    status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    created_at DATETIME,
    delivery_at DATETIME,
    FOREIGN KEY (appointment_id) REFERENCES Appointment(appointment_id),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)
);

CREATE TABLE PriceQuotation (
    price_quotation_id INT AUTO_INCREMENT PRIMARY KEY,
    service_ticket_id INT,
    total_amount DECIMAL(18,2),
    status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    created_at DATETIME,
    FOREIGN KEY (service_ticket_id) REFERENCES ServiceTicket(service_ticket_id)
);

CREATE TABLE PriceQuotationItem (
    price_quotation_item_id INT AUTO_INCREMENT PRIMARY KEY,
    price_quotation_id INT,
    part_id INT,
    description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    quantity INT,
    total_price DECIMAL(18,2),
    part_status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    update_at DATETIME,
    account_id INT,
    update_status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (price_quotation_id) REFERENCES PriceQuotation(price_quotation_id),
    FOREIGN KEY (part_id) REFERENCES Part(part_id),
    FOREIGN KEY (account_id) REFERENCES Account(account_id)
);

CREATE TABLE PurchaseRequest (
    purchase_request_id INT AUTO_INCREMENT PRIMARY KEY,
    price_quotation_id INT,
    part_id INT,
    supplier VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    expected_date DATETIME,
    status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    created_at DATETIME,
    FOREIGN KEY (price_quotation_id) REFERENCES PriceQuotation(price_quotation_id),
    FOREIGN KEY (part_id) REFERENCES Part(part_id)
);

CREATE TABLE InventoryTransaction (
    inventory_transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    part_id INT,
    type VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    quantity INT,
    service_ticket_id INT,
    purchase_request_id INT,
    created_at DATETIME,
    FOREIGN KEY (part_id) REFERENCES Part(part_id),
    FOREIGN KEY (service_ticket_id) REFERENCES ServiceTicket(service_ticket_id),
    FOREIGN KEY (purchase_request_id) REFERENCES PurchaseRequest(purchase_request_id)
);

CREATE TABLE Payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT,
    payment_method VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    amount DECIMAL(18,2),
    payment_date DATETIME,
    status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    reference_code VARCHAR(50),
    FOREIGN KEY (ticket_id) REFERENCES ServiceTicket(service_ticket_id)
);

CREATE TABLE Debt (
    debt_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    service_ticket_id INT,
    amount_due DECIMAL(18,2),
    due_date DATE,
    paid_amount DECIMAL(18,2),
    status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    updated_at DATETIME,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (service_ticket_id) REFERENCES ServiceTicket(service_ticket_id)
);

CREATE TABLE Assignment (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    service_ticket_id INT,
    employee_id INT,
    start_time DATETIME,
    end_time DATETIME,
    note VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (service_ticket_id) REFERENCES ServiceTicket(service_ticket_id),
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);

CREATE TABLE Attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    date DATE,
    is_present_am BIT,
    is_present_pm BIT,
    note VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    recorded_by INT,
    recorded_at DATETIME,
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);

CREATE TABLE Payroll (
    payroll_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    month INT,
    year INT,
    total_salary DECIMAL(18,2),
    advance_deduction DECIMAL(18,2),
    warranty_deduction DECIMAL(18,2),
    salary_bonus DECIMAL(18,2),
    net_salary DECIMAL(18,2),
    create_at DATETIME,
    status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);

CREATE TABLE Expense (
    expense_id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    description VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    amount DECIMAL(18,2),
    employee_id INT,
    expense_date DATETIME,
    created_at DATETIME,
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);

CREATE TABLE Warranty (
    warranty_id INT AUTO_INCREMENT PRIMARY KEY,
    service_ticket_id INT,
    customer_id INT,
    type VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    cost_dn DECIMAL(18,2),
    cost_tech DECIMAL(18,2),
    cost_customer DECIMAL(18,2),
    approved_by INT,
    supplier_status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    status VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    created_at DATETIME,
    approved_at DATETIME,
    description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    attachment_url VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (service_ticket_id) REFERENCES ServiceTicket(service_ticket_id),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (approved_by) REFERENCES Employee(employee_id)
);

CREATE TABLE WarrantyItem (
    warranty_item_id INT AUTO_INCREMENT PRIMARY KEY,
    warranty_id INT,
    part_id INT,
    description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    cost DECIMAL(18,2),
    note VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (warranty_id) REFERENCES Warranty(warranty_id),
    FOREIGN KEY (part_id) REFERENCES Part(part_id)
);

