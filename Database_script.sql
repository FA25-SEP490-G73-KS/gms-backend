DROP DATABASE IF EXISTS GarageManagement;
CREATE DATABASE GarageManagement CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE GarageManagement;

-- Role must be created before Account
CREATE TABLE role
(
    role_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);

CREATE TABLE account
(
    account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone      VARCHAR(20) UNIQUE,
    role_id    BIGINT NOT NULL,
    password   VARCHAR(100),
    FOREIGN KEY (role_id) REFERENCES Role (role_id)
);

CREATE TABLE customer
(
    customer_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    phone         VARCHAR(50),
    zalo_id       VARCHAR(50),
    address       VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    customer_type VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    loyalty_level VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);

CREATE TABLE vehicle
(
    vehicle_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id   BIGINT NOT NULL,
    license_plate VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    brand         VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    model         VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    year          INT,
    vin           VARCHAR(50),
    FOREIGN KEY (customer_id) REFERENCES Customer (customer_id)
);


CREATE TABLE employee
(
    employee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    position    VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    phone       VARCHAR(20) UNIQUE,
    salary_base DECIMAL(18, 2),
    paid_amount DECIMAL(18, 2),
    hire_date   DATETIME,
    status      VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);

CREATE TABLE category
(
    category_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    brand         VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);

CREATE TABLE part
(
    part_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    part_name         VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    supplier          VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    cost_price        DECIMAL(18, 2),
    sell_price        DECIMAL(18, 2),
    quantity_in_stock INT,
    status            VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    reorder_level     INT,
    last_updated      DATETIME,
    category_id       BIGINT,
    FOREIGN KEY (category_id) REFERENCES Category (category_id)
);

-- TimeSlot table required by Appointment
CREATE TABLE time_slot
(
    timeSlotId  BIGINT AUTO_INCREMENT PRIMARY KEY,
    label       VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci UNIQUE NOT NULL,
    startTime   TIME                                                                 NOT NULL,
    endTime     TIME                                                                 NOT NULL,
    maxCapacity INT                                                                  NOT NULL
);

-- Appointment entity uses snake/camel mix as per annotations
CREATE TABLE appointment
(
    appointmentId   BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT                                                        NOT NULL,
    vehicle_id      BIGINT                                                        NOT NULL,
    time_slot_id    BIGINT                                                        NOT NULL,
    appointmentDate DATE                                                          NOT NULL,
    serviceType     VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    status          VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL,
    description     VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    created_at      DATETIME                                                      NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customer (customer_id),
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle (vehicle_id),
    FOREIGN KEY (time_slot_id) REFERENCES time_slot (timeSlotId)
);

CREATE TABLE service_ticket
(
    service_ticket_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id    BIGINT,
    customer_id       BIGINT,
    vehicle_id        BIGINT,
    status            VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    notes             CHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    created_at        DATETIME,
    delivery_at       DATETIME,
    FOREIGN KEY (appointment_id) REFERENCES appointment (appointmentId),
    FOREIGN KEY (customer_id) REFERENCES Customer (customer_id),
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle (vehicle_id)
);

CREATE TABLE payment
(
    payment_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id      BIGINT,
    payment_method VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    amount         DECIMAL(18, 2),
    payment_date   DATETIME,
    status         VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    reference_code VARCHAR(50),
    FOREIGN KEY (ticket_id) REFERENCES service_ticket (service_ticket_id)
);

CREATE TABLE price_quotation
(
    price_quotation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_ticket_id  BIGINT,
    total_amount       DECIMAL(18, 2),
    status             VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    created_at         DATETIME,
    FOREIGN KEY (service_ticket_id) REFERENCES service_ticket (service_ticket_id)
);

CREATE TABLE price_quotation_item
(
    price_quotation_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    price_quotation_id      BIGINT,
    part_id                 BIGINT,
    description             TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    quantity                INT,
    total_price             DECIMAL(18, 2),
    part_status             VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    update_at               DATETIME,
    account_id              INT,
    update_status           VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (price_quotation_id) REFERENCES price_quotation (price_quotation_id),
    FOREIGN KEY (part_id) REFERENCES Part (part_id)
);

