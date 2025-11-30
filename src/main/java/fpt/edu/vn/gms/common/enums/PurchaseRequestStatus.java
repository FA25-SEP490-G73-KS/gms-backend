package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PurchaseRequestStatus {
    PENDING("Chờ xử lý"),
    COMPLETED("Hoàn thành");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
