package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReceiptPaymentStatus {

    UNPAID("Chưa thanh toán"), // Chưa thanh toán
    PARTIAL_PAID("Thanh toán 1 phần"),   // Thanh toán 1 phần
    PAID("Đã thanh toán đủ");       // Đã thanh toán đủ

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}

