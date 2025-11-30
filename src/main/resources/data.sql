USE gms;
-- TimeSlot
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
    employee_id, full_name, gender, date_of_birth, phone, address, hire_date, status, account_id, daily_salary
) VALUES
      (1, 'Nguyễn Văn An', 'Nam', '1985-04-10', '0909123456', '123 Đường A, Quận 1', '2023-05-10 08:00:00', 'ACTIVE', 1, 200000.00),
      (2, 'Trần Minh Đức', 'Nam', '1990-07-15', '0912345678', '456 Đường B, Quận 2', '2023-06-01 08:00:00', 'ACTIVE', 2, 200000.00),
      (3, 'Phạm Thị Hoa', 'Nữ', '1992-01-20', '0987123456', '789 Đường C, Quận 3', '2024-01-15 08:00:00', 'ACTIVE', 3, 200000.00),
      (4, 'Nguyễn Minh Đức', 'Nam', '1992-01-20', '0123456789', '789 Đường C, Quận 3', '2024-01-15 08:00:00', 'ACTIVE', 4, 200000.00),
      (5, 'Nguyễn Minh Đức', 'Nam', '1992-01-20', '0999999999', '789 Đường C, Quận 3', '2024-01-15 08:00:00', 'ACTIVE', null, 200000.00),
      (6, 'Nguyễn Minh Đức', 'Nam', '1992-01-20', '0888888888', '789 Đường C, Quận 3', '2024-01-15 08:00:00', 'ACTIVE', null, 200000.00);

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

INSERT INTO suppliers (name, phone, email, address, is_active) VALUES
                                                                   ('Petrolimex Lubricants', '0123456789', 'contact@petrolimex.com', 'Hà Nội, Việt Nam', true),
                                                                   ('Vinfast Spare Parts', '0987654321', 'support@vinfastauto.com', 'Hải Phòng, Việt Nam', true),
                                                                   ('Motul Vietnam', '0909090909', 'info@motul.com.vn', 'TP. Hồ Chí Minh, Việt Nam', true),
                                                                   ('Castrol BP Vietnam', '0912345678', 'support@castrol.vn', 'Hà Nội, Việt Nam', true),
                                                                   ('Yamaha Genuine Parts', '0933221100', 'parts@yamaha-motor.com.vn', 'Bình Dương, Việt Nam', true);

INSERT INTO unit (name)
VALUES ("cái"),
       ("bộ"),
       ("lít"),
       ("cặp");

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
    unit,
    supplier
) VALUES
      ('Lọc nhớt động cơ',        10, 1, false, 80000, 120000, 0.00, 50, 0, 10, false, 1, 1, 1),
      ('Lọc nhớt động cơ',        10, 2, false, 80000, 120000, 0.00, 50, 0, 10, false, 2, 1, 1),
      ('Dầu động cơ 10W-40',      10, 1, true , 250000, 320000, 5.00, 100, 5, 20, false,3, 1, 1),
      ('Bugi đánh lửa',            1, 2, false, 40000 , 80000 , 0.00, 80, 2, 15, false, 4, 1, 1),
      ('Phanh đĩa trước',          4, 2, false, 300000, 450000, 0.00, 30, 0, 10, false, 5, 1, 1),
      ('Ắc quy GS 12V-45Ah',       7, 2, true , 950000,1200000, 3.00, 20, 1, 5, false, 1, 1, 1),
      ('Dây curoa tổng',           1, 2, false, 180000, 250000, 0.00, 40, 0, 10, false, 2, 1, 1),
      ('Lọc gió điều hoà',         6, 3, true , 50000 , 90000 , 0.00, 60, 0, 15, false, 3, 1, 1),
      ('Gạt mưa trước',            8, 1, true , 30000 , 60000 , 0.00,120, 0, 30, false, 4, 1, 1),
      ('Đèn pha LED',              8, 3, false, 700000, 950000, 0.00, 15, 0, 5, false,5, 1, 1),
      ('Cảm biến ABS',             7, 4, false, 450000, 600000, 0.00, 10, 0, 3, false, 1, 1, 1);

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

INSERT INTO zalo_access_token (access_token, refresh_token)
VALUES (
           "JBN5E2WaidnAlySmB2gsV4cxmZ0kDyy1JlxQ1c0z_XzQriiIEHoz20sEl0q-7PTFMEhgQMO7x7Hx_-1mNMdFKsUgy5r2HUHXMAAGT6jmkZ9pXCa-5Kky73RTlGmgFfvEIVQYULKyeqfbzhLAIZ2lULdmsbzh2zj0SftA1cuAva5FuCnXVYlTJ6JAgt5XN8jDPOkVU4XCbLfMtUTDMKx5O4M8-aTQOVXhG9MCSNDHv4vpffD-5KAPSYQfdqv1QhGFNOM47ZeHy2q_nk5l9WV1A0d2nILhKUadJ8hl47rRt3rWp-WZUm_8PqVRl7PRByXmNVZrPsytmcHtoiruR0h7LMdjvaDsFCbx6CV4Uda6r6fQYTeeJdZgAtljv2L1FkqVVjV1CaT5tHnzgweXU3chU5Mih6DNTzzGLw95T2yviti",
           "1De-78C8yMm2q3mKXmRrR07KEHI56_K60vTI3uSvzYjTacyDjaBLVq_pO5AYLVvtS_mqK8zac6PGiZ9jWI2iNtsLENUgFF1-BBLF3VjcwYejq2GurMQF3I2t40hb5ua28wuH4CW9k20NfNHJbJtHUq25ObwHEybqTAjE0eWRwN88hsDQp0N8RXI2SHlCE_XsDxvCUSiQucWrgNaVrN_X1HZrUYpzGSqrFVX_6-9iyWaLopOVot3vD4syO0ND6SScD"
       );




