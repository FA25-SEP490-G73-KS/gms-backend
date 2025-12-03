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


INSERT INTO unit (name)
VALUES ("cái"),
       ("bộ"),
       ("lít"),
       ("cặp");

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


INSERT INTO unit (
    name
) VALUES
      ('cái'),
      ('bộ'),
      ('lít'),
      ('cặp');

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
    unit
) VALUES
      ('Lọc nhớt động cơ', 10, 1, false, 80000, 120000, 0.00, 50, 0, 10, false, 1,
       'LOCNHODONGCO-VIOS-VN', 1, 1),

      ('Lọc nhớt động cơ', 10, 2, false, 80000, 120000, 0.00, 50, 0, 10, false, 2,
       'LOCNHODONGCO-COROLLA-JP', 2, 1),

      ('Dầu động cơ 10W-40', 10, 1, true, 250000, 320000, 5.00, 100, 5, 20, false, 3,
       'DAUDONGCO10W40-CIVIC-VN', 3, 1),

      ('Bugi đánh lửa', 1, 2, false, 40000, 80000, 0.00, 80, 2, 15, false, 4,
       'BUGIDANHLUA-CITY-JP', 4, 1),

      ('Phanh đĩa trước', 4, 2, false, 300000, 450000, 0.00, 30, 0, 10, false, 5,
       'PHANHDIATRUOC-FOCUS-JP', 5, 1),

      ('Ắc quy GS 12V-45Ah', 7, 2, true, 950000, 1200000, 3.00, 20, 1, 5, false, 1,
       'ACQUYGS12V45AH-VIOS-JP', 6, 1),

      ('Dây curoa tổng', 1, 2, false, 180000, 250000, 0.00, 40, 0, 10, false, 2,
       'DAYCUROATONG-COROLLA-JP', 7, 1),

      ('Lọc gió điều hoà', 6, 3, true, 50000, 90000, 0.00, 60, 0, 15, false, 3,
       'LOCGIODIEUHOA-CIVIC-US', 8, 1),

      ('Gạt mưa trước', 8, 1, true, 30000, 60000, 0.00, 120, 0, 30, false, 4,
       'GATMUATRUOC-CITY-VN', 9, 1),

      ('Đèn pha LED', 8, 3, false, 700000, 950000, 0.00, 15, 0, 5, false, 5,
       'DENPHALED-FOCUS-US', 10, 1),

      ('Cảm biến ABS', 7, 4, false, 450000, 600000, 0.00, 10, 0, 3, false, 1,
       'CAMBIENABS-VIOS-EU',1, 1);


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
           "oacJ4eZMDphCGv4QZDWpEiz7b1gEnJf0XoQQCe3oMMABBQuyhinKHOy7-s-D_XGlY3xKLStG3YlGCDnMpyu4VyHKzIBSaX1RoscoB9Ui2LQDQzOinQOHPTXdpJxnc1nIp07i9j_a6KtcCTy2sjiMGvaErXUmpWLvfHZVFhBD1ssb5iiZW8ad2vDr_NsljqOywIBZVilzEn3mEPbj-F8u0VW4ppJZtpeRrXdhM-w23tNTOSXQ_ufe4CT2p7t3sWyz_ZNpS_xDF5wsCPnrWgn-0iLYW5Q4_5iMcZATIv-gIJ2zGUrHghu1CxrRmtoFpXiCbGZaHuVMHok4DvDAhTvtFRuHWn6-Ys5clnQoIfVMS6Iq6DyL_Va3PVe1tYdwz1zclYda7u3VApI25OrUajek3B06-KIttoCBPN6ibXYFopOz",
                        "GPn24PahgNr7ba0PdIMjVKIIHHcVEAKGBeiKDVDejLXkd09ltqA-OsAPNdRJ9VzxOinEPB4Tta4Sz69gd4Ja1IUw41hXH-H_M81wGTzhrbz-tsr0-2w3S5NC0qgCSAf1ACK9MwmCeM5q-q08rGNZ05csI0_3OQSxVhmS7g9mgYrmWofS_7E0R2MSR43ZAEnJHPv5TkfmgNbOi1vVyGMnItJ01t6iFgbQBDatOfXjkMigZWfcaN-1N1AKDaMmH8fz38auSeXkb4KJlGD0prUjMMccBc_iLgPrNRu5VUyFgKL-fWXAk6MWL0lO5r2nQPf75w4tUSGSiHr9rG09-1IFOMlvE0luJA08PQCqFlv9fWeXcmTWccUqQIAG14I8Kebe9QSeRPHrl7Wla2TNzNYfMa7FGclhXDbSdZQeUW"
        );




