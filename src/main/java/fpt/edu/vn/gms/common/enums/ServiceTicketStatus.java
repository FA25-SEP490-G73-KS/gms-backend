package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceTicketStatus {
    CREATED("Đã tạo"),
    QUOTING("Đang báo giá"),
    QUOTE_CONFIRMED("Khách đã xác nhận báo giá"),
    UNDER_REPAIR("Đang sửa chữa"),
    WAITING_FOR_DELIVERY("Chờ bàn giao xe"),
    COMPLETED("Hoàn thành"),
    CANCELED("Hủy");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
