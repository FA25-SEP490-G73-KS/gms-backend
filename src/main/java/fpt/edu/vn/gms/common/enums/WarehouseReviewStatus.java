package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WarehouseReviewStatus {
    PENDING("Chờ duyệt"),
    CONFIRMED("Đã duyệt"),
    REJECTED("Từ chối");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
