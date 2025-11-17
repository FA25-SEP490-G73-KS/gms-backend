package fpt.edu.vn.gms.common.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Role {
    MANAGER("MANAGER"),          // Quản lý
    SERVICE_ADVISOR("SERVICE_ADVISOR"),  // Cố vấn dịch vụ
    ACCOUNTANT("ACCOUNTANT"),       // Kế toán
    WAREHOUSE("WAREHOUSE");

    String value;
}
