package fpt.edu.vn.gms.common;

public enum PurchaseOrderStatus {
    PENDING,     // Đang chờ nhận hàng
    PARTIALLY_RECEIVED, // Nhận một phần
    RECEIVED,    // Nhập đủ
    CANCELLED
}
