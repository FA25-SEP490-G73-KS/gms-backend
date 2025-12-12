package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LedgerVoucherCategory {
    ADVANCE_SALARY("Ứng lương"),
    SALARY_PAYMENT("Trả lương"),
    ELECTRICITY("Tiền điện"),
    SUPPLIER_PAYMENT("Nhà cung cấp"),
    OTHER("Khác");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
