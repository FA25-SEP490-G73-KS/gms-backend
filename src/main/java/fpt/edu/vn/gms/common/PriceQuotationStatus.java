package fpt.edu.vn.gms.common;

public enum PriceQuotationStatus {
    DRAFT,                     // Nháp – cố vấn tạo
    CONFIRMED_BY_WAREHOUSE,    // Kho xác nhận đủ hàng
    CONFIRMED_BY_CUSTOMER,     // Khách xác nhận
    REJECTED_BY_CUSTOMER,      // Khách từ chối
    BASELINE                   // Đã chốt và chuyển sang ticket sửa chữa
}
