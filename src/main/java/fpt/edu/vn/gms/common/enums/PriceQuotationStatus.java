package fpt.edu.vn.gms.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PriceQuotationStatus {
    DRAFT("Nháp"),
    WAITING_WAREHOUSE_CONFIRM("Chờ kho xác nhận"),
    WAREHOUSE_CONFIRMED("Kho đã xác nhận"),
    WAITING_CUSTOMER_CONFIRM("Chờ khách hàng xác nhận"),
    CUSTOMER_CONFIRMED("Khách hàng đã xác nhận"),
    CUSTOMER_REJECTED("Khách hàng từ chối"),
    COMPLETED("Hoàn thành");

    private final String value;
}
