package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StockReceiptStatus {

    PENDING("Chờ nhập kho"),
    PARTIAL_RECEIVED("Nhập kho một phần"),
    RECEIVED("Đã nhập kho"),
    CANCELLED("Đã hủy");

    private final String value;

    @JsonValue
    private String getValue() {
        return this.value;
    }
}
