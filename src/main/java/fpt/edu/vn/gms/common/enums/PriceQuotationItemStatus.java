package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PriceQuotationItemStatus {
    AVAILABLE("Có sẵn"),
    OUT_OF_STOCK("Hết hàng"),
    UNKNOWN("Không rõ");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
