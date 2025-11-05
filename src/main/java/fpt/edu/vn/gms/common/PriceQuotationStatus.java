package fpt.edu.vn.gms.common;

public enum PriceQuotationStatus {
    DRAFT,                    // Nháp
    WAITING_WAREHOUSE_CONFIRM, // Chờ kho xác nhận
    WAREHOUSE_CONFIRMED,       // Kho đã xác nhận
    WAITING_CUSTOMER_CONFIRM,  // Chờ khách xác nhận
    CUSTOMER_CONFIRMED,        // Khách đã xác nhận
    CUSTOMER_REJECTED,         // Khách từ chối
    COMPLETED                  // Hoàn thành
}
