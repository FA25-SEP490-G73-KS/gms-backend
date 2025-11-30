package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ManualVoucherType {
    RECEIPT("Phiếu thu"),
    PAYMENT("Phiếu chi"),
    ADVANCE_SALARY("Ứng lương");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
