package fpt.edu.vn.gms.common;


/**
 * Trạng thái của Phiếu Dịch Vụ
 */
public enum ServiceTicketStatus {
    CHO_BAO_GIA("chờ báo giá"),
    DUYET("duyệt"),
    KHONG_DUYET("không duyệt"),
    DANG_SUA_CHUA("đang sửa chữa"),
    CHO_THANH_TOAN("Chờ thanh toán"),
    CHO_CONG_NO("Chờ công nợ"),
    HOAN_THANH("hoàn thành"),
    HUY("Hủy");

    private final String displayName;

    ServiceTicketStatus(String displayName) {
        this.displayName = displayName;
    }
}
