package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayrollStatus {

    PENDING_MANAGER_APPROVAL("Chờ quản lý duyệt"),
    APPROVED("Duyệt"),
    PAID("Đã thanh toán");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
