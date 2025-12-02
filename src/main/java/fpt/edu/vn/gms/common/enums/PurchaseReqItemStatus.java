package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PurchaseReqItemStatus {
    PENDING("Chờ nhập hàng"),
    RECEIVED("Đã nhập hàng");

    private final String value;

    @JsonValue
    private String getValue() {
        return this.value;
    }
}
