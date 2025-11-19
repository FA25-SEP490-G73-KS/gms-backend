USE gms;
-- TimeSlot
INSERT INTO time_slot(label, start_time, end_time, max_capacity)
VALUES
    ('08:00-10:00','08:00:00','10:00:00',3),
    ('10:00-12:00','10:00:00','12:00:00',3),
    ('13:00-15:00','13:00:00','15:00:00',3),
    ('15:00-17:00','15:00:00','17:00:00',3);

-- === SERVICE TYPES ===
INSERT INTO service_type (name) VALUES ('Sơn');
INSERT INTO service_type (name) VALUES ('Sửa chữa');
INSERT INTO service_type (name) VALUES ('Bảo dưỡng');
INSERT INTO service_type (name) VALUES ('Bảo hành');

INSERT INTO role (role_id, role_name)
VALUES
    (1, 'ADMIN'),
    (2, 'USER');

INSERT INTO employee (
    employee_id, full_name, gender, date_of_birth, phone, address, hire_date, status
) VALUES
      (1, 'Nguyễn Văn An', 'Nam', '1985-04-10', '0909123456', '123 Đường A, Quận 1', '2023-05-10 08:00:00', 'ACTIVE'),
      (2, 'Trần Minh Đức', 'Nam', '1990-07-15', '0912345678', '456 Đường B, Quận 2', '2023-06-01 08:00:00', 'ACTIVE'),
      (3, 'Phạm Thị Hoa', 'Nữ', '1992-01-20', '0987123456', '789 Đường C, Quận 3', '2024-01-15 08:00:00', 'ACTIVE');

INSERT INTO account (account_id, phone, password, role, employee_id, is_active)
VALUES
    (1, '0909123456', '$2a$10$1405F5A.2xsmn98bZYT3GeeIUcKvpaCfAK.9iqJaOQElN9y33Tagy', 'SERVICE_ADVISOR', 1, 1),
    (2, '0909988776', '$2a$10$1405F5A.2xsmn98bZYT3GeeIUcKvpaCfAK.9iqJaOQElN9y33Tagy', 'MANAGER', 2, 1);

INSERT INTO part_category (name)
VALUES
    ('Dầu, dung dịch'),
    ('Lọc'),
    ('Đèn / Phanh'),
    ('Điện'),
    ('Phụ kiện tiêu hao');

INSERT INTO part (
    part_name,
    market,
    is_universal,
    purchase_price,
    selling_price,
    discount_rate,
    quantity_in_stock,
    reserved_quantity,
    reorder_level,
    special_part
) VALUES
      ('Lọc nhớt động cơ', 'VN', false, 80000, 120000, 0.00, 50, 0, 10, true),
      ('Lọc nhớt động cơ', 'VN', false, 80000, 120000, 0.00, 50, 0, 10, true),
      ('Dầu động cơ 10W-40', 'VN', true, 250000, 320000, 5.00, 100, 5, 20, true),
      ('Bugi đánh lửa', 'JP', false, 40000, 80000, 0.00, 80, 2, 15, true),
      ('Phanh đĩa trước', 'JP', false, 300000, 450000, 0.00, 30, 0, 10, true),
      ('Ắc quy GS 12V-45Ah', 'JP', true, 950000, 1200000, 3.00, 20, 1, 5, true),
      ('Dây curoa tổng', 'VN', false, 180000, 250000, 0.00, 40, 0, 10, true),
      ('Lọc gió điều hoà', 'US', true, 50000, 90000, 0.00, 60, 0, 15, true),
      ('Gạt mưa trước', 'VN', true, 30000, 60000, 0.00, 120, 0, 30, true),
      ('Đèn pha LED', 'EU', false, 700000, 950000, 0.00, 15, 0, 5, true),
      ('Cảm biến ABS', 'JP', false, 450000, 600000, 0.00, 10, 0, 3, true);

INSERT INTO customer (full_name, phone, zalo_id, address, customer_type, loyalty_level)
VALUES
    ('Nguyễn Văn A', '0123456789', 'zalo_001', '123 Nguyễn Trãi, Hà Nội', 'CA_NHAN', 'BRONZE'),
    ('Trần Thị B', '0123987654', 'zalo_002', '45 Lê Lợi, TP.HCM', 'CA_NHAN', 'BRONZE'),
    ('Lê Văn C', '0987654321', 'zalo_003', '78 Hai Bà Trưng, Đà Nẵng', 'CA_NHAN', 'GOLD');

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

