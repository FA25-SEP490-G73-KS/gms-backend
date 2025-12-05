package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExportItemStatus {
    WAITING_TO_RECEIPT("Chờ nhập kho"),
    EXPORTING("Đang xuất hàng"),
    FINISHED("Hoàn thành");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
