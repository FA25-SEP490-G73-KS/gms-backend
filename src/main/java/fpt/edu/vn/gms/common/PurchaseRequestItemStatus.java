package fpt.edu.vn.gms.common;

public enum PurchaseRequestItemStatus {
    PENDING,   // Chờ duyệt
    APPROVED,  // Được duyệt mua
    REJECTED,  // Bị từ chối
    ORDERED,   // Đã được tạo Purchase Order
    RECEIVED   // Đã nhập hàng đầy đủ
}
