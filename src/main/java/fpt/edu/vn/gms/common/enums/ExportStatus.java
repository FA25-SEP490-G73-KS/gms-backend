package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExportStatus {
    WAITING_PURCHASE("Chờ mua hàng"),
    WAITING_TO_EXPORT("Chờ xuất hàng"),
    EXPORTED("Đã xuất hàng"),
    NONE("Không rõ");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
