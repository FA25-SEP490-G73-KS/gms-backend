package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceTicketStatus {
    CREATED("Đã tạo"),
    WAITING_FOR_QUOTATION("Chờ báo giá"),
    WAITING_FOR_DELIVERY("Chờ bàn giao xe"),
    COMPLETED("Hoàn thành"),
    CANCELED("Hủy");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