CREATE TABLE purchase_request
(
    purchase_request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    price_quotation_id  BIGINT,
    part_id             BIGINT,
    supplier            VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    expected_date       DATETIME,
    status              VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    created_at          DATETIME,
    FOREIGN KEY (price_quotation_id) REFERENCES price_quotation (price_quotation_id),
    FOREIGN KEY (part_id) REFERENCES Part (part_id)
);

CREATE TABLE inventory_transaction
(
    inventory_transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    part_id                  BIGINT,
    type                     VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    quantity                 INT,
    service_ticket_id        BIGINT,
    purchase_request_id      BIGINT,
    created_at               DATETIME,
    FOREIGN KEY (part_id) REFERENCES Part (part_id),
    FOREIGN KEY (service_ticket_id) REFERENCES service_ticket (service_ticket_id),
    FOREIGN KEY (purchase_request_id) REFERENCES purchase_request (purchase_request_id)
);


CREATE TABLE debt
(
    debt_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id       BIGINT,
    service_ticket_id BIGINT,
    amount_due        DECIMAL(18, 2),
    due_date          DATE,
    paid_amount       DECIMAL(18, 2),
    status            VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    updated_at        DATETIME,
    FOREIGN KEY (customer_id) REFERENCES Customer (customer_id),
    FOREIGN KEY (service_ticket_id) REFERENCES service_ticket (service_ticket_id)
);

CREATE TABLE assignment
(
    assignment_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_ticket_id BIGINT,
    employee_id       BIGINT,
    start_time        DATETIME,
    end_time          DATETIME,
    note              VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (service_ticket_id) REFERENCES service_ticket (service_ticket_id),
    FOREIGN KEY (employee_id) REFERENCES Employee (employee_id)
);

CREATE TABLE attendance
(
    attendance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id   BIGINT,
    date          DATE,
    is_present_am BIT,
    is_present_pm BIT,
    note          VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    recorded_by   INT,
    recorded_at   DATETIME,
    FOREIGN KEY (employee_id) REFERENCES Employee (employee_id)
);

CREATE TABLE payroll
(
    payroll_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id        BIGINT,
    month              INT,
    year               INT,
    total_salary       DECIMAL(18, 2),
    advance_deduction  DECIMAL(18, 2),
    warranty_deduction DECIMAL(18, 2),
    salary_bonus       DECIMAL(18, 2),
    net_salary         DECIMAL(18, 2),
    create_at          DATETIME,
    status             VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (employee_id) REFERENCES Employee (employee_id)
);

CREATE TABLE expense
(
    expense_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    type         VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    description  VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    amount       DECIMAL(18, 2),
    employee_id  BIGINT,
    expense_date DATETIME,
    created_at   DATETIME,
    FOREIGN KEY (employee_id) REFERENCES Employee (employee_id)
);

CREATE TABLE warranty
(
    warranty_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_ticket_id BIGINT,
    customer_id       BIGINT,
    type              VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    cost_dn           DECIMAL(18, 2),
    cost_tech         DECIMAL(18, 2),
    cost_customer     DECIMAL(18, 2),
    approved_by       INT,
    supplier_status   VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    status            VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    created_at        DATETIME,
    approved_at       DATETIME,
    description       VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    attachment_url    VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (service_ticket_id) REFERENCES service_ticket (service_ticket_id),
    FOREIGN KEY (customer_id) REFERENCES Customer (customer_id)
);

CREATE TABLE warranty_item
(
    warranty_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warranty_id      BIGINT,
    part_id          BIGINT,
    description      VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    cost             DECIMAL(18, 2),
    note             VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (warranty_id) REFERENCES Warranty (warranty_id),
    FOREIGN KEY (part_id) REFERENCES Part (part_id)
);

