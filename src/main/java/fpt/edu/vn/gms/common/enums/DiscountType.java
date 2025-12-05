package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiscountType {

    PERCENT("Phần trăm"),
    FIXED("Số tiền cố định");

    private final String value;

    @JsonValue
    public String getValue() {
      return this.value;
    }
}