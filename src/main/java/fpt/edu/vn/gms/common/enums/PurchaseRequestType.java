package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PurchaseRequestType {

    QUOTATION("Theo báo giá"),
    ADDITIONAL_PART("Bổ sung kho");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
