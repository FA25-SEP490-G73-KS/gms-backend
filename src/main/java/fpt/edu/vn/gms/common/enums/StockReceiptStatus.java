package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StockReceiptStatus {

    DRAFT("Nháp"),
    PENDING("Chờ nhập kho"),
    PARTIAL_RECEIVED("Nhập kho một phần"),
    RECEIVED("Đã nhập kho"),
    CANCELLED("Đã hủy");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
