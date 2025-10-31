package fpt.edu.vn.gms.common;

public enum WarehouseReviewStatus {
    PENDING,    // Chưa được kho xem xét
    CONFIRMED,  // Kho đã duyệt
    REJECTED    // Kho từ chối (ví dụ sai part, không có sẵn, v.v.)
}
