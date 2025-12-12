package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StockLevelStatus {

    IN_STOCK("Đủ hàng"),
    LOW_STOCK("Sắp hết"),
    OUT_OF_STOCK("Hết hàng");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
