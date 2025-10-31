package fpt.edu.vn.gms.common;

public enum PurchaseRequestStatus {
    PENDING,            // Chờ quản lý duyệt (toàn bộ item còn chờ)
    PARTIALLY_APPROVED, // Một số item được duyệt, một số chưa
    APPROVED,           // Tất cả item được duyệt
    REJECTED,           // Từ chối toàn bộ (hoặc hết)
    COMPLETED           // Đã nhập đủ hàng cho toàn bộ item
}
