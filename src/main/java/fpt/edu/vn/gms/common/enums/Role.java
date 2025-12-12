package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Role {
    MANAGER("Quản lý"),
    SERVICE_ADVISOR("Cố vấn dịch vụ"),
    ACCOUNTANT("Kế toán"),
    WAREHOUSE("Nhân viên kho"),
    TECHNICIAN("Kỹ thuật viên");

    String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
