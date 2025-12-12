package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LedgerVoucherType {

    STOCK_RECEIPT_PAYMENT("Phí linh kiện"),
    SALARY("Tiền lương"),
    SERVICE_FEE("Phí dịch vụ"),
    OTHER("Khác");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
