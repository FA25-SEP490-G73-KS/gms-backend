package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ManagerReviewStatus {
    PENDING("Chờ xác nhận"),
    APPROVED("Xác nhận"),
    REJECTED("Từ chối");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
