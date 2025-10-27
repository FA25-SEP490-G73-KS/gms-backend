package fpt.edu.vn.gms.common;

public enum ServiceTicketStatus {
    TIEP_NHAN("tiếp nhận"),                // Mới tạo ticket
    DANG_BAO_GIA("đang báo giá"),          // Đang tạo/sửa báo giá
    CHO_DUYET_BAO_GIA("chờ duyệt báo giá"),// Đã gửi khách, chờ phản hồi
    KHONG_DUYET("không duyệt"),            // Khách từ chối báo giá
    CHO_LINH_KIEN("chờ linh kiện"),        // Báo giá đã duyệt, đang đợi nhập kho
    DANG_SUA_CHUA("đang sửa chữa"),        // Đang thực hiện sửa chữa
    CHO_THANH_TOAN("chờ thanh toán"),      // Hoàn tất kỹ thuật, chờ khách thanh toán
    CHO_CONG_NO("chờ công nợ"),            // Khách công nợ
    HOAN_THANH("hoàn thành"),              // Dịch vụ đã hoàn tất
    HUY("hủy");                            // Hủy ticket

    private final String displayName;

    ServiceTicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
